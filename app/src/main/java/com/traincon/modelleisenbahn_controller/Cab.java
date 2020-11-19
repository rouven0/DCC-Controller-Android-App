package com.traincon.modelleisenbahn_controller;

import com.traincon.CBusMessage.CBusAsciiMessageBuilder;
import com.traincon.CBusMessage.CBusMessage;

import java.io.IOException;

public class Cab {
    private final BoardManager boardManager;
    private final CBusAsciiMessageBuilder cBusAsciiMessageBuilder;
    private String session;
    private boolean isSession = false;

    public Cab(BoardManager boardManager) {
        this.boardManager = boardManager;
        cBusAsciiMessageBuilder = new CBusAsciiMessageBuilder();
    }


    public boolean allocateSession() throws InterruptedException {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("RLOC", new String[]{"E3", "29"})));
                    Thread.sleep(500);
                    String answerFrame = boardManager.receive(boardManager.getSocketInputStream().available());
                    CBusMessage answer = boardManager.getReceivedCBusMessage(answerFrame);
                    if (answer.getEvent().equals("PLOC")) {
                        session = answer.getData()[0];
                        //String speedDir = answer.getData()[3];
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

    public void setSpeedDir(int targetSpeed) {
        if(isSession) {
            if (targetSpeed < 0) {
                targetSpeed = targetSpeed * -1;
            } else {
                targetSpeed = targetSpeed + 128;
            }
            String hexSpeed = Integer.toHexString(targetSpeed);
            boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("DSPD", new String[]{session, hexSpeed})));
        }
    }

    public void idle() {
        setSpeedDir(0);
    }

    public void estop() {
        boardManager.send(cBusAsciiMessageBuilder.build(new CBusMessage("RESTP", CBusMessage.NO_DATA)));
    }

}
