package com.traincon.CBusMessage;

public class CBusAsciiMessageBuilder {
    /**
     *  The CBusMessage will be converted into Ascii
     * @param cBusMessage is given by the controlling units like Cab or AccessoryController
     * @return the frame that is sent to the board
     * @see CBusMessage
     */
    public static String build(CBusMessage cBusMessage){
        String eventAddress = cBusMessage.getEventAddress();
        StringBuilder data = new StringBuilder();
        for (int i=0; i<cBusMessage.getData().length; i++){
            data.append(cBusMessage.getData()[i]);
        }
        return ":S0C80N" + eventAddress + data + ";";
    }
}