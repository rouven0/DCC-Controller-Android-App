package com.traincon.modelleisenbahn_controller.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.traincon.modelleisenbahn_controller.BoardManager;
import com.traincon.modelleisenbahn_controller.Cab;
import com.traincon.modelleisenbahn_controller.R;
import com.traincon.modelleisenbahn_controller.database.AppDatabase;
import com.traincon.modelleisenbahn_controller.database.DatabaseSpinnerAdapter;
import com.traincon.modelleisenbahn_controller.database.DatabaseViewModel;
import com.traincon.modelleisenbahn_controller.database.Loco;
import com.traincon.modelleisenbahn_controller.widget.TwoDirSeekBar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

    private AppDatabase database;
    private List<Loco> locos;
    private Spinner spinner;

    private Bundle savedInstanceState;
    private final String KEY_SELECTED_ITEM = "selectedItem";

    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        database = new ViewModelProvider(requireActivity()).get(DatabaseViewModel.class).appDatabase;
        if (savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState;
        }
        return inflater.inflate(R.layout.fragment_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        boardManager = getArguments().getParcelable("boardManager");
        initCab();
        initUpdates();
    }

    @Override
    public void onResume() {
        Thread thread = new Thread(() -> locos = database.locoDao().getAll());
        thread.start();
        try {
            thread.join();
            spinner.setAdapter(new DatabaseSpinnerAdapter(getContext(), locos));
            if (savedInstanceState != null && cab.getSession() == null) {
                try {
                    spinner.setSelection(savedInstanceState.getInt(KEY_SELECTED_ITEM));
                } catch (IndexOutOfBoundsException ignored) {}

            }
        } catch (InterruptedException e) {e.printStackTrace();}
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_ITEM, spinner.getSelectedItemPosition());
    }

    @Override
    public void onDestroy() {
        cab.releaseSession();
        handler.removeCallbacks(keepAlive);
        super.onDestroy();
    }

    private void initCab() {
        spinner = requireView().findViewById(R.id.spinner);
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

        sessionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    if(spinner.getSelectedItem() != null){
                        sessionSwitch.setChecked(cab.allocateSession((((Loco) spinner.getSelectedItem()).address)));
                        controllerSeekBar.setValue(cab.getSpeedDir());
                    } else {
                        sessionSwitch.setChecked(false);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                cab.releaseSession();
                controllerSeekBar.setValue(0);
            }
            resetFunctionButtons();
        });

        Button idleButton = requireView().findViewById(R.id.button_idle);
        idleButton.setOnClickListener(v -> {
            cab.idle();
            controllerSeekBar.setValue(0);
        });

        for (int i=0; i<functionButtons.length; i++) {
            functionButtons[i] = requireView().findViewById(buttonIdArray[i]);
            functionButtons[i].setTextOff("F"+(i+1)+" "+functionButtons[i].getTextOff());
            functionButtons[i].setTextOn("F"+(i+1)+ " "+functionButtons[i].getTextOn());
            functionButtons[i].setChecked(false);
            final int finalI = i;
            functionButtons[i].setOnClickListener(v -> cab.setFunction(finalI+1, functionButtons[finalI].isChecked()));
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
}