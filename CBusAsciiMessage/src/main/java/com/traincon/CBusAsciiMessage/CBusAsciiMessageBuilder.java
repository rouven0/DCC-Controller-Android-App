package com.traincon.CBusAsciiMessage;

public class CBusAsciiMessageBuilder {
    public final static String EVENT_NVRD = "71";
    public final static String EVENT_ASON = "98";
    public final static String EVENT_ASOF = "99";

    private final String canId;

    public CBusAsciiMessageBuilder(String canid){
        canId = canid;
    }

    public String build(String eventAddress){
        return ":S"+canId+"N"+eventAddress+";";
    }

    public String build(String eventAddress, String dat1){
        return ":S"+canId+"N"+eventAddress+dat1+";";
    }

    public String build(String eventAddress, String dat1, String dat2){
        return ":S"+canId+"N"+eventAddress+dat1+dat2+";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3){
        return ":S"+canId+"N"+eventAddress+dat1+dat2+dat3+";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3, String dat4){
        return ":S"+canId+"N"+eventAddress+dat1+dat2+dat3+dat4+";";
    }


}