package com.traincon.modelleisenbahn_controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FullscreenActivity extends AppCompatActivity {
    final private int[] menuButtonIdArray = new int[]{R.id.mainMenuButton, R.id.presetsMenuButton, R.id.switchMenuButton, R.id.switchSetStandardActionbutton, R.id.switchSetToCenterActionButton, R.id.switchCalibrateActionButton, R.id.sectionMenuButton, R.id.sectionSetStandardActionbutton, R.id.sectionsAllOffActionbutton, R.id.reconnectActionButton, R.id.lightActionButton};
    final private Button[] menuButtons = new Button[menuButtonIdArray.length];

    private boolean isMenuOpen = false;
    private boolean isPresetMenuOpen = false;
    private boolean isSwitchMenuOpen = false;
    private boolean isSectionMenuOpen = false;
    private BoardManager boardManager;

    //<editor-fold desc="private final Thread createTabLayout...">
    private final Thread createTabLayout = new Thread(new Runnable() {
        @Override
        public void run() {
            //Init TabLayout
            TabLayout tabLayout = findViewById(R.id.tabLayout);
            TabLayout.Tab firsttab = tabLayout.newTab();
            firsttab.setText(getResources().getString(R.string.tab_controller));
            tabLayout.addTab(firsttab);
            TabLayout.Tab secondtab = tabLayout.newTab();
            secondtab.setText(getResources().getString(R.string.tab_layout));
            tabLayout.addTab(secondtab);
            final Fragment[] fragment = {null};

            //Show Controllerfragment at the beginning
            fragment[0] = new ControllerFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.constraintLayout, Objects.requireNonNull(fragment[0]));
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();

            //Bei änderungen im Tablayout
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            fragment[0] = new ControllerFragment();
                            break;
                        case 1:
                            fragment[0] = new ScreenFragment(boardManager);
                            break;
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.constraintLayout, Objects.requireNonNull(fragment[0]));
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }
    });
    //</editor-fold>

    //<editor-fold desc="private final Thread createMeuButtons...">
    private final Thread createMenuButtons = new Thread(new Runnable() {
        @Override
        public void run() {
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
                        menuButtons[9].setVisibility(View.VISIBLE);
                        menuButtons[10].setVisibility(View.VISIBLE);
                        isMenuOpen = true;
                    } else {
                        //Menu Schließen
                        for (int i = 1; i < menuButtonIdArray.length; i++) {
                            menuButtons[i].setVisibility(View.GONE);
                        }

                        isMenuOpen = false;
                    }
                }
            });

            //Presets
            menuButtons[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isPresetMenuOpen) {
                        menuButtons[0].setVisibility(View.GONE);
                        menuButtons[2].setVisibility(View.VISIBLE);
                        menuButtons[6].setVisibility(View.VISIBLE);
                        menuButtons[9].setVisibility(View.GONE);
                        menuButtons[10].setVisibility(View.GONE);
                        isPresetMenuOpen = true;
                    } else {
                        menuButtons[0].setVisibility(View.VISIBLE);
                        menuButtons[2].setVisibility(View.GONE);
                        menuButtons[6].setVisibility(View.GONE);
                        menuButtons[9].setVisibility(View.VISIBLE);
                        menuButtons[10].setVisibility(View.VISIBLE);
                        isPresetMenuOpen = false;
                    }
                }
            });

            //Switches
            menuButtons[2].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSwitchMenuOpen) {
                        menuButtons[1].setVisibility(View.GONE);
                        menuButtons[3].setVisibility(View.VISIBLE);
                        menuButtons[4].setVisibility(View.VISIBLE);
                        menuButtons[5].setVisibility(View.VISIBLE);
                        menuButtons[6].setVisibility(View.GONE);
                        isSwitchMenuOpen = true;
                    } else {
                        menuButtons[1].setVisibility(View.VISIBLE);
                        menuButtons[3].setVisibility(View.GONE);
                        menuButtons[4].setVisibility(View.GONE);
                        menuButtons[5].setVisibility(View.GONE);
                        menuButtons[6].setVisibility(View.VISIBLE);
                        isSwitchMenuOpen = false;
                    }

                }
            });

            //Switches 3 Laps
            menuButtons[3].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Menu Schließen
                    for (int i = 1; i < menuButtonIdArray.length; i++) {
                        menuButtons[i].setVisibility(View.GONE);
                    }
                    menuButtons[0].setVisibility(View.VISIBLE);
                    boardManager.switchPreset_3r();
                    isMenuOpen = false;
                }
            });

            //Switches all to center
            menuButtons[4].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Menu Schließen
                    for (int i = 1; i < menuButtonIdArray.length; i++) {
                        menuButtons[i].setVisibility(View.GONE);
                    }
                    menuButtons[0].setVisibility(View.VISIBLE);
                    boardManager.switchSetToCenter();
                    isMenuOpen = false;
                }
            });

            //Calibrate Switches
            menuButtons[5].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Menu Schließen
                    for (int i = 1; i < menuButtonIdArray.length; i++) {
                        menuButtons[i].setVisibility(View.GONE);
                    }
                    menuButtons[0].setVisibility(View.VISIBLE);
                    boardManager.switchCalibrate();
                    isMenuOpen = false;
                }
            });

            //Sections
            menuButtons[6].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSectionMenuOpen) {
                        menuButtons[1].setVisibility(View.GONE);
                        menuButtons[2].setVisibility(View.GONE);
                        menuButtons[7].setVisibility(View.VISIBLE);
                        menuButtons[8].setVisibility(View.VISIBLE);
                        isSectionMenuOpen = true;
                    } else {
                        menuButtons[1].setVisibility(View.VISIBLE);
                        menuButtons[2].setVisibility(View.VISIBLE);
                        menuButtons[7].setVisibility(View.GONE);
                        menuButtons[8].setVisibility(View.GONE);
                        isSectionMenuOpen = false;
                    }

                }
            });

            //Sections 3 laps
            menuButtons[7].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Menu Schließen
                    for (int i = 1; i < menuButtonIdArray.length; i++) {
                        menuButtons[i].setVisibility(View.GONE);
                    }
                    menuButtons[0].setVisibility(View.VISIBLE);
                    boardManager.sectionPreset_3r();
                    isMenuOpen = false;
                }
            });

            //All sections off
            menuButtons[8].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Menu Schließen
                    for (int i = 1; i < menuButtonIdArray.length; i++) {
                        menuButtons[i].setVisibility(View.GONE);
                    }
                    menuButtons[0].setVisibility(View.VISIBLE);
                    boardManager.sectionsAllOff();
                    isMenuOpen = false;
                }
            });

            //reconnect
            menuButtons[9].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Menu Schließen
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
            menuButtons[10].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Menu Schließen
                    for (int i = 1; i < menuButtonIdArray.length; i++) {
                        menuButtons[i].setVisibility(View.GONE);
                    }
                    boardManager.setLight();
                    isMenuOpen = false;
                }
            });
        }
    });

    //</editor-fold>

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_main);

        Intent intent = getIntent();
        String devId = intent.getStringExtra("deviceId");
        String host = intent.getStringExtra("host");
        int port = intent.getIntExtra("port", 0);
        createTabLayout.start();
        createMenuButtons.start();

        boardManager = new BoardManager(getBaseContext(), devId, host, port);
        boardManager.connect();
    }

    protected void onDestroy() {
        super.onDestroy();
        try {
            boardManager.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}