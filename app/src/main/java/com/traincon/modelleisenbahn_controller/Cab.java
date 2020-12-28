package com.traincon.modelleisenbahn_controller;

import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;

public class Cab {
    private final BoardManager boardManager;
    private final boolean[] functions = new boolean[12];
    boolean isSession = false;
    int locoAddress;
    private String session;
    private String speedDir = "00";


    public Cab(BoardManager boardManager) {
        this.boardManager = boardManager;
    }

    public static void estop(BoardManager boardManager) {
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("RESTP", CBusMessage.NO_DATA)));
    }

    public static void reset(BoardManager boardManager){
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("ARST", CBusMessage.NO_DATA)));
    }

    public void allocateSession(final int locoAddress) {
        this.locoAddress = locoAddress;
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("RLOC", getHexAddress(locoAddress))));
    }

    public boolean onSessionAllocated(CBusMessage cBusMessage) {
        if (cBusMessage.getData()[1].equals(getHexAddress(locoAddress)[0]) && cBusMessage.getData()[2].equals(getHexAddress(locoAddress)[1])) {
            session = cBusMessage.getData()[0];
            speedDir = cBusMessage.getData()[3];
            isSession = true;
            StringBuilder[] byteCodes = new StringBuilder[3];
            for (int i = 0; i < byteCodes.length; i++) {
                byteCodes[i] = new StringBuilder(Integer.toBinaryString(Integer.parseInt(cBusMessage.getData()[4 + i], 16)));
                while (byteCodes[i].length() < 8) {
                    byteCodes[i].insert(0, "0");
                }
                byteCodes[i].reverse();
            }
            for (int i = 0; i < functions.length; i++) {
                if (i == 0) {
                    functions[i] = String.valueOf(byteCodes[0].charAt(4)).equals("1");
                } else if (i < 5) {
                    functions[i] = String.valueOf(byteCodes[0].charAt(i-1)).equals("1");
                } else if (i < 9) {
                    functions[i] = String.valueOf(byteCodes[1].charAt(i - 5)).equals("1");
                } else {
                    functions[i] = String.valueOf(byteCodes[2].charAt(i - 9)).equals("1");
                }
            }
        }
        return isSession;
    }

    public void releaseSession() {
        if (isSession) {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("KLOC", new String[]{session})));
            isSession = false;
            session = null;
            locoAddress = 0;
        }
    }

    public void keepAlive() {
        if (isSession) {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("DKEEP", new String[]{session})));
        }
    }

    public int getSpeedDir() {
        int intSpeedDir = Integer.valueOf(speedDir, 16);
        if (intSpeedDir > 127) {
            intSpeedDir = intSpeedDir - 128;
        } else {
            intSpeedDir = -intSpeedDir;
        }
        return intSpeedDir;
    }

    public void setSpeedDir(int targetSpeedDir) {
        if (isSession) {
            if (targetSpeedDir > 0) {
                targetSpeedDir = targetSpeedDir + 128;
            }
            speedDir = Integer.toHexString(Math.abs(targetSpeedDir)).toUpperCase();
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("DSPD", new String[]{session, speedDir})));
        }
    }

    public void idle() {
        setSpeedDir(0);
    }

    public void setFunction(int number, boolean targetState) {
        functions[number] = targetState;
        if (targetState) {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("DFNON", new String[]{session, "0" + Integer.toHexString(number).toUpperCase()})));
        } else {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("DFNOF", new String[]{session, "0" + Integer.toHexString(number).toUpperCase()})));
        }
    }

    private String[] getHexAddress(int address) {
        //set the highest 2 bits to 1 with 2^15 + 2^14; The address input will be limited to 2*14 (16383)
        address = address + 49152;
        String hexAddress = Integer.toHexString(address).toUpperCase();
        return new String[]{hexAddress.substring(0, 2), hexAddress.substring(2)};
    }

    public String getSession() {
        return session;
    }

    public boolean[] getFunctions() {
        return functions;
    }
}