package com.traincon.modelleisenbahn_controller;

import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;

import java.io.IOException;
import java.util.Arrays;

public class AccessoryController {
    public final BoardManager boardManager;
    public final CBusAsciiMessageBuilder cBusAsciiMessageBuilder;

    public final boolean[] switchStates = new boolean[16];
    public final boolean[] sectionStates = new boolean[13];
    public boolean lightState = false;

    public AccessoryController(BoardManager boardManager){
        this.boardManager = boardManager;
        Arrays.fill(switchStates, false);
        Arrays.fill(sectionStates, false);
        cBusAsciiMessageBuilder = new CBusAsciiMessageBuilder(); //Gerätenummer wird zur canId
    }

    public void setSwitch(int targetSwitch, boolean targetState) {
        switchStates[targetSwitch] = targetState;
        if (targetState) {
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));
        } else {
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "23", "0" + Integer.toHexString(targetSwitch).toUpperCase()})));}
    }

    public void setSection(int targetSection, boolean targetState) {
        sectionStates[targetSection] = targetState;
        if (targetState) {
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "24", "0" + Integer.toHexString(targetSection).toUpperCase()})));

        } else {
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "24", "0" + Integer.toHexString(targetSection).toUpperCase()})));

        }
    }

    public void setLightOn() {
        lightState = true;
        boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("ASON", new String[]{"00", "11", "24", "0D"})));
    }

    public void setLightOff() {
        lightState = false;
        boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("ASOF", new String[]{"00", "11", "24", "0D"})));
    }

    public boolean getLightState(){
        return lightState;
    }

    public void requestSwitchStates() throws InterruptedException, IOException {
        //get the node variable
        boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("NVRD", new String[]{"00", "65", "03"})));
        String receivedSwitchStates_0 = boardManager.receive(CBusAsciiMessageBuilder.getExpectedMessageLength(3));
        boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("NVRD", new String[]{"00", "65", "04"})));
        String receivedSwitchStates_1 = boardManager.receive(CBusAsciiMessageBuilder.getExpectedMessageLength(3));
        //buffer
        try {
            boardManager.receive(boardManager.getSocketInputStream().available());
        } catch (NullPointerException ignore) {
        }
        //get the values
        receivedSwitchStates_0 = receivedSwitchStates_0.substring(15, 17);
        receivedSwitchStates_1 = receivedSwitchStates_1.substring(15, 17);
        StringBuilder receivedSwitchStatesBinary_0;
        StringBuilder receivedSwitchStatesBinary_1;
        try {
            //values to binary
            receivedSwitchStatesBinary_0 = new StringBuilder(Integer.toBinaryString(Integer.parseInt(receivedSwitchStates_0, 16)));
            receivedSwitchStatesBinary_1 = new StringBuilder(Integer.toBinaryString(Integer.parseInt(receivedSwitchStates_1, 16)));
            while (receivedSwitchStatesBinary_0.length() < 8) {
                receivedSwitchStatesBinary_0.insert(0, "0");
            }
            while (receivedSwitchStatesBinary_1.length() < 8) {
                receivedSwitchStatesBinary_1.insert(0, "0");
            }
            receivedSwitchStatesBinary_0.reverse();
            receivedSwitchStatesBinary_1.reverse();
            //apply the switch states
            for (int i = 0; i < receivedSwitchStatesBinary_0.length(); i++) {
                switchStates[i] = String.valueOf(receivedSwitchStatesBinary_0.charAt(i)).equals("1");
            }
            for (int i = 0; i < receivedSwitchStatesBinary_1.length(); i++) {
                switchStates[i + receivedSwitchStatesBinary_0.length()] = String.valueOf(receivedSwitchStatesBinary_1.charAt(i)).equals("1");
            }
        } catch (NumberFormatException ignore) {
        }
    }
}
