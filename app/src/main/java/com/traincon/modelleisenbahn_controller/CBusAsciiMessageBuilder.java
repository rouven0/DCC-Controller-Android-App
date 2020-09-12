package com.traincon.modelleisenbahn_controller;

public class CBusAsciiMessageBuilder {
    public final static String EVENT_NVRD = "71";
    public final static String EVENT_ASON = "98"; //Todo add more
    public final static String EVENT_ASOF = "99";

    private final String canId;
    public CBusAsciiMessageBuilder(String canid){
        canId = canid;
    }

// --Commented out by Inspection START (12.09.20 12:08):
//    public String build(String eventAddress, String dat1){
//        return ":S"+canId+"N"+eventAddress+dat1+";";
//    }
// --Commented out by Inspection STOP (12.09.20 12:08)

// --Commented out by Inspection START (12.09.20 12:09):
//    public String build(String eventAddress, String dat1, String dat2){
//        return ":S"+canId+"N"+eventAddress+dat1+dat2+";";
//    }
// --Commented out by Inspection STOP (12.09.20 12:09)

    public String build(String eventAddress, String dat1, String dat2, String dat3){
        return ":S"+canId+"N"+eventAddress+dat1+dat2+dat3+";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3, String dat4){
        return ":S"+canId+"N"+eventAddress+dat1+dat2+dat3+dat4+";";
    }



}
