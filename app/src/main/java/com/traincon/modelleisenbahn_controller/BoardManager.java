package com.traincon.modelleisenbahn_controller;

import android.util.Log;

import java.net.Socket;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class BoardManager {
    public final int[] switchStates = new int[16];
    public final int[] sectionStates = new int[13];
    public int lightState = 0;

    public Socket mainSocket;

    public BoardManager(){
        Arrays.fill(switchStates, 0);
        Arrays.fill(sectionStates, 0);
    }


    public void setSwitch(int targetSwitch, int targetState){
        Log.d(TAG, "setSwitch: Weiche: "+ (targetSwitch + 1) +" auf Status: "+ targetState);
    }

    public void setSection(int targetSection, int targetState){
        Log.d(TAG, "setSection: Gleisabschnitt: "+ (targetSection + 1) +" auf Status: "+ targetState);
    }
}