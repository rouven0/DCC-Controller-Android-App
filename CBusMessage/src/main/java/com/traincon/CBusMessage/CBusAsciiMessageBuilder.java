package com.traincon.CBusMessage;

import androidx.annotation.NonNull;

/*
 * Build Ascii-CBus messages that are sent to the board
 */

public class CBusAsciiMessageBuilder {

    public static int getExpectedMessageLength(int dataBytes){
        return 12+(2*dataBytes);
    }

    public CBusAsciiMessageBuilder() {
    }

    public String build(@NonNull CBusMessage cBusMessage){
        String eventAddress = cBusMessage.getEventAddress();
        StringBuilder data = new StringBuilder();
        for (int i=0; i<cBusMessage.getData().length; i++){
            data.append(cBusMessage.getData()[i]);
        }
        return ":S0C80N" + eventAddress + data + ";";
    }

}