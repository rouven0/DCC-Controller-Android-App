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

public class MainActivity extends AppCompatActivity {
    final private int[] menuButtonIdArray = new int[]{R.id.mainMenuButton, R.id.reconnectActionButton, R.id.lightActionButton};
    final private Button[] menuButtons = new Button[menuButtonIdArray.length];
    private boolean isMenuOpen = false;
    private BoardManager boardManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String devId = intent.getStringExtra("deviceId");
        String host = intent.getStringExtra("host");
        int port = intent.getIntExtra("port", 0);
        createTabLayout();
        createMenuButtons();

        boardManager = new BoardManager(getBaseContext(), devId, host, port);
        boardManager.connect();
    }

    public void createTabLayout() {
        //Init TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
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

    public void createMenuButtons() {
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
                    //Menu Schließen
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
        menuButtons[2].setOnClickListener(new View.OnClickListener() {
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

    protected void onDestroy() {
        super.onDestroy();
        try {
            boardManager.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}