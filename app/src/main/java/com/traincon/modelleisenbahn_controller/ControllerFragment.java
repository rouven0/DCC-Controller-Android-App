package com.traincon.modelleisenbahn_controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ControllerFragment extends Fragment {
    public int takeOver=0;

    final private int[] seekBarIdArray = new int[]{R.id.seekBar_1, R.id.seekBar_2, R.id.seekBar_3};
    final private int[] resetButtonIdArray = new int[]{R.id.button_reset_1, R.id.button_reset_2, R.id.button_reset_3};
    final private int[] textViewIdArray = new int[]{R.id.sText_1, R.id.sText_2, R.id.sText_3};
    final private int[] radioButtonIdArray = new int[]{R.id.takeOver_single, R.id.takeOver_12, R.id.takeOver_13, R.id.takeOver_23, R.id.takeOver_123};
    final private SeekBar[] seekBarArray = new SeekBar[seekBarIdArray.length];
    final private FloatingActionButton[] resetButtonArray = new FloatingActionButton[resetButtonIdArray.length];
    final private TextView[] textViewArray = new TextView[textViewIdArray.length];
    final private RadioButton[] radioButtonArray = new RadioButton[radioButtonIdArray.length];

    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_controller,container, false);
        initSeekBars(rootView);
        initResetButtons(rootView);
        initTakeOver(rootView);

        final Handler handler = new Handler();

        final Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                update();
                handler.post(this);
            }
        };
        handler.post(updateRunnable);
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

    private void initTakeOver(View view){
        for(int n=0; n<radioButtonIdArray.length; n++){
            radioButtonArray[n]=view.findViewById(radioButtonIdArray[n]);
        }
        Button confirm = view.findViewById(R.id.button_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<radioButtonArray.length; i++){
                    if(radioButtonArray[i].isChecked()){
                        takeOver=i;
                        setResetButtonVisibility(i);
                    }
                }
            }
        });
    }

    //Unnütze resetButtons verstecken
    private void setResetButtonVisibility(int visibilityCode){
        switch (visibilityCode){
            case 0:
                resetButtonArray[1].setVisibility(View.VISIBLE);
                resetButtonArray[2].setVisibility(View.VISIBLE);
                break;
            case 1:
                resetButtonArray[1].setVisibility(View.INVISIBLE);
                resetButtonArray[2].setVisibility(View.VISIBLE);
                break;
            case 2:
            case 3:
                resetButtonArray[1].setVisibility(View.VISIBLE);
                resetButtonArray[2].setVisibility(View.INVISIBLE);
                break;
            case 4:
                resetButtonArray[1].setVisibility(View.INVISIBLE);
                resetButtonArray[2].setVisibility(View.INVISIBLE);
                break;

        }
    }

    private void update(){
        //Entsprechend der übernahme setzen
        switch (takeOver){
            case 0:
                break;
            case 1:
                seekBarArray[1].setProgress(seekBarArray[0].getProgress());
                break;
            case 2:
                seekBarArray[2].setProgress(seekBarArray[0].getProgress());
                break;
            case 3:
                seekBarArray[2].setProgress(seekBarArray[1].getProgress());
                break;
            case 4:
                seekBarArray[1].setProgress(seekBarArray[0].getProgress());
                seekBarArray[2].setProgress(seekBarArray[0].getProgress());
                break;
        }
    }
}
