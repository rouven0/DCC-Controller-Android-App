package com.traincon.modelleisenbahn_controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ConsoleActivity extends AppCompatActivity {
    private final int[] editTextIdAray = new int[]{R.id.input_canid, R.id.input_addr, R.id.input_dat1, R.id.input_dat2, R.id.input_dat3, R.id.input_dat4, R.id.input_dat5, R.id.input_dat6, R.id.input_dat7};
    private final EditText[] currentPartialMessage = new EditText[editTextIdAray.length];
    private final CBusMessage currentCBusMessage = new CBusMessage(null, null); //message that is sent to the board
    public String host;
    public int port;
    public Socket consoleSocket;
    private EditText currentMessage;
    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        host = intent.getStringExtra("host");
        port = intent.getIntExtra("port", 0);
        connect();
        initSendRow();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Functions to send messages
    private void initSendRow() {
        for (int i = 0; i < currentPartialMessage.length; i++) {
            currentPartialMessage[i] = findViewById(editTextIdAray[i]);
            currentPartialMessage[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    updateMessage();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        currentMessage = findViewById(R.id.input_rawmessage);
        FloatingActionButton sendButton = findViewById(R.id.fab_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentCBusMessage.getData() != null && currentCBusMessage.getEvent() != null) {
                    String canId = currentPartialMessage[0].getText().toString();
                    send((new CBusAsciiMessageBuilder(canId).build(currentCBusMessage)));
                    clearInput();
                }
            }
        });
    }

    private void updateMessage() {
        String canId = currentPartialMessage[0].getText().toString();
        String[] data = new String[7];
        for (int i = 0; i < data.length; i++) {
            data[i] = currentPartialMessage[i + 2].getText().toString();
        }
        currentCBusMessage.setEvent(currentPartialMessage[1].getText().toString());
        currentCBusMessage.setData(data);
        currentMessage.setText((new CBusAsciiMessageBuilder(canId)).build(currentCBusMessage));
    }

    private void clearInput() {
        for (EditText editText : currentPartialMessage) {
            editText.setText("");
        }
        currentMessage.setText("");
    }

    //Networking functions
    public void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    consoleSocket = new Socket();
                    consoleSocket.connect(new InetSocketAddress(host, port));
                    socketInputStream = new DataInputStream(consoleSocket.getInputStream());
                    socketOutputStream = new DataOutputStream(consoleSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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

// --Commented out by Inspection START (07.10.20 17:48):
//    private String receive(int lenght) throws InterruptedException {
//        final String[] message = new String[]{""};
//        final byte[] rawMessage = new byte[lenght];
//
//        final Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < rawMessage.length; i++) {
//                    try {
//                        rawMessage[i] = socketInputStream.readByte();
//                    } catch (IOException | NullPointerException ignored) {
//                    }
//                    message[0] += (char) rawMessage[i];
//                }
//            }
//        });
//        thread.start();
//        thread.join();
//        return message[0];
//    }
// --Commented out by Inspection STOP (07.10.20 17:48)
}
