package com.traincon.modelleisenbahn_controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity extends AppCompatActivity {
    final private int[] menuButtonIdArray = new int[]{R.id.mainMenuButton, R.id.reconnectActionButton, R.id.lightActionButton};
    final private Button[] menuButtons = new Button[menuButtonIdArray.length];
    final private int[] switchIdArray = {R.id.switch_1, R.id.switch_2, R.id.switch_3, R.id.switch_4, R.id.switch_5, R.id.switch_6, R.id.switch_7, R.id.switch_8, R.id.switch_9, R.id.switch_10, R.id.switch_11, R.id.switch_12, R.id.switch_13, R.id.switch_14, R.id.switch_15, R.id.switch_16};
    final private int[] sectionIdArray = {R.id.section_1, R.id.section_2, R.id.section_3, R.id.section_4, R.id.section_5, R.id.section_6, R.id.section_7, R.id.section_8, R.id.section_9, R.id.section_10, R.id.section_11, R.id.section_12, R.id.section_13};
    final private SwitchCompat[] switchSwitchCompatArray = new SwitchCompat[switchIdArray.length];
    final private ToggleButton[] sectionToggleButtonArray = new ToggleButton[sectionIdArray.length];
    final private int[] seekBarIdArray = new int[]{R.id.seekBar_1, R.id.seekBar_2, R.id.seekBar_3};
    final private int[] textViewIdArray = new int[]{R.id.sText_1, R.id.sText_2, R.id.sText_3};
    final private SeekBar[] seekBarArray = new SeekBar[seekBarIdArray.length];
    final private TextView[] textViewArray = new TextView[textViewIdArray.length];
    private BoardManager boardManager;
    private Handler handler;
    private Runnable updateRunnable;
    private boolean isMenuOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String devId = intent.getStringExtra("deviceId");
        String host = intent.getStringExtra("host");
        int port = intent.getIntExtra("port", 0);

        boardManager = new BoardManager(getBaseContext(), devId, host, port);
        boardManager.connect();

        handler = new Handler(getMainLooper());
        initLayout();

    }

    private void initLayout() {
        initMenuButtons();
        initSeekBars();
        initSwitches(switchIdArray, switchSwitchCompatArray);
        initSections(sectionIdArray, sectionToggleButtonArray);
        initUpdate();
    }

    private void initMenuButtons() {
        for (int i = 0; i < menuButtons.length; i++) {
            menuButtons[i] = findViewById(menuButtonIdArray[i]);
            if (i > 0) {
                menuButtons[i].setVisibility(View.GONE);
            }
        }

        //Main menu
        menuButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMenuOpen) {
                    menuButtons[1].setVisibility(View.VISIBLE);
                    menuButtons[2].setVisibility(View.VISIBLE);
                    isMenuOpen = true;
                } else {
                    for (int i = 1; i < menuButtonIdArray.length; i++) {
                        menuButtons[i].setVisibility(View.GONE);
                    }

                    isMenuOpen = false;
                }
            }
        });

        //Reconnect
        menuButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 1; i < menuButtonIdArray.length; i++) {
                    menuButtons[i].setVisibility(View.GONE);
                }
                try {
                    boardManager.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boardManager.connect();
                isMenuOpen = false;
            }
        });

        //Light
        menuButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 1; i < menuButtonIdArray.length; i++) {
                    menuButtons[i].setVisibility(View.GONE);
                }
                boardManager.setLight();
                isMenuOpen = false;
            }
        });
    }

    private void initSeekBars() {
        for (int n = 0; n < seekBarArray.length; n++) {
            seekBarArray[n] = findViewById(seekBarIdArray[n]);
            textViewArray[n] = findViewById(textViewIdArray[n]);
            final int finalN = n;
            seekBarArray[n].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    textViewArray[finalN].setText(String.format("%s", i - 50));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }
    }

    private void initSwitches(int[] idArray, final SwitchCompat[] switchCompatArray) {

        for (int i = 0; i < idArray.length; i++) {
            switchCompatArray[i] = findViewById(idArray[i]);
            final int finalI = i;
            switchCompatArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boardManager.setSwitch(finalI, switchCompatArray[finalI].isChecked());
                }
            });
        }
    }

    private void initSections(@NonNull int[] idArray, final ToggleButton[] toggleButtonArray) {

        for (int i = 0; i < idArray.length; i++) {
            toggleButtonArray[i] = findViewById(idArray[i]);
            toggleButtonArray[i].setChecked(false);
            final int finalI = i;
            toggleButtonArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boardManager.setSection(finalI, toggleButtonArray[finalI].isChecked());
                }
            });
        }
    }

    private void initUpdate() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    boardManager.requestSwitchStates();
                    for (int i = 0; i < boardManager.switchStates.length; i++) {
                        switchSwitchCompatArray[i].setChecked(boardManager.switchStates[i]);
                    }

                    for (int i = 0; i < boardManager.sectionStates.length; i++) {
                        sectionToggleButtonArray[i].setChecked(boardManager.sectionStates[i]);
                    }
                    handler.postDelayed(this, 1000);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        handler.post(updateRunnable);
    }

    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
        try {
            boardManager.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}