package com.traincon.modelleisenbahn_controller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.traincon.modelleisenbahn_controller.AccessoryController;
import com.traincon.modelleisenbahn_controller.BoardManager;
import com.traincon.modelleisenbahn_controller.Cab;
import com.traincon.modelleisenbahn_controller.R;
import java.io.IOException;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    final private int[] switchIdArray = {R.id.switch_1, R.id.switch_2, R.id.switch_3, R.id.switch_4, R.id.switch_5, R.id.switch_6, R.id.switch_7, R.id.switch_8, R.id.switch_9, R.id.switch_10, R.id.switch_11, R.id.switch_12, R.id.switch_13, R.id.switch_14, R.id.switch_15, R.id.switch_16};
    final private int[] sectionIdArray = {R.id.section_1, R.id.section_2, R.id.section_3, R.id.section_4, R.id.section_5, R.id.section_6, R.id.section_7, R.id.section_8, R.id.section_9, R.id.section_10, R.id.section_11, R.id.section_12, R.id.section_13};
    final private ToggleButton[] switches = new ToggleButton[switchIdArray.length];
    final private SwitchCompat[] sections = new SwitchCompat[sectionIdArray.length];
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private ConstraintLayout accessoryFrame;
    private Menu menu;
    private Handler handler;
    private Runnable updateSwitchStates;
    private BoardManager boardManager;
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

        handler = new Handler(getMainLooper());
        initLayout();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        updateLayout();
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

        //Reconnect
        if (item.getItemId() == R.id.action_reconnect) {
            try {
                boardManager.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void initLayout() {
        String[] controllerTags = new String[]{"c1", "c2", "c3"};
        Fragment[] controllers = new Fragment[controllerTags.length];
        for (int i = 0; i < controllers.length; i++) {
            controllers[i] = fragmentManager.findFragmentByTag(controllerTags[i]);
            Bundle bundle = new Bundle();
            bundle.putParcelable("boardManager", boardManager);
            assert controllers[i] != null;
            controllers[i].setArguments(bundle);
            //Destroy controller 2 and 3 because they are not shown in portrait mode
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && i != 0) {
                try {

                    controllers[i].onDestroy();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        accessoryController = new AccessoryController(boardManager);
        accessoryFrame = findViewById(R.id.accessory);
        initSwitches();
        initSections();
        initUpdates();
        applyMode();
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
        updateSwitchStates = new Runnable() {
            @Override
            public void run() {
                Thread thread = new Thread(() -> {
                    try {
                        accessoryController.requestSwitchStates();
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                for (int i = 0; i < accessoryController.switchStates.length; i++) {
                    switches[i].setChecked(accessoryController.switchStates[i]);
                }

                for (int i = 0; i < accessoryController.sectionStates.length; i++) {
                    sections[i].setChecked(accessoryController.sectionStates[i]);
                }
                handler.postDelayed(this, 1000);
            }
        };
    }

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

    private void updateRunnables() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (sharedPreferences.getBoolean("is_accessory_on", false)) {
            handler.post(updateSwitchStates);
        } else {
            handler.removeCallbacks(updateSwitchStates);
        }
    }

    private void applyMode(){
        TextView sectionTextView = findViewById(R.id.label_sections);
        ScrollView sectionScrollView = findViewById(R.id.scrollView_sections);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (Objects.equals(sharedPreferences.getString("mode", "D"), "D")) {
            sectionTextView.setVisibility(View.GONE);
            sectionScrollView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLayout();
        updateRunnables();
    }

    protected void onDestroy() {
        handler.removeCallbacks(updateSwitchStates);
        super.onDestroy();
    }
}