package com.traincon.CBusMessage;

import androidx.annotation.NonNull;

/*
 * Build Ascii-CBus messages that are sent to the board
 */

@SuppressWarnings({"unused", "RedundantSuppression"})
public class CBusAsciiMessageBuilder {

    //<editor-fold desc="Expected message lenght">
    public final static int EML_0 = 12;
    public final static int EML_1 = 14;
    public final static int EML_2 = 16;
    public final static int EML_3 = 18;
    public final static int EML_4 = 20;
    public final static int EML_5 = 22;
    public final static int EML_6 = 24;
    public final static int EML_7 = 26;
    //</editor-fold>

    private final String canId;

    public CBusAsciiMessageBuilder(String canid) {
        canId = canid;
    }

    public String build(@NonNull CBusMessage cBusMessage){
        StringBuilder data = new StringBuilder();
        for (int i=0; i<cBusMessage.getData().length; i++){
            data.append(cBusMessage.getData()[i]);
        }
        return ":S" + canId + "N" + cBusMessage.getEvent() + data + ";";
    }

}