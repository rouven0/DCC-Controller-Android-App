package com.traincon.modelleisenbahn_controller;

import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;

public class Cab {
    private final BoardManager boardManager;
    private final CBusAsciiMessageBuilder cBusAsciiMessageBuilder;
    private String session;
    boolean isSession = false;
    private String speedDir = "00";
    int locoAddress = 0;


    public static void estop(BoardManager boardManager) {
        boardManager.send(new CBusAsciiMessageBuilder().build(new CBusMessage("RESTP", CBusMessage.NO_DATA)));
    }

    public Cab(BoardManager boardManager) {
        this.boardManager = boardManager;
        cBusAsciiMessageBuilder = new CBusAsciiMessageBuilder();
    }

    public void allocateSession(final int locoAddress){
        this.locoAddress = locoAddress;
        boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("RLOC", getHexAddress(locoAddress))));
    }
    public boolean sessionAllocated(CBusMessage message){
        if(message.getData()[1].equals(getHexAddress(locoAddress)[0]) && message.getData()[2].equals(getHexAddress(locoAddress)[1])) {
            session = message.getData()[0];
            speedDir = message.getData()[3];
            isSession = true;
        }
        return isSession;
    }

    public void releaseSession() {
        if(isSession){
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("KLOC", new String[]{session})));
            isSession = false;
            session = null;
        }
    }

    public void keepAlive() {
        if (isSession) {
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("DKEEP", new String[]{session})));
        }
    }

    public void setSpeedDir(int targetSpeedDir) {
        if(isSession) {
            if (targetSpeedDir > 0) {
                targetSpeedDir = targetSpeedDir +128;
            }
            speedDir = Integer.toHexString(Math.abs(targetSpeedDir)).toUpperCase();
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("DSPD", new String[]{session, speedDir})));
        }
    }

    public int getSpeedDir() {
        int intSpeedDir = Integer.valueOf(speedDir, 16);
        if (intSpeedDir>127) {
            intSpeedDir = intSpeedDir-128;
        } else {
            intSpeedDir = -intSpeedDir;
        }
        return intSpeedDir;
    }

    public void idle() {
        setSpeedDir(0);
    }

    public void setFunction(int function, boolean targetState) {
        if(targetState) {
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("DFNON", new String[]{session, "0"+Integer.toHexString(function).toUpperCase()})));
        } else {
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("DFNOF", new String[]{session, "0"+Integer.toHexString(function).toUpperCase()})));
        }
    }

    private String[] getHexAddress(int address) {
        //set the highest 2 bits to 1 with 2^15 + 2^14; The address input will be limited to 2*14 (16383)
        address = address + 49152;
        String hexAddress = Integer.toHexString(address).toUpperCase();
        return new String[]{hexAddress.substring(0,2), hexAddress.substring(2)};
    }

    public String getSession() {
        return session;
    }
}