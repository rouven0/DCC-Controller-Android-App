package com.traincon.modelleisenbahn_controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ControllerFragment extends Fragment {
    final private int[] seekBarIdArray = new int[]{R.id.seekBar_1, R.id.seekBar_2, R.id.seekBar_3};
    final private int[] resetButtonIdArray = new int[]{R.id.button_reset_1, R.id.button_reset_2, R.id.button_reset_3};
    final private int[] textViewIdArray = new int[]{R.id.sText_1, R.id.sText_2, R.id.sText_3};
    final private SeekBar[] seekBarArray = new SeekBar[seekBarIdArray.length];
    final private FloatingActionButton[] resetButtonArray = new FloatingActionButton[resetButtonIdArray.length];
    final private TextView[] textViewArray = new TextView[textViewIdArray.length];

    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_controller,container, false);
        initSeekBars(rootView);
        initResetButtons(rootView);

        /*final Handler handler = new Handler();

        final Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                update();
                handler.post(this);
            }
        };
        handler.post(updateRunnable);*/
        return rootView;
    }

    private void initSeekBars(View view){
        for(int n=0; n<seekBarArray.length; n++){
            seekBarArray[n]=view.findViewById(seekBarIdArray[n]);
            textViewArray[n]=view.findViewById(textViewIdArray[n]);
            final int finalN = n;
            seekBarArray[n].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    textViewArray[finalN].setText(Integer.toString(i-50));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }
    }

    private void initResetButtons(View view){
        for(int n=0;n<resetButtonIdArray.length; n++){
            resetButtonArray[n]=view.findViewById(resetButtonIdArray[n]);
            final int finalN = n;
            resetButtonArray[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seekBarArray[finalN].setProgress(50);
                }
            });
        }
    }

    /*private void update(){

    }*/
}
