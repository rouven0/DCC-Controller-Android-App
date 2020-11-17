package com.traincon.modelleisenbahn_controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    final private int[] switchIdArray = {R.id.switch_1, R.id.switch_2, R.id.switch_3, R.id.switch_4, R.id.switch_5, R.id.switch_6, R.id.switch_7, R.id.switch_8, R.id.switch_9, R.id.switch_10, R.id.switch_11, R.id.switch_12, R.id.switch_13, R.id.switch_14, R.id.switch_15, R.id.switch_16};
    final private int[] sectionIdArray = {R.id.section_1, R.id.section_2, R.id.section_3, R.id.section_4, R.id.section_5, R.id.section_6, R.id.section_7, R.id.section_8, R.id.section_9, R.id.section_10, R.id.section_11, R.id.section_12, R.id.section_13};
    final private ToggleButton[] switches = new ToggleButton[switchIdArray.length];
    final private SwitchCompat[] sections = new SwitchCompat[sectionIdArray.length];
    final private int[] seekBarIdArray = new int[]{R.id.seekBar_1, R.id.seekBar_2, R.id.seekBar_3};
    final private int[] textViewIdArray = new int[]{R.id.sText_1, R.id.sText_2, R.id.sText_3};
    final private SeekBar[] controllerSeekBars = new SeekBar[seekBarIdArray.length];
    final private TextView[] seekBarTextViews = new TextView[textViewIdArray.length];
    private ConstraintLayout accessoryFrame;
    private Menu menu;
    private Handler handler;
    private Runnable updateRunnable;

    private BoardManager boardManager;
    private AccessoryController accessoryController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String host = intent.getStringExtra("host");
        int port = intent.getIntExtra("port", 0);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        boardManager = new BoardManager(host, port);
        boardManager.connect();

        accessoryController = new AccessoryController(boardManager);

        handler = new Handler(getMainLooper());
        initLayout();
        updateLayout();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_config_controller, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Light
        if (item.getItemId() == R.id.action_light) {
            if (!accessoryController.getLightState()) {
                accessoryController.setLightOn();
                menu.getItem(0).setIcon(R.drawable.ic_light_bulb_on);
            } else {
                accessoryController.setLightOff();
                menu.getItem(0).setIcon(R.drawable.ic_light_bulb_off);
            }
        }

        //Configure
        if (item.getItemId() == R.id.action_config) {
            startActivity(new Intent(getBaseContext(), ControllerConfigActivity.class));
        }

        //Reconnect
        if (item.getItemId() == R.id.action_reconnect) {
            try {
                boardManager.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            boardManager.connect();
        }

        //Back
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLayout() {
        initControllers();
        initAccessory();
        initUpdate();
    }

    private void initControllers() {
        for (int n = 0; n < controllerSeekBars.length; n++) {
            controllerSeekBars[n] = findViewById(seekBarIdArray[n]);
            seekBarTextViews[n] = findViewById(textViewIdArray[n]);
            final int finalN = n;
            controllerSeekBars[n].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    seekBarTextViews[finalN].setText(String.format("%s", i - 128));
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

    private void initAccessory() {
        accessoryFrame = findViewById(R.id.frame_accessory);
        initSwitches();
        initSections();
    }

    private void initSwitches() {
        for (int i = 0; i < switchIdArray.length; i++) {
            switches[i] = findViewById(switchIdArray[i]);
            switches[i].setTextOff(String.format("%s", i + 1) + " " + switches[i].getTextOff());
            switches[i].setTextOn(String.format("%s", i + 1) + " " + switches[i].getTextOn());
            switches[i].setChecked(false);
            final int finalI = i;
            switches[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accessoryController.setSwitch(finalI, switches[finalI].isChecked());
                }
            });
        }
    }

    private void initSections() {
        for (int i = 0; i < sectionIdArray.length; i++) {
            sections[i] = findViewById(sectionIdArray[i]);
            sections[i].setText(String.format("%s", i + 1));
            final int finalI = i;
            sections[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accessoryController.setSection(finalI, sections[finalI].isChecked());
                }
            });
        }
    }

    private void initUpdate() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    accessoryController.requestSwitchStates();
                    for (int i = 0; i < accessoryController.switchStates.length; i++) {
                        switches[i].setChecked(accessoryController.switchStates[i]);
                    }

                    for (int i = 0; i < accessoryController.sectionStates.length; i++) {
                        sections[i].setChecked(accessoryController.sectionStates[i]);
                    }
                    handler.postDelayed(this, 1000);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        handler.post(updateRunnable);
    }

    private void updateLayout() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (sharedPreferences.getBoolean("is_accessory_on", false)) {
            accessoryFrame.setVisibility(View.VISIBLE);
        } else {
            accessoryFrame.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLayout();
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