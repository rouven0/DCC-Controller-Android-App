package com.traincon.modelleisenbahn_controller;

import android.os.Handler;
import android.os.Looper;
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
import java.util.ArrayList;
import java.util.List;

public class BoardManager implements Parcelable {
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
    public final String host;
    public final int port;
    private final List<CBusMessage> receivedMessages = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final String TAG = "DCC-Controller: Network";
    public Runnable getMessagesRunnable;
    private Socket mainSocket;
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

    public void connect() {
        Log.v(TAG, "Trying to connect");
        new Thread(() -> {
            try {
                mainSocket = new Socket();
                mainSocket.connect(new InetSocketAddress(host, port));
                socketInputStream = new DataInputStream(mainSocket.getInputStream());
                socketOutputStream = new DataOutputStream(mainSocket.getOutputStream());
                Log.v(TAG, "connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void disconnect() {
        try {
            mainSocket.close();
            Log.v(TAG, "disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStartFrameListener() {
        getMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    //Get the message
                    String receivedCharacters = receive(socketInputStream.available());
                    //Divide it
                    String[] receivedFrames = receivedCharacters.split(";");
                    //Add messages to the list
                    for (String frame : receivedFrames) {
                        if (!frame.equals("")) {
                            CBusMessage receivedMessage = CBusMessage.getFromString(frame);
                            Log.v(TAG, "received Event: " + receivedMessage.getEvent());
                            receivedMessages.add(receivedMessage);
                        }
                    }
                } catch (InterruptedException | IOException | StringIndexOutOfBoundsException | NullPointerException ignored) {
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.post(getMessagesRunnable);
    }

    public void onStopFrameListener() {
        handler.removeCallbacks(getMessagesRunnable);
    }

    public void send(final String message) {
        Thread thread = new Thread(() -> {
            try {
                byte[] bMessage = message.getBytes(StandardCharsets.UTF_8);
                socketOutputStream.write(bMessage);
                Log.v(TAG, "send: " + message + CBusMessage.getFromString(message).getEvent());
            } catch (IOException | NullPointerException ignored) {
                Log.v(TAG, "Failed to send a frame: " + message + CBusMessage.getFromString(message).getEvent());
            }

        });
        thread.start();
    }

    public List<CBusMessage> getReceivedMessages() {
        return receivedMessages;
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
        if(length != 0){
            Log.v(TAG, "received: length=" + length + " message: " + message[0]);
        }
        return message[0];
    }

    public Handler getHandler() {
        return handler;
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