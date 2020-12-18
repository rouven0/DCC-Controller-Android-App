package com.traincon.modelleisenbahn_controller;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.traincon.CBusMessage.CBusMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static android.content.ContentValues.TAG;

public class BoardManager implements Parcelable {
    public final String host;
    public final int port;
    public Socket mainSocket;
    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;

    public BoardManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    protected BoardManager(Parcel in) {
        host = in.readString();
        port = in.readInt();
    }

    public static final Creator<BoardManager> CREATOR = new Creator<BoardManager>() {
        @Override
        public BoardManager createFromParcel(Parcel in) {
            return new BoardManager(in);
        }

        @Override
        public BoardManager[] newArray(int size) {
            return new BoardManager[size];
        }
    };

    public void connect() {
        Log.v(TAG, "Trying to connect");
        //disconnect in case of failed destruction
        try {
            disconnect();
        } catch (IOException | NullPointerException ignore) {}
        Thread thread = new Thread(() -> {
            try {
                mainSocket = new Socket();
                mainSocket.connect(new InetSocketAddress(host, port));
                socketInputStream = new DataInputStream(mainSocket.getInputStream());
                socketOutputStream = new DataOutputStream(mainSocket.getOutputStream());
                Log.v(TAG, "connected");
                Thread.sleep(500);
                clear();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void clear() throws InterruptedException {
        receive(18);
    }

    public void disconnect() throws IOException {
        mainSocket.close();
    }

    public void send(final String message) {
        Thread thread = new Thread(() -> {
            try {
                byte[] bMessage = message.getBytes(StandardCharsets.UTF_8);
                socketOutputStream.write(bMessage);
                Log.v(TAG, "send: "+ message + getReceivedCBusMessage(message).getEvent());
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                Log.v(TAG, "Failed to send a frame: "+ message + getReceivedCBusMessage(message).getEvent());
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

        final Thread thread = new Thread(() -> {
            for (int i = 0; i < rawMessage.length; i++) {
                try {
                    rawMessage[i] = socketInputStream.readByte();
                } catch (IOException | NullPointerException ignored) {
                }
                message[0] += (char) rawMessage[i];
            }
        });
        thread.start();
        thread.join();
        Log.v(TAG, "received: length="+ length + " message: "+ message[0]);
        return message[0];
    }

    public DataInputStream getSocketInputStream() {
        return socketInputStream;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(host);
        dest.writeInt(port);
    }
}