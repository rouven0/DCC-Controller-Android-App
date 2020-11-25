package com.traincon.modelleisenbahn_controller;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

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
        Button idleButton = requireView().findViewById(R.id.button_idle);
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
                        sessionSwitch.setChecked(cab.allocateSession());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    cab.releaseSession();
                }
            }
        });

        idleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cab.idle();
                controllerSeekBar.setValue(0);
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        cab.releaseSession();
        handler.removeCallbacks(keepAlive);
    }
}