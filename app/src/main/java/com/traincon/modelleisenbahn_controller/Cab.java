package com.traincon.modelleisenbahn_controller;

import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;

/**
 * The cab is the controlling unit
 * It is able to allocate a session and control any locos the command station can control
 * It communicates with the boardManager and CBusMessages
 * @see BoardManager
 * @see CBusMessage
 */
public class Cab {
    private final BoardManager boardManager;
    private final boolean[] functions = new boolean[12];
    boolean hasSession = false;

    /**
     * Address of the current controlled loco
     */
    int locoAddress;

    /**
     * Session number for targeting the commands
     */
    private String session;
    private String speedDir = "00";


    public Cab(BoardManager boardManager) {
        this.boardManager = boardManager;
    }

    /**
     * Requests an emergency stop
     * @param boardManager is required to send a message
     */
    public static void estop(BoardManager boardManager) {
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("RESTP", CBusMessage.NO_DATA)));
    }

    /**
     * Resets all nodes in case of a crash
     * @param boardManager is required to send a message
     */
    public static void reset(BoardManager boardManager){
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("ARST", CBusMessage.NO_DATA)));
    }

    /**
     * Allocates a session to s specific loco
     * With this session all commands are sent to the right loco
     * @param locoAddress says which loco should be controlled
     */
    public void allocateSession(final int locoAddress) {
        this.locoAddress = locoAddress;
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("RLOC", getHexAddress(locoAddress))));
    }

    /**
     * This reads all data from the allocation message and applies it
     * @param cBusMessage RLOC message received by the boardManager
     * @return When the allocated session was requested by this cab the UI will show it
     */
    public boolean onSessionAllocated(CBusMessage cBusMessage) {
        if (cBusMessage.getData()[1].equals(getHexAddress(locoAddress)[0]) && cBusMessage.getData()[2].equals(getHexAddress(locoAddress)[1])) {
            session = cBusMessage.getData()[0];
            speedDir = cBusMessage.getData()[3];
            hasSession = true;
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
        return hasSession;
    }

    public void releaseSession() {
        if (hasSession) {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("KLOC", new String[]{session})));
            hasSession = false;
            session = null;
            locoAddress = 0;
        }
    }

    /**
     * Sends DKEEP frames to keep the session alive
     * When something crashes and no DKEEP is received by the command station this session will be released
     */
    public void keepAlive() {
        if (hasSession) {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("DKEEP", new String[]{session})));
        }
    }

    /**
     * @return Returns the speedDir as an integer to display it in the UI
     */
    public int getSpeedDir() {
        int intSpeedDir = Integer.valueOf(speedDir, 16);
        if (intSpeedDir > 127) {
            intSpeedDir = intSpeedDir - 128;
        } else {
            intSpeedDir = -intSpeedDir;
        }
        return intSpeedDir;
    }

    /**
     * Sets the speedDir and sends it to the board
     * @param targetSpeedDir speed that the Loco should have
     */
    public void setSpeedDir(int targetSpeedDir) {
        if (hasSession && Math.abs(targetSpeedDir) != 1) {
            if (targetSpeedDir > -1) {
                targetSpeedDir = targetSpeedDir + 128;
            }
            StringBuilder newSpeedDir = new StringBuilder(Integer.toHexString(Math.abs(targetSpeedDir)).toUpperCase());
            if(newSpeedDir.length()<2){
                newSpeedDir.insert(0, "0");
            }
            speedDir = newSpeedDir.toString();
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("DSPD", new String[]{session, speedDir})));
        }
    }

    /**
     * Stop with delay
     */
    public void idle() {
        setSpeedDir(0);
    }

    public void stop(){
        boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("DSPD", new String[]{session, "01"})));
    }

    public void setFunction(int number, boolean targetState) {
        functions[number] = targetState;
        if (targetState) {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("DFNON", new String[]{session, "0" + Integer.toHexString(number).toUpperCase()})));
        } else {
            boardManager.send(CBusAsciiMessageBuilder.build(new CBusMessage("DFNOF", new String[]{session, "0" + Integer.toHexString(number).toUpperCase()})));
        }
    }

    /**
     * This method converts the integer address to a Hex string
     * When the int address is bigger that 127 (long address), the two highest bytes will be set 1
     * @param address Integer address given by the user
     * @return HexString for the CBUS protocol
     */
    private String[] getHexAddress(int address) {
        if(address > 127){
            //set the highest 2 bits to 1 with 2^15 + 2^14; The address input will be limited to 2*14 (16383)
            address = address + 49152;
        }
        StringBuilder hexAddress = new StringBuilder(Integer.toHexString(address).toUpperCase());
        while(hexAddress.length() < 4){
            hexAddress.insert(0, "0");
        }
        return new String[]{hexAddress.substring(0, 2), hexAddress.substring(2)};
    }

    public String getSession() {
        return session;
    }

    public boolean[] getFunctions() {
        return functions;
    }
}