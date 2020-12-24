package com.traincon.CBusMessage;

public class CBusAsciiMessageBuilder {
    public static String build(CBusMessage cBusMessage){
        String eventAddress = cBusMessage.getEventAddress();
        StringBuilder data = new StringBuilder();
        for (int i=0; i<cBusMessage.getData().length; i++){
            data.append(cBusMessage.getData()[i]);
        }
        return ":S0C80N" + eventAddress + data + ";";
    }
}