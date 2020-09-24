package com.traincon.modelleisenbahn_controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ControllerFragment extends Fragment {
    final private int[] seekBarIdArray = new int[]{R.id.seekBar_1, R.id.seekBar_2, R.id.seekBar_3};
    final private int[] textViewIdArray = new int[]{R.id.sText_1, R.id.sText_2, R.id.sText_3};
    final private SeekBar[] seekBarArray = new SeekBar[seekBarIdArray.length];
    final private TextView[] textViewArray = new TextView[textViewIdArray.length];

    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_controller, container, false);
        initSeekBars(rootView);

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

    private void initSeekBars(View view) {
        for (int n = 0; n < seekBarArray.length; n++) {
            seekBarArray[n] = view.findViewById(seekBarIdArray[n]);
            textViewArray[n] = view.findViewById(textViewIdArray[n]);
            final int finalN = n;
            seekBarArray[n].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    textViewArray[finalN].setText(Integer.toString(i - 50));
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

    /*private void update(){

    }*/
}