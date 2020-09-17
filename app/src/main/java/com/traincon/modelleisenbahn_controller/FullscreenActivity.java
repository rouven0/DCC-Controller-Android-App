package com.traincon.modelleisenbahn_controller;

import android.annotation.SuppressLint;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.Objects;

public class FullscreenActivity extends AppCompatActivity {
    final private int[] menuButtonIdArray = new int[]{R.id.mainMenuButton, R.id.presetsMenuButton, R.id.switchMenuButton, R.id.switchSetStandardActionbutton, R.id.switchSetToCenterActionButton, R.id.switchCalibrateActionButton, R.id.sectionMenuButton, R.id.sectionSetStandardActionbutton, R.id.sectionsAllOffActionbutton, R.id.reconnectActionButton, R.id.lightActionButton};
    final private Button[] menuButtons = new Button[menuButtonIdArray.length];

    private boolean isMenuOpen = false;
    private boolean isPresetMenuOpen = false;
    private boolean isSwitchMenuOpen = false;
    private boolean isSectionMenuOpen = false;
    private BoardManager boardManager;

    @SuppressLint("SetTextI18n")
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

        boardManager = new BoardManager(devId, host, port);
        boardManager.connect();
    }

    private final Thread createTabLayout = new Thread(new Runnable() {
        @Override
        public void run() {
            //Tablayout einrichten
            TabLayout tabLayout = findViewById(R.id.tabLayout);
            TabLayout.Tab firsttab = tabLayout.newTab();
            firsttab.setText("Fahrtsteuerung");
            tabLayout.addTab(firsttab);
            TabLayout.Tab secondtab = tabLayout.newTab();
            secondtab.setText("Interaktiver gleisplan");
            tabLayout.addTab(secondtab);
            final Fragment[] fragment = {null};

            //Am Anfang das fragment_controller anzeigen
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

    private final Thread createMenuButtons = new Thread(new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < menuButtons.length; i++) {
                menuButtons[i] = findViewById(menuButtonIdArray[i]);
                if (i > 0) {
                    menuButtons[i].setVisibility(View.GONE);
                }
            }

            //Hauptmenu
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

            //Weichen
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

            //Weichen 3 Runden
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

            //Weichen Alle auf Mitte
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

            //Weichen Nachstellen
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

            //Gleisabschnitte
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

            // Gleisabschnitte 3 Runden
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

            //Gleisabschnitte Alle ausschalten
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

            // Neu verbinden
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

            //Licht
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

    protected void onDestroy() {
        super.onDestroy();
        try {
            boardManager.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}