package com.traincon.CBusAsciiMessage;

//------------------------------------
//Build ascii CBus messages that are sent to the board
//For more information about the events read the CBUS specification
//------------------------------------

@SuppressWarnings("unused")
public class CBusAsciiMessageBuilder {
    //0 data bytes Packages
    public final static String EVENT_0_ACK = "00";
    public final static String EVENT_0_NAK = "01";
    public final static String EVENT_0_HLT = "02";
    public final static String EVENT_0_BON = "03";
    public final static String EVENT_0_TOF = "04";
    public final static String EVENT_0_TON = "05";
    public final static String EVENT_0_ESTOP = "06";
    public final static String EVENT_0_ARST = "07";
    public final static String EVENT_0_RTOF = "08";
    public final static String EVENT_0_RTON = "09";
    public final static String EVENT_0_RESTP = "0A";
    //0B: reserved
    public final static String EVENT_0_RSTAT = "0C";
    public final static String EVENT_0_QNN = "0D";
    //0E+0F: reserved
    public final static String EVENT_0_RQNP = "10";
    public final static String EVENT_0_RQMN = "11";
    //12-1F: reserved

    //1 data bytes Packages
    //20: reserved
    public final static String EVENT_1_KLOC = "21";
    public final static String EVENT_1_QLOC = "22";
    public final static String EVENT_1_DKEEP = "23";
    //24-2F: reserved
    public final static String EVENT_1_DBG1 = "30";
    //31-3E: reserved
    public final static String EVENT_1_EXTC = "3F";

    //2 data bytes Packages

    //3 data bytes Packages
    public final static String EVENT_3_NVRD = "71";

    //4 data bytes Packages
    public final static String EVENT_4_ASON = "98";
    public final static String EVENT_4_ASOF = "99";

    //5 data bytes Packages

    //6 data bytes Packages

    //7 data bytes Packages

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

    public String build(String eventAddress, String dat1, String dat2, String dat3, String dat4, String dat5){
        return ":S"+canId+"N"+eventAddress+dat1+dat2+dat3+dat4+dat5+";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3, String dat4, String dat5, String dat6){
        return ":S"+canId+"N"+eventAddress+dat1+dat2+dat3+dat4+dat5+dat6+";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3, String dat4, String dat5, String dat6, String dat7){
        return ":S"+canId+"N"+eventAddress+dat1+dat2+dat3+dat4+dat5+dat6+dat7+";";
    }


}