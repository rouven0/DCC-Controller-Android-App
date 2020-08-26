package com.traincon.modelleisenbahn_controller;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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

     //Verbindng zur Platine auf dem Brett aufbauen und Willkommensnachricht auslesen
    public void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainSocket = new Socket();
                    mainSocket.connect(new InetSocketAddress(host, port));
                    socketInputStream = new DataInputStream(mainSocket.getInputStream());
                    socketOutputStream = new DataOutputStream(mainSocket.getOutputStream());
                    Log.d(TAG, receive(18));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
       //Log.d(TAG, "connect: "+recieve());
    }

    //Weiche auf dem Brett stellen
    public void setSwitch(int targetSwitch, int targetState){
        switchStates[targetSwitch]=targetState;
        Log.d(TAG, "te"); //Todo durch send() ersetzen
        Log.d(TAG, "setSwitch: Weiche: "+ (targetSwitch + 1) +" auf Status: "+ targetState);
    }

    //Gleisaschnitt auf dem Brett umschalten
    public void setSection(int targetSection, int targetState){
        sectionStates[targetSection]=targetState;
        send("test");
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
    protected void requestSwitchStates(){
        send(":Y"); //Todo ersetzen
        receive(18);
    }

    //String an das Brett senden
    private void send(final String message){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketOutputStream.writeChars(message);
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    //String vom Brett empfangen
    private String receive(int lenght){
        final String[] message = new String[]{""};
        final byte[] rawMessage = new byte[lenght];

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<rawMessage.length; i++){
                    try {
                        rawMessage[i]=socketInputStream.readByte();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    message[0] = message[0]+Byte.decode((Byte.toString(rawMessage[i])));
                }
            }
        });
        thread.start();
        Log.d(TAG, "m: "+message[0]);
        return message[0];
    }
}
