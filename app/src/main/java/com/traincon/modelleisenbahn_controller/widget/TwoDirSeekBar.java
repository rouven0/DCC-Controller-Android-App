package com.traincon.modelleisenbahn_controller.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TwoDirSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    public TwoDirSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TwoDirSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoDirSeekBar(@NonNull Context context) {
        super(context);
    }

    public void setValue(int value){
        setProgress(value + (getMax()/2));
    }
}
