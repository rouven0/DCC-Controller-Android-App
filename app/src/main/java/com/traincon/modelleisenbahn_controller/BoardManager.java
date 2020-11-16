package com.traincon.modelleisenbahn_controller;

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

import static android.content.ContentValues.TAG;

public class BoardManager {
    public final boolean[] switchStates = new boolean[16];
    public final boolean[] sectionStates = new boolean[13];
    public final String host;
    public final int port;
    private final CBusAsciiMessageBuilder cBusAsciiMessageBuilder;
    public boolean lightState = false;
    public Socket mainSocket;
    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;

    public BoardManager(String host, int port) {
        this.host = host;
        this.port = port;
        Arrays.fill(switchStates, false);
        Arrays.fill(sectionStates, false);
        cBusAsciiMessageBuilder = new CBusAsciiMessageBuilder(); //Gerätenummer wird zur canId
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
            send(cBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));
            Log.d(TAG, "setSwitch: "+cBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));
        } else {
            send(cBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));
            Log.d(TAG, "setSwitch: "+cBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));
        }
    }

    public void setSection(int targetSection, boolean targetState) {
        sectionStates[targetSection] = targetState;
        if (targetState) {
            send(cBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "24", "0" + Integer.toHexString(targetSection).toUpperCase()})));

        } else {
            send(cBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "24", "0" + Integer.toHexString(targetSection).toUpperCase()})));

        }
    }

    public void setLightOn() {
        lightState = true;
        send(cBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "24", "0D"})));
    }

    public void setLightOff() {
        lightState = false;
        send(cBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "24", "0D"})));
    }

    public boolean getLightState(){
        return lightState;
    }

    protected void requestSwitchStates() throws InterruptedException, IOException {
        //get the node variable
        send(cBusAsciiMessageBuilder.build(new CBusMessage("NVRD", new String[]{"00", "65", "03"})));
        String receivedSwitchStates_0 = receive(CBusAsciiMessageBuilder.getExpectedMessageLength(3));
        send(cBusAsciiMessageBuilder.build(new CBusMessage("NVRD", new String[]{"00", "65", "04"})));
        String receivedSwitchStates_1 = receive(CBusAsciiMessageBuilder.getExpectedMessageLength(3));
        //buffer
        try {
            receive(socketInputStream.available());
        } catch (NullPointerException ignore) {
        }
        //get the values
        receivedSwitchStates_0 = receivedSwitchStates_0.substring(15, 17);
        receivedSwitchStates_1 = receivedSwitchStates_1.substring(15, 17);
        StringBuilder receivedSwitchStatesBinary_0;
        StringBuilder receivedSwitchStatesBinary_1;
        try {
            //values to binary
            receivedSwitchStatesBinary_0 = new StringBuilder(Integer.toBinaryString(Integer.parseInt(receivedSwitchStates_0, 16)));
            receivedSwitchStatesBinary_1 = new StringBuilder(Integer.toBinaryString(Integer.parseInt(receivedSwitchStates_1, 16)));
            while (receivedSwitchStatesBinary_0.length() < 8) {
                receivedSwitchStatesBinary_0.insert(0, "0");
            }
            while (receivedSwitchStatesBinary_1.length() < 8) {
                receivedSwitchStatesBinary_1.insert(0, "0");
            }
            receivedSwitchStatesBinary_0.reverse();
            receivedSwitchStatesBinary_1.reverse();
            //apply the switch states
            for (int i = 0; i < receivedSwitchStatesBinary_0.length(); i++) {
                switchStates[i] = String.valueOf(receivedSwitchStatesBinary_0.charAt(i)).equals("1");
            }
            for (int i = 0; i < receivedSwitchStatesBinary_1.length(); i++) {
                switchStates[i + receivedSwitchStatesBinary_0.length()] = String.valueOf(receivedSwitchStatesBinary_1.charAt(i)).equals("1");
            }
        } catch (NumberFormatException ignore) {
        }
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

    private String receive(int length) throws InterruptedException {
        final String[] message = new String[]{""};
        final byte[] rawMessage = new byte[length];

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
}