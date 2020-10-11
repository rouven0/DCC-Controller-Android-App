package com.traincon.CBusMessage;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class CBusMessage {
    /*
     * All events are declared here
     * For more information read the CBUS specification
     */

    //<editor-fold desc="Event Strings">
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
    //</editor-fold>

    public String event;
    public String[] data;

    public CBusMessage(String event, String[] data) {
        this.event = event;
        this.data = data;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getLenght() {
        return data.length;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }
}
