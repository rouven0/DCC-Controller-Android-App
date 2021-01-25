package com.traincon.modelleisenbahn_controller;

import android.util.Log;

import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

//This is still hardcoded
/**
 * The AccessoryController controls all switches and sections and gets their states
 * The boardManager is given by the mainActivity
 */
public class AccessoryController {
    public final BoardManager boardManager;

    public final boolean[] switchStates = new boolean[16];
    public final boolean[] sectionStates = new boolean[13];
    private final String NN_SWITCH_HI = "00";
    private final String NN_SWITCH_LO = "65";
    private final String DN_SWITCH_1 = "03";
    private final String DN_SWITCH_2 = "04";
    private boolean lightState = false;

    public AccessoryController(BoardManager boardManager) {
        this.boardManager = boardManager;
        Arrays.fill(switchStates, false);
        Arrays.fill(sectionStates, false);
    }

    public void setSwitch(int targetSwitch, boolean targetState) {
        switchStates[targetSwitch] = targetState;
        if (targetState) {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));
        } else {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));
        }
    }

    public void setSection(int targetSection, boolean targetState) {
        sectionStates[targetSection] = targetState;
        if (targetState) {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "24", "0" + Integer.toHexString(targetSection).toUpperCase()})));

        } else {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "24", "0" + Integer.toHexString(targetSection).toUpperCase()})));

        }
    }

    public void setLightOn() {
        lightState = true;
        Log.d(TAG, "setLightOn: ");
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "24", "0D"})));
    }

    public void setLightOff() {
        lightState = false;
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "24", "0D"})));
    }

    public boolean getLightState() {
        return lightState;
    }

    public void setLightState(boolean lightState) {
        this.lightState = lightState;
    }

    public void requestSwitchStates() {
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("NVRD", new String[]{NN_SWITCH_HI, NN_SWITCH_LO, DN_SWITCH_1})));
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("NVRD", new String[]{NN_SWITCH_HI, NN_SWITCH_LO, DN_SWITCH_2})));
    }

    /**
     * When an NVANS event is detected it will get here
     * The NV will be converted in to a boolean array that the mainActivity can read to display the states
     * @param cBusMessage is received by the boarManager
     */
    public void onReceiveSwitchStates(CBusMessage cBusMessage) {
        if (cBusMessage.getData()[0].equals(NN_SWITCH_HI) && cBusMessage.getData()[1].equals(NN_SWITCH_LO)) {
            StringBuilder binaryStates = new StringBuilder(Integer.toBinaryString(Integer.parseInt(cBusMessage.getData()[3], 16)));
            while (binaryStates.length() < 8) {
                binaryStates.insert(0, "0");
            }
            binaryStates.reverse();
            switch (cBusMessage.getData()[2]) {
                case DN_SWITCH_1:
                    for (int i = 0; i < binaryStates.length(); i++) {
                        switchStates[i] = String.valueOf(binaryStates.charAt(i)).equals("1");
                    }
                    break;
                case DN_SWITCH_2:
                    for (int i = 0; i < binaryStates.length(); i++) {
                        switchStates[i + binaryStates.length()] = String.valueOf(binaryStates.charAt(i)).equals("1");
                    }
                    break;
            }
        }
    }
}
