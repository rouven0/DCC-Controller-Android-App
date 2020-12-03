package com.traincon.modelleisenbahn_controller;

import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;

import java.io.IOException;

public class Cab {
    private final BoardManager boardManager;
    private final CBusAsciiMessageBuilder cBusAsciiMessageBuilder;
    private String session;
    private boolean isSession = false;
    private String speedDir = "00";


    public static void estop(BoardManager boardManager) {
        boardManager.send(new CBusAsciiMessageBuilder().build(new CBusMessage("RESTP", CBusMessage.NO_DATA)));
        //boardManager.receive(CBusAsciiMessageBuilder.getExpectedMessageLength(0));
    }

    public Cab(BoardManager boardManager) {
        this.boardManager = boardManager;
        cBusAsciiMessageBuilder = new CBusAsciiMessageBuilder();
    }


    public boolean allocateSession(final int locoAddress) throws InterruptedException {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("RLOC", getHexAddress(locoAddress))));
                    Thread.sleep(500);
                    CBusMessage answer = boardManager.getReceivedCBusMessage(boardManager.receive(boardManager.getSocketInputStream().available()));
                    if (answer.getEvent().equals("PLOC")) {
                        session = answer.getData()[0];
                        speedDir = answer.getData()[3];
                        isSession = true;
                    } else {
                        isSession = false;
                    }
                } catch (NullPointerException | InterruptedException | IOException e) {
                    isSession = false;
                }
            }
        });
        thread.start();
        thread.join();
        return isSession;
    }

    public void releaseSession() {
        boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("KLOC", new String[]{session})));
        isSession = false;
        session = null;
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
}