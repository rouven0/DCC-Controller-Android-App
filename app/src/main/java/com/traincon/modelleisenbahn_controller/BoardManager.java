package com.traincon.modelleisenbahn_controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.traincon.CBusMessage.CBusMessage;
import com.traincon.CBusMessage.CBusAsciiMessageBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import androidx.preference.PreferenceManager;

import static android.content.ContentValues.TAG;

public class BoardManager {
    public final boolean[] switchStates = new boolean[16];
    public final boolean[] sectionStates = new boolean[13];
    public final String devId;
    public final String host;
    public final int port;
    private final CBusAsciiMessageBuilder cBusAsciiMessageBuilder;
    public boolean lightState = false;
    public Socket mainSocket;
    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;

    private final Context context;
    public BoardManager(Context context,String devid, String hst, int prt) {
        devId = devid;
        host = hst;
        port = prt;
        this.context = context;
        Arrays.fill(switchStates, false);
        Arrays.fill(sectionStates, false);
        cBusAsciiMessageBuilder = new CBusAsciiMessageBuilder(getCanId()); //Gerätenummer wird zur CANID
    }

    public void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainSocket = new Socket();
                    mainSocket.connect(new InetSocketAddress(host, port));
                    socketInputStream = new DataInputStream(mainSocket.getInputStream());
                    socketOutputStream = new DataOutputStream(mainSocket.getOutputStream());
                    receive(18);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void disconnect() throws IOException {
        mainSocket.close();
    }

    public void setSwitch(int targetSwitch, boolean targetState) {
        switchStates[targetSwitch] = targetState;
        if (targetState) {
            send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_4_ASON, new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));
        } else {
            send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_4_ASOF, new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));
        }
        Log.d(TAG, "setSwitch: Weiche: " + (targetSwitch + 1) + " auf Status: " + targetState);
    }

    public void setSection(int targetSection, boolean targetState) {
        sectionStates[targetSection] = targetState;
        if (targetState) {
            send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_4_ASON, new String[]{"00", "11", "24", "0" + Integer.toHexString(targetSection).toUpperCase()})));

        } else {
            send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_4_ASOF, new String[]{"00", "11", "24", "0" + Integer.toHexString(targetSection).toUpperCase()})));

        }
        Log.d(TAG, "setSection: Gleisabschnitt: " + (targetSection + 1) + " auf Status: " + targetState);
    }

    //HARCODED!
    public void setLight() {
        if (!lightState) {
            lightState = true;
            Log.d(TAG, "setLight: Licht angeschaltet");
            send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_4_ASON, new String[]{"00", "11", "24", "0D"})));
        } else {
            lightState = false;
            Log.d(TAG, "setLight: Licht ausgeschaltet");
            send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_4_ASOF, new String[]{"00", "11", "24", "0D"})));

        }
    }

    //Called in ScreenFragment.update()
    protected void requestSwitchStates() throws InterruptedException, IOException {
        //Abfragen
        send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_3_NVRD, new String[]{"00", "65", "03"})));
        String receivedSwitchStates_0 = receive(CBusAsciiMessageBuilder.EML_3);
        send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_3_NVRD, new String[]{"00", "65", "04"})));
        String receivedSwitchStates_1 = receive(CBusAsciiMessageBuilder.EML_3);
        //Datenpuffer
        try {
            receive(socketInputStream.available());
        } catch (NullPointerException ignore) {
        }
        //Werte übertragen
        receivedSwitchStates_0 = receivedSwitchStates_0.substring(15, 17);
        receivedSwitchStates_1 = receivedSwitchStates_1.substring(15, 17);
        StringBuilder receivedSwitchStatesBinary_0;
        StringBuilder receivedSwitchStatesBinary_1;
        try {
            //Zur Binärzahl
            receivedSwitchStatesBinary_0 = new StringBuilder(Integer.toBinaryString(Integer.parseInt(receivedSwitchStates_0, 16)));
            receivedSwitchStatesBinary_1 = new StringBuilder(Integer.toBinaryString(Integer.parseInt(receivedSwitchStates_1, 16)));
            while (receivedSwitchStatesBinary_0.length() < 8) {
                receivedSwitchStatesBinary_0.insert(0, "0");
            }
            while (receivedSwitchStatesBinary_1.length() < 8) {
                receivedSwitchStatesBinary_1.insert(0, "0");
            }
            //Invertieren
            receivedSwitchStatesBinary_0.reverse();
            receivedSwitchStatesBinary_1.reverse();
            //Übertragen
            for (int i = 0; i < receivedSwitchStatesBinary_0.length(); i++) {
                switchStates[i] = String.valueOf(receivedSwitchStatesBinary_0.charAt(i)).equals("1");
            }
            for (int i = 0; i < receivedSwitchStatesBinary_1.length(); i++) {
                switchStates[i + receivedSwitchStatesBinary_0.length()] = String.valueOf(receivedSwitchStatesBinary_1.charAt(i)).equals("1");
            }
        } catch (NumberFormatException ignore) {
        }
    }

    //Menu features(Hardcoded)
    protected void switchPreset_3r() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < switchStates.length; i++) {
                    if (i == 0 || (2 < i && i < 10)) {
                        setSwitch(i, true);
                    } else {
                        setSwitch(i, false);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    protected void switchSetToCenter() {
        send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_4_ASON, new String[]{"00", "11", "23", "10"})));
    }

    protected void switchCalibrate() {
        send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_4_ASON, new String[]{"00", "11", "23", "11"})));
    }

    protected void sectionPreset_3r() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < sectionStates.length; i++) {
                    if ((1 < i && i < 4) || (5 < i && i < 8) || (9 < i && i < 12)) {
                        setSection(i, true);
                    } else {
                        setSection(i, false);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    protected void sectionsAllOff() {
        send(cBusAsciiMessageBuilder.build(new CBusMessage(CBusMessage.EVENT_4_ASON, new String[]{"00", "11", "24", "10"})));
        Arrays.fill(sectionStates, false);
        lightState = false;
    }

    private void send(final String message) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bMessage = message.getBytes(StandardCharsets.UTF_8);
                    socketOutputStream.write(bMessage);
                } catch (IOException | NullPointerException ignored) {
                }

            }
        });
        thread.start();
    }

    private String receive(int lenght) throws InterruptedException {
        final String[] message = new String[]{""};
        final byte[] rawMessage = new byte[lenght];

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < rawMessage.length; i++) {
                    try {
                        rawMessage[i] = socketInputStream.readByte();
                    } catch (IOException | NullPointerException ignored) {
                    }
                    message[0] += (char) rawMessage[i];
                }
            }
        });
        thread.start();
        thread.join();
        return message[0];
    }

    private String getCanId() {
        String canId = "";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (devId) {
            case "1":
                canId = sharedPreferences.getString("canid_1", null);
                break;
            case "2":
                canId = sharedPreferences.getString("canid_2", null);
                break;
            case "3":
                canId = sharedPreferences.getString("canid_3", null);
                break;
            case "4":
                canId = sharedPreferences.getString("canid_4", null);
                break;
        }
        return canId;
    }
}