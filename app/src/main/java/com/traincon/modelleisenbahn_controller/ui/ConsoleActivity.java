package com.traincon.modelleisenbahn_controller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;
import com.traincon.modelleisenbahn_controller.BoardManager;
import com.traincon.modelleisenbahn_controller.R;

import java.io.IOException;
import java.util.Arrays;

public class ConsoleActivity extends AppCompatActivity {
    private final int[] editTextIdArray = new int[]{R.id.input_event, R.id.input_dat1, R.id.input_dat2, R.id.input_dat3, R.id.input_dat4, R.id.input_dat5, R.id.input_dat6, R.id.input_dat7};
    private final EditText[] currentPartialMessage = new EditText[editTextIdArray.length];
    private final CBusMessage currentCBusMessage = new CBusMessage("", null); //message that is sent to the board
    private final String[] lastPartialMessage = new String[editTextIdArray.length];
    private BoardManager boardManager;
    private EditText currentMessage;
    private Handler handler;
    private Runnable logUpdateRunnable;
    private boolean isRunning = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        String host = intent.getStringExtra("host");
        int port = intent.getIntExtra("port", 0);
        boardManager = new BoardManager(host, port);
        boardManager.connect();
        initLog();
        initSendRow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_console, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_playPause && isRunning) {
            handler.removeCallbacks(logUpdateRunnable);
            isRunning = false;

            item.setIcon(R.drawable.ic_baseline_play_arrow_24);
            return true;
        } else if (item.getItemId() == R.id.action_playPause && !isRunning) {
            handler.post(logUpdateRunnable);
            isRunning = true;
            item.setIcon(R.drawable.ic_baseline_pause_24);
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
        handler = new Handler(getBaseContext().getMainLooper());
        logUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    //Initialize
                    //Get string
                    String receivedString = "";
                    String[] receivedFrames;
                    try {
                        receivedString = boardManager.receive(boardManager.getSocketInputStream().available());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    receivedFrames = receivedString.split(";");

                    for (String frame : receivedFrames) {
                        String oldLog = rawLogTextView.getText().toString();
                        String oldLog_processed = processedLogTextView.getText().toString();
                        //update the log
                        if (!receivedString.equals("")) {
                            //raw string
                            String combinedLog = oldLog + "\n" + frame;
                            rawLogTextView.setText(combinedLog);
                            rawLogScrollView.fullScroll(View.FOCUS_DOWN);
                            //processed string
                            String combinedLog_processed = oldLog_processed + "\n" + getResources().getString(R.string.info_event) + " " + boardManager.getReceivedCBusMessage(frame).getEvent() + ", " + getResources().getString(R.string.info_data) + " " + Arrays.toString(boardManager.getReceivedCBusMessage(frame).getData());
                            processedLogTextView.setText(combinedLog_processed);
                            processedLogScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    }

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 10);
            }
        };
        handler.post(logUpdateRunnable);
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
        sendButton.setOnClickListener(view -> {
            updateMessage();
            boardManager.send((new CBusAsciiMessageBuilder().build(currentCBusMessage)));
            //Save the message
            for (int i = 0; i < lastPartialMessage.length; i++) {
                lastPartialMessage[i] = currentPartialMessage[i].getText().toString();
            }
            clearInput();
        });
        FloatingActionButton cachedButton = findViewById(R.id.fab_cached);
        cachedButton.setOnClickListener(v -> getLastMessage());
    }

    private void updateMessage() {
        String[] data = new String[7];
        for (int i = 0; i < data.length; i++) {
            data[i] = currentPartialMessage[i + 1].getText().toString();
        }
        currentCBusMessage.setEvent(currentPartialMessage[0].getText().toString());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            boardManager.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.removeCallbacks(logUpdateRunnable);
    }
}
