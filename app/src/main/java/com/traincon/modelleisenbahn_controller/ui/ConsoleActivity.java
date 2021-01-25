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

import androidx.annotation.NonNull;
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

/**
 * The console is used to receive and process all frames coming from the board.
 */
public class ConsoleActivity extends AppCompatActivity {
    private final int[] editTextIdArray = new int[]{R.id.input_event, R.id.input_dat1, R.id.input_dat2, R.id.input_dat3, R.id.input_dat4, R.id.input_dat5, R.id.input_dat6, R.id.input_dat7};
    private final EditText[] messagePartEditTexts = new EditText[editTextIdArray.length];

    /**
     * This the CBusMessage object that is created from the EditTexts and the sent to the board as ASCII Frame
     * @see CBusMessage
     */
    private final CBusMessage currentCBusMessage = new CBusMessage("", null);

    /**
     * The last sent message will be cached here in its parts
     * It will be loaded into the Edittext when the cache button is pressed
     */
    private final String[] lastPartialMessage = new String[editTextIdArray.length];

    /**
     * This is the preview of the message that is sent to the board
     */
    private EditText currentMessageString;

    /**
     * BoardManager instance to communicate with the board
     * @see BoardManager
     */
    private BoardManager boardManager;

    private Handler handler;
    private Runnable logUpdateRunnable;

    /**
     * Boolean used for the play/pause button
     */
    private boolean isRunning = true;

    private TextView rawLogTextView;
    private TextView processedLogTextView;

    private final String KEY_LOG_RAW = "logText_raw";
    private final String KEY_LOG_PROCESSED = "logText_processed";

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
        initLog(savedInstanceState);
        initSendRow();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_LOG_RAW, rawLogTextView.getText().toString());
        outState.putString(KEY_LOG_PROCESSED, processedLogTextView.getText().toString());
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

        if(item.getItemId() == R.id.action_clear){
            clear();
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

    /**
     * After creating the layout the runnable to get the logs is started
     * The runnable gets everything from the socketInputStream of the boardManager, splits it and prints it to the logs
     * The frame will be processed into a CBusMessage for event and data output
     * @see CBusMessage
     * @param savedInstanceState is used to load the log when the phone is rotated
     */
    private void initLog(Bundle savedInstanceState) {
        final ScrollView rawLogScrollView = findViewById(R.id.scrollView_raw);
        final ScrollView processedLogScrollView = findViewById(R.id.scrollView_processed);
        rawLogTextView = findViewById(R.id.log_raw);
        processedLogTextView = findViewById(R.id.log_processed);
        if(savedInstanceState != null){
            rawLogTextView.setText(savedInstanceState.getString(KEY_LOG_RAW));
            processedLogTextView.setText(savedInstanceState.getString(KEY_LOG_PROCESSED));
        }
        handler = new Handler(getMainLooper());

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
                            rawLogScrollView.postDelayed(() -> rawLogScrollView.fullScroll(View.FOCUS_DOWN), 300);
                            //processed string
                            String combinedLog_processed = oldLog_processed + "\n" + getResources().getString(R.string.info_event) + " " + CBusMessage.getFromString(frame).getEvent() + ", " + getResources().getString(R.string.info_data) + " " + Arrays.toString(CBusMessage.getFromString(frame).getData());
                            processedLogTextView.setText(combinedLog_processed);
                            processedLogScrollView.postDelayed(() -> processedLogScrollView.fullScroll(View.FOCUS_DOWN), 300);
                        }
                    }

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 350);
            }
        };
        handler.post(logUpdateRunnable);
    }

    public void clear(){
        rawLogTextView.setText("");
        processedLogTextView.setText("");
    }

    //Functions to send messages
    private void initSendRow() {
        for (int i = 0; i < messagePartEditTexts.length; i++) {
            messagePartEditTexts[i] = findViewById(editTextIdArray[i]);
            messagePartEditTexts[i].addTextChangedListener(new TextWatcher() {
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
        currentMessageString = findViewById(R.id.input_message);
        FloatingActionButton sendButton = findViewById(R.id.fab_send);
        sendButton.setOnClickListener(view -> {
            updateMessage();
            boardManager.send((CBusAsciiMessageBuilder.build(currentCBusMessage)));
            //Save the message
            for (int i = 0; i < lastPartialMessage.length; i++) {
                lastPartialMessage[i] = messagePartEditTexts[i].getText().toString();
            }
            clearInput();
        });
        FloatingActionButton cachedButton = findViewById(R.id.fab_cached);
        cachedButton.setOnClickListener(v -> getLastMessage());
    }

    /**
     *  When one of the EditTexts changes currentMessageString will be updated
     *  A CBusAsciiMessage will be built and loaded into the currentMessageString
     */
    private void updateMessage() {
        String[] data = new String[7];
        for (int i = 0; i < data.length; i++) {
            data[i] = messagePartEditTexts[i + 1].getText().toString();
        }
        currentCBusMessage.setEvent(messagePartEditTexts[0].getText().toString());
        currentCBusMessage.setData(data);
        currentMessageString.setText(CBusAsciiMessageBuilder.build(currentCBusMessage));
    }

    private void clearInput() {
        for (EditText editText : messagePartEditTexts) {
            editText.setText("");
        }
        currentMessageString.setText("");
    }

    private void getLastMessage() {
        for (int i = 0; i < lastPartialMessage.length; i++) {
            messagePartEditTexts[i].setText(lastPartialMessage[i]);
        }
        updateMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boardManager.disconnect();
        handler.removeCallbacks(logUpdateRunnable);
    }
}
