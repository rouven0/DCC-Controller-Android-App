package com.traincon.modelleisenbahn_controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ConsoleActivity extends AppCompatActivity {
    private final int[] editTextIdArray = new int[]{R.id.input_event, R.id.input_dat1, R.id.input_dat2, R.id.input_dat3, R.id.input_dat4, R.id.input_dat5, R.id.input_dat6, R.id.input_dat7};
    private final EditText[] currentPartialMessage = new EditText[editTextIdArray.length];
    private final CBusMessage currentCBusMessage = new CBusMessage("", null); //message that is sent to the board
    private final String[] lastPartialMessage = new String[editTextIdArray.length];
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

    //Log functions
    private void initLog() {
        final ScrollView rawLogScrollView = findViewById(R.id.scrollView_raw);
        final ScrollView processedLogScrollView = findViewById(R.id.scrollView_processed);
        final TextView rawLogTextView = findViewById(R.id.log_raw);
        final TextView processedLogTextView = findViewById(R.id.log_processed);
        final Handler handler = new Handler(getBaseContext().getMainLooper());
        Runnable logUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    //Initialize
                    String oldLog = rawLogTextView.getText().toString();
                    String oldLog_processed = processedLogTextView.getText().toString();
                    //Get string
                    String receivedString = receive(socketInputStream.available());
                    //update the log
                    if (!receivedString.equals("")) {
                        //raw string
                        String combinedLog = oldLog + "\n" + receivedString;
                        rawLogTextView.setText(combinedLog);
                        rawLogScrollView.fullScroll(View.FOCUS_DOWN);
                        //processed string
                        String combinedLog_processed = oldLog_processed + "\n" + getResources().getString(R.string.info_event)+ " " +getReceivedCBusMessage(receivedString).getEvent() + ", " + getResources().getString(R.string.info_data) + " " + Arrays.toString(getReceivedCBusMessage(receivedString).getData());
                        processedLogTextView.setText(combinedLog_processed);
                        processedLogScrollView.fullScroll(View.FOCUS_DOWN);
                    }

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 10);
            }
        };
        handler.post(logUpdateRunnable);
    }

    private CBusMessage getReceivedCBusMessage(String receivedFrame){
        String event = receivedFrame.substring(7,9);
        String[] data = new String[receivedFrame.substring(9).length()/2];
        for(int i=0; i<data.length; i++){
            data[i] = receivedFrame.substring(9+(2*i), 11+(2*i));
        }
        return new CBusMessage(event, data);
    }

    //Functions to send messages
    private void initSendRow() {
        for (int i = 0; i < currentPartialMessage.length; i++) {
            currentPartialMessage[i] = findViewById(editTextIdArray[i]);
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
        currentMessage = findViewById(R.id.input_message);
        FloatingActionButton sendButton = findViewById(R.id.fab_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMessage();
                send((new CBusAsciiMessageBuilder().build(currentCBusMessage)));
                //Save the message
                for (int i = 0; i < lastPartialMessage.length; i++) {
                    lastPartialMessage[i] = currentPartialMessage[i].getText().toString();
                }
                clearInput();
            }
        });
        FloatingActionButton cachedButton = findViewById(R.id.fab_cached);
        cachedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastMessage();
            }
        });
    }

    private void updateMessage() {
        String[] data = new String[7];
        for (int i = 0; i < data.length; i++) {
            data[i] = currentPartialMessage[i + 1].getText().toString();
        }
        currentCBusMessage.setEvent(currentPartialMessage[1].getText().toString());
        currentCBusMessage.setData(data);
        currentMessage.setText((new CBusAsciiMessageBuilder()).build(currentCBusMessage));
    }

    private void clearInput() {
        for (EditText editText : currentPartialMessage) {
            editText.setText("");
        }
        currentMessage.setText("");
    }

    private void getLastMessage() {
        for (int i = 0; i < lastPartialMessage.length; i++) {
            currentPartialMessage[i].setText(lastPartialMessage[i]);
        }
        updateMessage();
    }
    //Networking functions
    private void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    consoleSocket = new Socket();
                    consoleSocket.connect(new InetSocketAddress(host, port));
                    socketInputStream = new DataInputStream(consoleSocket.getInputStream());
                    socketOutputStream = new DataOutputStream(consoleSocket.getOutputStream());
                    initLog();
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
