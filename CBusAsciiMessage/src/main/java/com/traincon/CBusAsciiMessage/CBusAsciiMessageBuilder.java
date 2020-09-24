package com.traincon.CBusAsciiMessage;

/*
 * Build ascii CBus messages that are sent to the board
 * For more information about the events read the CBUS specification
 */

@SuppressWarnings("unused")
public class CBusAsciiMessageBuilder {
    /*
     * All events and other values are declared here
     * The Reader will also use these
     */

    //Expected Message Length
    public final static int EML_0 = 12;
    public final static int EML_1 = 14;
    public final static int EML_2 = 16;
    public final static int EML_3 = 18;
    public final static int EML_4 = 20;
    public final static int EML_5 = 22;
    public final static int EML_6 = 24;
    public final static int EML_7 = 26;

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
    public final static String EVENT_0_RSTAT = "0C";
    public final static String EVENT_0_QNN = "0D";
    public final static String EVENT_0_RQNP = "10";
    public final static String EVENT_0_RQMN = "11";

    //1 data bytes Packages
    public final static String EVENT_1_KLOC = "21";
    public final static String EVENT_1_QLOC = "22";
    public final static String EVENT_1_DKEEP = "23";
    public final static String EVENT_1_DBG1 = "30";
    public final static String EVENT_1_EXTC = "3F";

    //2 data bytes Packages
    public final static String EVENT_2_RLOC = "40";
    public final static String EVENT_2_SNN = "42";
    public final static String EVENT_2_STMOD = "44";
    public final static String EVENT_2_PCOM = "45";
    public final static String EVENT_2_KCON = "46";
    public final static String EVENT_2_DSPD = "47";
    public final static String EVENT_2_DFLG = "48";
    public final static String EVENT_2_DFNON = "49";
    public final static String EVENT_2_DFNOF = "4A";
    public final static String EVENT_2_SSTAT = "4C";
    public final static String EVENT_2_RQNN = "50";
    public final static String EVENT_2_NNREL = "51";
    public final static String EVENT_2_NNACK = "52";
    public final static String EVENT_2_NNLRN = "53";
    public final static String EVENT_2_NNULN = "54";
    public final static String EVENT_2_NNCLR = "55";
    public final static String EVENT_2_NNEVN = "56";
    public final static String EVENT_2_NERD = "57";
    public final static String EVENT_2_RQEVN = "58";
    public final static String EVENT_2_WRACK = "59";
    public final static String EVENT_2_RQDAT = "5A";
    public final static String EVENT_2_RQDDS = "5B";
    public final static String EVENT_2_BOOTM = "5C";
    public final static String EVENT_2_ENUM = "5D";
    public final static String EVENT_2_EXTC1 = "5F";

    //3 data bytes Packages
    public final static String EVENT_3_DFUN = "60";
    public final static String EVENT_3_GLOC = "61";
    public final static String EVENT_3_ERR = "63";
    public final static String EVENT_3_CMDERR = "6F";
    public final static String EVENT_3_EVNLF = "70";
    public final static String EVENT_3_NVRD = "71";
    public final static String EVENT_3_NENRD = "72";
    public final static String EVENT_3_RQNPN = "73";
    public final static String EVENT_3_NUMEV = "74";
    public final static String EVENT_3_CANID = "75";
    public final static String EVENT_3_EXTC2 = "7F";

    //4 data bytes Packages
    public final static String EVENT_4_ASON = "98";
    public final static String EVENT_4_ASOF = "99";

    //5 data bytes Packages

    //6 data bytes Packages

    //7 data bytes Packages

    private final String canId;

    public CBusAsciiMessageBuilder(String canid) {
        canId = canid;
    }

    public String build(String eventAddress) {
        return ":S" + canId + "N" + eventAddress + ";";
    }

    public String build(String eventAddress, String dat1) {
        return ":S" + canId + "N" + eventAddress + dat1 + ";";
    }

    public String build(String eventAddress, String dat1, String dat2) {
        return ":S" + canId + "N" + eventAddress + dat1 + dat2 + ";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3) {
        return ":S" + canId + "N" + eventAddress + dat1 + dat2 + dat3 + ";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3, String dat4) {
        return ":S" + canId + "N" + eventAddress + dat1 + dat2 + dat3 + dat4 + ";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3, String dat4, String dat5) {
        return ":S" + canId + "N" + eventAddress + dat1 + dat2 + dat3 + dat4 + dat5 + ";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3, String dat4, String dat5, String dat6) {
        return ":S" + canId + "N" + eventAddress + dat1 + dat2 + dat3 + dat4 + dat5 + dat6 + ";";
    }

    public String build(String eventAddress, String dat1, String dat2, String dat3, String dat4, String dat5, String dat6, String dat7) {
        return ":S" + canId + "N" + eventAddress + dat1 + dat2 + dat3 + dat4 + dat5 + dat6 + dat7 + ";";
    }


}