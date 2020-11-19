package com.traincon.modelleisenbahn_controller;


import android.util.Log;

import com.traincon.CBusMessage.CBusMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class BoardManager {
    public final String host;
    public final int port;
    public Socket mainSocket;
    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;

    public BoardManager(String host, int port) {
        this.host = host;
        this.port = port;
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
                    receive(18); // CLear the welcome Message
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

    public void send(final String message) {
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

    public CBusMessage getReceivedCBusMessage(String receivedFrame){
        String event = receivedFrame.substring(7,9);
        String[] data = new String[receivedFrame.substring(9).length()/2];
        for(int i=0; i<data.length; i++){
            data[i] = receivedFrame.substring(9+(2*i), 11+(2*i));
        }
        return new CBusMessage(event, data);
    }

    public String receive(int length) throws InterruptedException {
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
        Log.d("0", "receive: " + message[0]);
        return message[0];
    }

    public DataInputStream getSocketInputStream() {
        return socketInputStream;
    }
}