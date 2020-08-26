package com.traincon.modelleisenbahn_controller;
//------------
//Fertig, wenn möglich nicht mehr ändern
//
//
//-----------

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import java.net.SocketException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;


public class ScreenFragment extends Fragment {
    final private int[] switchIdArray = {R.id.switch_1, R.id.switch_2, R.id.switch_3, R.id.switch_4, R.id.switch_5, R.id.switch_6, R.id.switch_7, R.id.switch_8, R.id.switch_9, R.id.switch_10, R.id.switch_11, R.id.switch_12, R.id.switch_13, R.id.switch_14, R.id.switch_15, R.id.switch_16};
    final private int[] sectionIdArray = {R.id.section_1, R.id.section_2, R.id.section_3, R.id.section_4, R.id.section_5, R.id.section_6, R.id.section_7, R.id.section_8, R.id.section_9, R.id.section_10, R.id.section_11, R.id.section_12, R.id.section_13};
    final private SwitchCompat[] switchSwitchCompatArray = new SwitchCompat[16];
    final private ToggleButton[] sectionToggleButtonArray = new ToggleButton[13];
    private String screenRatio;
    private BoardManager boardManager;


    public ScreenFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ScreenFragment(BoardManager bManager, String ratio) {
        screenRatio = ratio;
        boardManager = bManager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_screen, container, false);
        FrameLayout frameLayout = rootView.findViewById(R.id.frameLayout_imageView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
        boolean switchIsBackgroundcolor = false;
        int[] switchRotationArray = new int[]{270, 255, 255, 270, 240, 270, 90, 75, 255, 270, 255, 255, 45, 105, 90, 90};
        float[][] switchPositionArray = new float[][]{
                {740, 389},
                {350, 520},
                {855, 385},
                {275, 547},

                {930, 371},
                {400, 545},
                {310, 555},
                {903, 398},

                {810, 440},
                {235, 577},
                {575, 553},
                {700, 515},

                {120, 230},
                {310, 216},
                {180, 6},
                {600, 6}};

        float[][] sectionPositionArray = new float[][]{
                {590, 390},
                {570, 430},
                {550, 470},
                {390, 132},
                {550, 235},
                {550, 270},

                {530, 510},
                {390, 172},
                {460, 575},
                {800, 575},

                {100, 80},
                {350, 0},
                {450, 25}};

        if ("16:9".equals(screenRatio)) {
            layoutParams.setMargins(42, 0, 42, 0);
        } else {
            layoutParams.setMargins(0, 73, 0, 0);
            for (int i = 0; i < switchPositionArray.length; i++) {
                switchPositionArray[i][0] = switchPositionArray[i][0] * 165 / 100;
                switchPositionArray[i][1] = switchPositionArray[i][1] * 168 / 100;
            }
            switchIsBackgroundcolor = true;

            for (int i = 0; i < sectionPositionArray.length; i++) {
                sectionPositionArray[i][0] = sectionPositionArray[i][0] * 165 / 100;
                sectionPositionArray[i][1] = sectionPositionArray[i][1] * 168 / 100;
            }
        }

        frameLayout.setLayoutParams(layoutParams);
        placeSwitches(rootView, switchPositionArray, switchIdArray, switchSwitchCompatArray, switchRotationArray, switchIsBackgroundcolor);
        placeSections(rootView, sectionPositionArray, sectionIdArray, sectionToggleButtonArray);
        return rootView;
    }

    //Alle Weichenschalter an die richtigen Positionen verschieben
    private void placeSwitches(View view, float[][] positionArray, int[] idArray, final SwitchCompat[] switchCompatArray, int[] rotationArray, boolean isBackroundColor) {
        for (int i = 0; i < idArray.length; i++) {
            switchCompatArray[i] = view.findViewById(idArray[i]);
            switchCompatArray[i].setTranslationX(positionArray[i][0]);
            switchCompatArray[i].setTranslationY(positionArray[i][1]);
            switchCompatArray[i].setRotation(rotationArray[i]);
            if (isBackroundColor) {
                switchCompatArray[i].setBackgroundColor(getResources().getColor(R.color.colorBackgroundDark, Objects.requireNonNull(getActivity()).getTheme()));
            }
            //Weichen stellen, wenn der Schalter betätigt wurde
            final int finalI = i;
            switchCompatArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int targetState;
                    if (switchCompatArray[finalI].isChecked()) {
                        targetState = 1; //abzweig
                    } else {
                        targetState = 0; //gerade
                    }
                    boardManager.setSwitch(finalI, targetState);
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
                    int targetState;
                    if (toggleButtonArray[finalI].isChecked()) {
                        targetState = 1;
                    } else {
                        targetState = 0;
                    }
                    boardManager.setSection(finalI, targetState);
                }
            });
        }
    }

    //Alle Schalter werden entsprechend der Stellungen auf dem Brett angezeigt
    public void update() throws InterruptedException, SocketException {
        boardManager.requestSwitchStates();
        for (int i = 0; i < boardManager.switchStates.length; i++) {
            if (boardManager.switchStates[i] == 0) {
                switchSwitchCompatArray[i].setChecked(false);
            }
            if (boardManager.switchStates[i] == 1) {
                switchSwitchCompatArray[i].setChecked(true);
            }
        }

        for (int i = 0; i < boardManager.sectionStates.length; i++) {
            if (boardManager.sectionStates[i] == 0) {
                sectionToggleButtonArray[i].setChecked(false);
            }
            if (boardManager.sectionStates[i] == 1) {
                sectionToggleButtonArray[i].setChecked(true);
            }
        }
    }
}
