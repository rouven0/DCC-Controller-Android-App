package com.traincon.modelleisenbahn_controller.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.traincon.modelleisenbahn_controller.BoardManager;
import com.traincon.modelleisenbahn_controller.Cab;
import com.traincon.modelleisenbahn_controller.R;
import com.traincon.modelleisenbahn_controller.widget.TwoDirSeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class ControllerFragment extends Fragment {
    private final Handler handler = new Handler();
    private BoardManager boardManager;
    private Cab cab;
    private SwitchCompat sessionSwitch;
    private TwoDirSeekBar controllerSeekBar;
    private TextView seekBarTextView;
    private Runnable keepAlive;
    private Runnable getSpeed;
    final int[] buttonIdArray = new int[]{R.id.button_f1, R.id.button_f2,R.id.button_f3, R.id.button_f4, R.id.button_f5, R.id.button_f6, R.id.button_f7, R.id.button_f8, R.id.button_f9};
    private  final ToggleButton[] functionButtons = new ToggleButton[buttonIdArray.length];

    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        boardManager = getArguments().getParcelable("boardManager");
        initCab();
        initUpdates();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initCab() {
        cab = new Cab(boardManager);
        sessionSwitch = requireView().findViewById(R.id.sessionSwitch);
        controllerSeekBar = requireView().findViewById(R.id.seekBar);
        seekBarTextView = requireView().findViewById(R.id.sText);
        seekBarTextView.setText("0");
        controllerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarTextView.setText(String.format("%s", i - 127));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.post(getSpeed);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(getSpeed);
            }
        });

        sessionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        sessionSwitch.setChecked(cab.allocateSession(9001));
                        controllerSeekBar.setValue(cab.getSpeedDir());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    cab.releaseSession();
                    controllerSeekBar.setValue(0);
                }
                resetFunctionButtons();
            }
        });

        Button idleButton = requireView().findViewById(R.id.button_idle);
        idleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cab.idle();
                controllerSeekBar.setValue(0);
            }
        });

        for (int i=0; i<functionButtons.length; i++) {
            functionButtons[i] = requireView().findViewById(buttonIdArray[i]);
            functionButtons[i].setTextOff("F"+(i+1)+" "+functionButtons[i].getTextOff());
            functionButtons[i].setTextOn("F"+(i+1)+ " "+functionButtons[i].getTextOn());
            functionButtons[i].setChecked(false);
            final int finalI = i;
            functionButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cab.setFunction(finalI+1, functionButtons[finalI].isChecked());
                }
            });
        }
    }

    private void initUpdates() {
        keepAlive = new Runnable() {
            @Override
            public void run() {
                cab.keepAlive();
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(keepAlive);

        getSpeed = new Runnable() {
            @Override
            public void run() {
                int value = controllerSeekBar.getProgress() - 127;
                cab.setSpeedDir(value);
                handler.postDelayed(this, 100);
            }
        };
    }

    private void resetFunctionButtons(){
        for (ToggleButton toggleButton : functionButtons) {
            toggleButton.setChecked(false);
        }
    }

    @Override
    public void onDestroy() {
        cab.releaseSession();
        handler.removeCallbacks(keepAlive);
        super.onDestroy();
    }
}