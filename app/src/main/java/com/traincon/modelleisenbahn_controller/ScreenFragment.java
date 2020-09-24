package com.traincon.modelleisenbahn_controller;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import static android.content.ContentValues.TAG;


public class ScreenFragment extends Fragment {
    final private int[] switchIdArray = {R.id.switch_1, R.id.switch_2, R.id.switch_3, R.id.switch_4, R.id.switch_5, R.id.switch_6, R.id.switch_7, R.id.switch_8, R.id.switch_9, R.id.switch_10, R.id.switch_11, R.id.switch_12, R.id.switch_13, R.id.switch_14, R.id.switch_15, R.id.switch_16};
    final private int[] sectionIdArray = {R.id.section_1, R.id.section_2, R.id.section_3, R.id.section_4, R.id.section_5, R.id.section_6, R.id.section_7, R.id.section_8, R.id.section_9, R.id.section_10, R.id.section_11, R.id.section_12, R.id.section_13};
    final private SwitchCompat[] switchSwitchCompatArray = new SwitchCompat[switchIdArray.length];
    final private ToggleButton[] sectionToggleButtonArray = new ToggleButton[sectionIdArray.length];
    final private FrameLayout[] imageOverlayFrames_x = new FrameLayout[40];
    final private FrameLayout[] imageOverlayFrames_y = new FrameLayout[30];
    private View rootView;
    private ConstraintLayout constraintLayout;
    private Handler handler;

