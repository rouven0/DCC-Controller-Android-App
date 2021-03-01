package com.traincon.modelleisenbahn_controller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.preference.PreferenceManager;

import com.google.android.material.tabs.TabLayout;
import com.traincon.CBusMessage.CBusMessage;
import com.traincon.modelleisenbahn_controller.AccessoryController;
import com.traincon.modelleisenbahn_controller.BoardManager;
import com.traincon.modelleisenbahn_controller.Cab;
import com.traincon.modelleisenbahn_controller.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    final private int[] switchIdArray = {R.id.switch_1, R.id.switch_2, R.id.switch_3, R.id.switch_4, R.id.switch_5, R.id.switch_6, R.id.switch_7, R.id.switch_8, R.id.switch_9, R.id.switch_10, R.id.switch_11, R.id.switch_12, R.id.switch_13, R.id.switch_14, R.id.switch_15, R.id.switch_16};
    final private int[] sectionIdArray = {R.id.section_1, R.id.section_2, R.id.section_3, R.id.section_4, R.id.section_5, R.id.section_6, R.id.section_7, R.id.section_8, R.id.section_9, R.id.section_10, R.id.section_11, R.id.section_12, R.id.section_13};
    final private ToggleButton[] switches = new ToggleButton[switchIdArray.length];
    final private SwitchCompat[] sections = new SwitchCompat[sectionIdArray.length];
    private final String KEY_LIGHTSTATE = "lightState";
    private ConstraintLayout accessoryFrame;
    private FragmentContainerView[] controllerContainers;
    private Fragment[] controllers;
    private Menu menu;
    private Handler handler;
    private Runnable updateSwitchStates;

    /**
     * @see BoardManager
     */
    private BoardManager boardManager;
    private Runnable cbusUpdateRunnable;

    /**
     * @see AccessoryController
     */
    private AccessoryController accessoryController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String host = intent.getStringExtra("host");
        int port = intent.getIntExtra("port", 0);

        boardManager = new BoardManager(host, port);
        boardManager.connect();
        boardManager.onStartFrameListener();

        handler = boardManager.getHandler();
        accessoryController = new AccessoryController(boardManager);
        if (savedInstanceState != null) {
            accessoryController.setLightState(savedInstanceState.getBoolean(KEY_LIGHTSTATE));
        }
        initLayout();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_LIGHTSTATE, accessoryController.getLightState());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        updateLayout();
        if (accessoryController.getLightState()) {
            menu.getItem(0).setIcon(R.drawable.ic_light_bulb_on_24);
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Light
        if (item.getItemId() == R.id.action_light) {
            if (!accessoryController.getLightState()) {
                accessoryController.setLightOn();
                menu.getItem(0).setIcon(R.drawable.ic_light_bulb_on_24);
            } else {
                accessoryController.setLightOff();
                menu.getItem(0).setIcon(R.drawable.ic_light_bulb_off_24);
            }
        }

        //Configure
        if (item.getItemId() == R.id.action_config) {
            startActivity(new Intent(getBaseContext(), ControllerConfigActivity.class));
        }

        //Loco
        if (item.getItemId() == R.id.action_locoList) {
            startActivity(new Intent(getBaseContext(), LocoConfigActivity.class));
        }

        //Reset
        if(item.getItemId() == R.id.action_reset){
            Cab.reset(boardManager);
        }

        //Reconnect
        if (item.getItemId() == R.id.action_reconnect) {
            boardManager.disconnect();
            boardManager.connect();
        }

        if (item.getItemId() == R.id.action_estop) {
            Cab.estop(boardManager);
        }

        if (item.getItemId() == R.id.action_exit) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLayout();
        applyAccessoryMode();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateSwitchStates);
        for (Fragment fragment : controllers){
            ((ControllerFragment) fragment).getCab().releaseSession();
        }
        //Wait until all sessions are released
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handler.removeCallbacks(cbusUpdateRunnable);
        boardManager.onStopFrameListener();
        boardManager.disconnect();
        super.onDestroy();
    }

    private void initLayout() {
        String[] controllerTags = new String[]{"c1", "c2", "c3"};
        controllers = new Fragment[controllerTags.length];

        int[] containerTags = new int[]{R.id.con1, R.id.con2, R.id.con3};
        controllerContainers = new FragmentContainerView[containerTags.length];

        for (int i = 0; i < controllers.length; i++) {
            controllers[i] = getSupportFragmentManager().findFragmentByTag(controllerTags[i]);
            Bundle bundle = new Bundle();
            bundle.putParcelable("boardManager", boardManager);
            assert controllers[i] != null;
            controllers[i].setArguments(bundle);

            controllerContainers[i] = findViewById(containerTags[i]);
        }
        accessoryFrame = findViewById(R.id.accessory);
        initTabs();
        initSwitches();
        initSections();
        initUpdates();
    }

    /**
     * This shows a TabLayout in portrait mode to choose a controller
     */
    private void initTabs(){
        TabLayout cabSelection = findViewById(R.id.cabSelection);
        cabSelection.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showCab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void showCab(int cab){
        for(int i = 0; i< controllerContainers.length; i++){
            if(i==cab){
                controllerContainers[i].setVisibility(View.VISIBLE);
                controllerContainers[i].startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.nav_default_enter_anim));
            } else {
                controllerContainers[i].setVisibility(View.GONE);
            }
        }
    }

    private void initSwitches() {
        for (int i = 0; i < switchIdArray.length; i++) {
            switches[i] = findViewById(switchIdArray[i]);
            switches[i].setTextOff(String.format("%s", i + 1) + " |");
            switches[i].setTextOn(String.format("%s", i + 1) + " /");
            switches[i].setChecked(false);
            final int finalI = i;
            switches[i].setOnClickListener(v -> accessoryController.setSwitch(finalI, switches[finalI].isChecked()));
        }
    }

    private void initSections() {
        for (int i = 0; i < sectionIdArray.length; i++) {
            sections[i] = findViewById(sectionIdArray[i]);
            sections[i].setText(String.format("%s", i + 1));
            final int finalI = i;
            sections[i].setOnClickListener(view -> accessoryController.setSection(finalI, sections[finalI].isChecked()));
        }
    }

    private void initUpdates() {
        cbusUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                processCbusMessages();
                handler.postDelayed(this, 200);
            }
        };
        handler.post(cbusUpdateRunnable);
        updateSwitchStates = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < accessoryController.switchStates.length; i++) {
                    switches[i].setChecked(accessoryController.switchStates[i]);
                }

                for (int i = 0; i < accessoryController.sectionStates.length; i++) {
                    sections[i].setChecked(accessoryController.sectionStates[i]);
                }
                handler.postDelayed(this, 10);
            }
        };
    }

    /**
     * All received messages will be processed here
     * @see CBusMessage
     */
    private void processCbusMessages(){
        List<CBusMessage> receivedMessages = boardManager.getReceivedMessages();
        for (CBusMessage cbusMessage : receivedMessages) {
            switch (cbusMessage.getEvent()) {
                case "ESTOP":
                    for (Fragment fragment : controllers) {
                        ((ControllerFragment) fragment).displayEstop();
                    }
                    break;
                case "PLOC":
                    for (Fragment fragment : controllers) {
                        ((ControllerFragment) fragment).onSessionAllocated(cbusMessage);
                    }
                    break;

                case "ERR":
                    if(cbusMessage.getData()[2].equals("08")){
                        for(Fragment fragment :  controllers){
                            if(((ControllerFragment) fragment).onSessionCancelled(cbusMessage)){
                                break;
                            }
                        }
                    }
                    break;
                case "NVANS":
                    accessoryController.onReceiveSwitchStates(cbusMessage);
                    break;
                case "ASON":
                case "ASOF":
                    accessoryController.setSwitchState(cbusMessage);
            }
        }
        receivedMessages.clear();
    }

    /**
     * This is called in onResume
     * The accessoryFrame is shown depending on the LocoConfig preferences
     */
    private void updateLayout() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (sharedPreferences.getBoolean("is_accessory_on", false)) {
            accessoryFrame.setVisibility(View.VISIBLE);
            if (menu != null) {
                menu.getItem(0).setVisible(true);
            }
        } else {
            accessoryFrame.setVisibility(View.GONE);
            if (menu != null) {
                menu.getItem(0).setVisible(false);
            }
        }
    }

    private void applyAccessoryMode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (sharedPreferences.getBoolean("is_accessory_on", false)) {
            accessoryController.requestSwitchStates();
            handler.post(updateSwitchStates);
        } else {
            handler.removeCallbacks(updateSwitchStates);
        }
    }
}