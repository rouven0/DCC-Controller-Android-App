package com.traincon.modelleisenbahn_controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;


public class ScreenFragment extends Fragment {
    final private int[] switchIdArray = {R.id.switch_1, R.id.switch_2, R.id.switch_3, R.id.switch_4, R.id.switch_5, R.id.switch_6, R.id.switch_7, R.id.switch_8, R.id.switch_9, R.id.switch_10, R.id.switch_11, R.id.switch_12, R.id.switch_13, R.id.switch_14, R.id.switch_15, R.id.switch_16};
    final private int[] sectionIdArray = {R.id.section_1, R.id.section_2, R.id.section_3, R.id.section_4, R.id.section_5, R.id.section_6, R.id.section_7, R.id.section_8, R.id.section_9, R.id.section_10, R.id.section_11, R.id.section_12, R.id.section_13};
    final private SwitchCompat[] switchSwitchCompatArray = new SwitchCompat[switchIdArray.length];
    final private ToggleButton[] sectionToggleButtonArray = new ToggleButton[sectionIdArray.length];
    private String screenRatio;
    final private Handler handler = new Handler();
    private Runnable updateRunnable;
    private BoardManager boardManager;


    public ScreenFragment() {
        // Required empty public constructor
    }

    public ScreenFragment(BoardManager bManager, String ratio) {
        screenRatio = ratio;
        boardManager = bManager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //Gleisplan einrichten
        View rootView = inflater.inflate(R.layout.fragment_screen, container, false);
        int[] switchRotationArray = new int[]{90, 255, 255, 270, 240, 270, 90, 75, 255, 270, 255, 255, 45, 105, 90, 90};
        float[][] switchPositionArray = new float[][]{{740, 389}, {350, 520}, {855, 385}, {275, 547}, {930, 371}, {400, 545}, {310, 555}, {903, 398}, {810, 440}, {235, 577}, {575, 553}, {700, 515}, {120, 230}, {310, 216}, {180, 6}, {600, 6}};
        float[][] sectionPositionArray = new float[][]{{590, 390},{570, 430},{550, 470},{390, 132},{550, 235}, {550, 270},{530, 510},{390, 172},{460, 575},{800, 575},{100, 80},{350, 0},{450, 25}};

        if (!"16:9".equals(screenRatio)){ //Todo entfernen und für alle geräte kompatibel machen(constraints)
            for (int i = 0; i < switchPositionArray.length; i++) {
                switchPositionArray[i][0] = switchPositionArray[i][0] * 165 / 100;
                switchPositionArray[i][1] = switchPositionArray[i][1] * 168 / 100;
            }
            for (int i = 0; i < sectionPositionArray.length; i++) {
                sectionPositionArray[i][0] = sectionPositionArray[i][0] * 165 / 100;
                sectionPositionArray[i][1] = sectionPositionArray[i][1] * 168 / 100;
            }
        }
        placeSwitches(rootView, switchPositionArray, switchIdArray, switchSwitchCompatArray, switchRotationArray);
        placeSections(rootView, sectionPositionArray, sectionIdArray, sectionToggleButtonArray);

        //Alle Schalter werden entsprechend der Stellungen auf dem Brett angezeigt
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    update();
                    handler.postDelayed(this, 1000);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        handler.post(updateRunnable);
        return rootView;
    }

    //Alle Weichenschalter an die richtigen Positionen verschieben
    private void placeSwitches(View view, float[][] positionArray, int[] idArray, final SwitchCompat[] switchCompatArray, int[] rotationArray) {
        for (int i = 0; i < idArray.length; i++) {
            switchCompatArray[i] = view.findViewById(idArray[i]);
            switchCompatArray[i].setTranslationX(positionArray[i][0]);
            switchCompatArray[i].setTranslationY(positionArray[i][1]);
            switchCompatArray[i].setRotation(rotationArray[i]);
            final int finalI = i;
            switchCompatArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boardManager.setSwitch(finalI, switchCompatArray[finalI].isChecked());
                }
            });
        }
    }

    //Alle Gleisabschnittsschalter an die richtigen Positionen verschieben
    @SuppressLint("SetTextI18n")
    private void placeSections(View view, float[][] positionArray, @NonNull int[] idArray, final ToggleButton[] toggleButtonArray) {
        for (int i = 0; i < idArray.length; i++) {
            toggleButtonArray[i] = view.findViewById(idArray[i]);
            toggleButtonArray[i].setTranslationX(positionArray[i][0]);
            toggleButtonArray[i].setTranslationY(positionArray[i][1]);
            toggleButtonArray[i].setText((i + 1) + "-Aus");
            toggleButtonArray[i].setTextOn((i + 1) + "-An");
            toggleButtonArray[i].setTextOff((i + 1) + "-Aus");
            //Gleisabschnitt bei Click umschalten
            final int finalI = i;
            toggleButtonArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*int targetState;
                    if (toggleButtonArray[finalI].isChecked()) {
                        targetState = 1;
                    } else {
                        targetState = 0;
                    }*/
                    boardManager.setSection(finalI, toggleButtonArray[finalI].isChecked());
                }
            });
        }
    }


    private void update() throws InterruptedException, IOException {
        boardManager.requestSwitchStates();
        for (int i = 0; i < boardManager.switchStates.length; i++) {
            switchSwitchCompatArray[i].setChecked(boardManager.switchStates[i]);
        }

        for (int i = 0; i < boardManager.sectionStates.length; i++) {
           sectionToggleButtonArray[i].setChecked(boardManager.sectionStates[i]);
        }
    }

    public void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}