    private BoardManager boardManager;
    private final Runnable updateRunnable = new Runnable() {
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
    private final Runnable initRunnable = new Runnable() {
        @Override
        public void run() {
            //Frames Erstellen
            ImageView imageView = rootView.findViewById(R.id.image_gp);
            ConstraintSet constraintSet = new ConstraintSet();
            for (int i = 0; i < imageOverlayFrames_x.length; i++) {
                imageOverlayFrames_x[i] = new FrameLayout(Objects.requireNonNull(getActivity()));
                imageOverlayFrames_x[i].setId(View.generateViewId());
                imageOverlayFrames_x[i].setLayoutParams(new FrameLayout.LayoutParams(imageView.getWidth() / imageOverlayFrames_x.length, imageView.getHeight() / imageOverlayFrames_y.length));
                constraintLayout.addView(imageOverlayFrames_x[i]);
            }
            for (int i = 0; i < imageOverlayFrames_y.length; i++) {
                imageOverlayFrames_y[i] = new FrameLayout(Objects.requireNonNull(getActivity()));
                imageOverlayFrames_y[i].setId(View.generateViewId());
                imageOverlayFrames_y[i].setLayoutParams(new FrameLayout.LayoutParams(imageView.getWidth() / imageOverlayFrames_x.length, imageView.getHeight() / imageOverlayFrames_y.length));
                constraintLayout.addView(imageOverlayFrames_y[i]);
            }
            //Frames Ausrichten
            constraintSet.clone(constraintLayout);
            for (int i = 0; i < imageOverlayFrames_x.length; i++) {
                if (i == 0) {
                    constraintSet.connect(imageOverlayFrames_x[i].getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.START, 0);
                } else {
                    constraintSet.connect(imageOverlayFrames_x[i].getId(), ConstraintSet.START, imageOverlayFrames_x[i - 1].getId(), ConstraintSet.END, 0);
                }
                constraintSet.connect(imageOverlayFrames_x[i].getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP, 0);
            }
            for (int i = 0; i < imageOverlayFrames_y.length; i++) {
                if (i == 0) {
                    constraintSet.connect(imageOverlayFrames_y[i].getId(), ConstraintSet.TOP, imageView.getId(), ConstraintSet.TOP, 0);
                } else {
                    constraintSet.connect(imageOverlayFrames_y[i].getId(), ConstraintSet.TOP, imageOverlayFrames_y[i - 1].getId(), ConstraintSet.BOTTOM, 0);
                }
                constraintSet.connect(imageOverlayFrames_y[i].getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.START, 0);
            }
            constraintSet.applyTo(constraintLayout);
            //Schalter platzieren
            final int[][] switchPositionArray = new int[][]{{26, 19}, {12, 25}, {30, 19}, {9, 26}, {32, 18}, {13, 26}, {11, 27}, {31, 20}, {29, 21}, {9, 28}, {21, 26}, {23, 26}, {4, 12}, {9, 10}, {8, 0}, {23, 0}};
            final int[][] sectionPositionArray = new int[][]{{19, 20}, {18, 22}, {18, 23}, {15, 8}, {19, 12}, {19, 13}, {19, 24}, {15, 7}, {13, 28}, {30, 28}, {13, 2}, {13, 0}, {15, 1}};
            placeSwitches(rootView, switchPositionArray, switchIdArray, switchSwitchCompatArray);
            placeSections(rootView, sectionPositionArray, sectionIdArray, sectionToggleButtonArray);
            handler.post(updateRunnable);
        }
    };

    public ScreenFragment() {
        // Required empty public constructor
    }

    public ScreenFragment(BoardManager bManager) {
        boardManager = bManager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //Gleisplan einrichten
        rootView = inflater.inflate(R.layout.fragment_screen, container, false);
        constraintLayout = rootView.findViewById(R.id.constraintLayout_imageView);
        handler = new Handler(Objects.requireNonNull(getContext()).getMainLooper());
        handler.post(initRunnable); // als runnable, damit es schneller dargestellt wird
        return rootView;
    }

    //Alle Weichenschalter an die richtigen Positionen verschieben
    private void placeSwitches(View view, int[][] positionArray, int[] idArray, final SwitchCompat[] switchCompatArray) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        for (int i = 0; i < idArray.length; i++) {
            switchCompatArray[i] = view.findViewById(idArray[i]);

            if (positionArray[i][0] < imageOverlayFrames_x.length && positionArray[i][1] < imageOverlayFrames_y.length) {
                constraintSet.connect(switchCompatArray[i].getId(), ConstraintSet.START, imageOverlayFrames_x[positionArray[i][0]].getId(), ConstraintSet.START);
                constraintSet.connect(switchCompatArray[i].getId(), ConstraintSet.TOP, imageOverlayFrames_y[positionArray[i][1]].getId(), ConstraintSet.TOP);
            } else {
                Log.d(TAG, "placeSwitches: " + i + " Fehler beim Positionieren");
            }
            final int finalI = i;
            switchCompatArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boardManager.setSwitch(finalI, switchCompatArray[finalI].isChecked());
                }
            });
        }
        constraintSet.applyTo(constraintLayout);
    }

    //Alle Gleisabschnittsschalter an die richtigen Positionen verschieben
    private void placeSections(View view, int[][] positionArray, @NonNull int[] idArray, final ToggleButton[] toggleButtonArray) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        for (int i = 0; i < idArray.length; i++) {
            toggleButtonArray[i] = view.findViewById(idArray[i]);
            if (positionArray[i][0] < imageOverlayFrames_x.length && positionArray[i][1] < imageOverlayFrames_y.length) {
                constraintSet.connect(toggleButtonArray[i].getId(), ConstraintSet.START, imageOverlayFrames_x[positionArray[i][0]].getId(), ConstraintSet.START);
                constraintSet.connect(toggleButtonArray[i].getId(), ConstraintSet.TOP, imageOverlayFrames_y[positionArray[i][1]].getId(), ConstraintSet.TOP);
            } else {
                Log.d(TAG, "placeSections: " + i + " Fehler beim Positionieren");
            }
            toggleButtonArray[i].setTextOn((i + 1) + "-An");
            toggleButtonArray[i].setTextOff((i + 1) + "-Aus");
            toggleButtonArray[i].setChecked(false);
            final int finalI = i;
            toggleButtonArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boardManager.setSection(finalI, toggleButtonArray[finalI].isChecked());
                }
            });
        }
        constraintSet.applyTo(constraintLayout);

    }

    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}