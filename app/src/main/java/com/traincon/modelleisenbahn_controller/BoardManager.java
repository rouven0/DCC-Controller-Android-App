package com.traincon.modelleisenbahn_controller;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class BoardManager {
    public final int[] switchStates = new int[16];
    public final int[] sectionStates = new int[13];
    public boolean lightState = false;
    public final String devId;
    public final String host;
    public final int port;
    public Socket mainSocket;
    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;


    public BoardManager(String devid, String hst, int prt){
        devId = devid;
        host=hst;
        port=prt;
        Arrays.fill(switchStates, 0);
        Arrays.fill(sectionStates, 0);
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
                    Log.d(TAG, devId);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    //Weiche auf dem Brett stellen
    public void setSwitch(int targetSwitch, int targetState) {
        switchStates[targetSwitch]=targetState;
        send(":S0166N"+(99 - targetState)+"0011230"+Integer.toHexString(targetSwitch).toUpperCase()+";");
        Log.d(TAG, "setSwitch: Weiche: "+ (targetSwitch + 1) +" auf Status: "+ targetState);
    }

    //Gleisaschnitt auf dem Brett umschalten
    public void setSection(int targetSection, int targetState){
        sectionStates[targetSection]=targetState;
        send(":S0166N"+(99 - targetState)+"0011240"+Integer.toHexString(targetSection).toUpperCase()+";");
        Log.d(TAG, "setSection: Gleisabschnitt: "+ (targetSection + 1) +" auf Status: "+ targetState);
    }

    //Licht umschalten
    public void setLight(){
        if(!lightState){
            lightState=true;
            Log.d(TAG, "setLight: Licht angeschaltet");
            send(":S0166N980011240D;");
        }
        else{
            lightState=false;
            Log.d(TAG, "setLight: Licht ausgeschaltet");
            send(":S0166N990011240D;");
        }
    }

    //Weichenpositionen abfragen
    //Wird in ScreenFragment.update aufgerufen
    protected void requestSwitchStates() throws InterruptedException, IOException {
        //Abfragen
        send(":S0166N71006503;");
        String receivedSwitchStates_0 = receive(18);
        send(":S0166N71006504;");
        String receivedSwitchStates_1 = receive(18);
        //Datenpuffer
        try {
            receive(socketInputStream.available());
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        //Werte übertragen
        receivedSwitchStates_0 = receivedSwitchStates_0.substring(15,17);
        receivedSwitchStates_1 = receivedSwitchStates_1.substring(15,17);
        StringBuilder receivedSwitchStatesBinary_0;
        StringBuilder receivedSwitchStatesBinary_1;
        try {
            //Zur Binärzahl
            receivedSwitchStatesBinary_0 = new StringBuilder(Integer.toBinaryString(Integer.parseInt(receivedSwitchStates_0, 16)));
            receivedSwitchStatesBinary_1 = new StringBuilder(Integer.toBinaryString(Integer.parseInt(receivedSwitchStates_1, 16)));
            while (receivedSwitchStatesBinary_0.length()<8){
                receivedSwitchStatesBinary_0.insert(0, "0");
            }
            while (receivedSwitchStatesBinary_1.length()<8){
                receivedSwitchStatesBinary_1.insert(0, "0");
            }
            //Invertieren
            receivedSwitchStatesBinary_0.reverse();
            receivedSwitchStatesBinary_1.reverse();
            //Übertragen
            for(int i=0; i<receivedSwitchStatesBinary_0.length(); i++){
                switchStates[i]=Integer.parseInt(String.valueOf(receivedSwitchStatesBinary_0.charAt(i)));
            }
            for(int i=0; i<receivedSwitchStatesBinary_1.length(); i++){
                switchStates[i+receivedSwitchStatesBinary_0.length()]=Integer.parseInt(String.valueOf(receivedSwitchStatesBinary_1.charAt(i)));
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
    }


    //Funktionen aus dem Menü
    //Weichen 3 Runden
    protected void switchPreset_3r(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<switchStates.length; i++){
                    if(i==0 || (2<i && i<10)){
                        setSwitch(i, 1);
                    }
                    else{
                        setSwitch(i, 0);
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

    //Weichen auf mitte
    protected void switchSetToCenter(){
        send(":S0166N9900112310;");
    }

    //Weichen nachjustieren
    protected void switchCalibrate(){
        send(":S0166N9900112311;");
    }

    //Gleise 3 runden
    protected void sectionPreset_3r(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<sectionStates.length; i++){
                    if((1<i && i<4) || (5<i && i<8) || (9<i && i<12)){
                        setSection(i, 1);
                    }
                    else{
                        setSection(i, 0);
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

    //Alle Gleise ausschalten
    protected void sectionsAllOff(){
        send(":S0166N9900112410;");
        Arrays.fill(sectionStates, 0);
    }

    //String an das Brett senden
    private void send(final String message){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bMessage = message.getBytes(StandardCharsets.UTF_8);
                    socketOutputStream.write(bMessage);
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    //String vom Brett empfangen
    private String receive(int lenght) throws InterruptedException {
        final String[] message = new String[]{""};
        final byte[] rawMessage = new byte[lenght];

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<rawMessage.length; i++){
                    try {
                        rawMessage[i]=socketInputStream.readByte();
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    message[0] += (char)rawMessage[i];
                }
            }
        });
        thread.start();
        thread.join();
        return message[0];
    }
}
