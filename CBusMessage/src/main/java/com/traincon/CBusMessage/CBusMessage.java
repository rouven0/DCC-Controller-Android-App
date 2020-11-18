package com.traincon.CBusMessage;

public class CBusMessage {
    public static final String[] NO_DATA = new String[0];

    public String eventAddress;
    public String[] data;

    public CBusMessage(String event, String[] data) {
        if (event.length() == 2) {
            this.eventAddress = event;
        } else {
            this.eventAddress = getAddressByEvent(event);
        }
        this.data = data;
    }

    public static String getAddressByEvent(String event) {
        return getEventStringArray()[indexOfEvent(event)][1];
    }

    public static String getEventByAddress(String address) {
        return getEventStringArray()[indexOfAddress(address)][0];
    }

    private static String[][] getEventStringArray() {
        return new String[][]
                {
                        //0 data bytes
                        {"ACK", "00"},
                        {"NAK", "01"},
                        {"HLT", "02"},
                        {"BON", "03"},
                        {"TOF", "04"},
                        {"TON", "05"},
                        {"ESTOP", "06"},
                        {"ARST", "07"},
                        {"RTOF", "08"},
                        {"RTON", "09"},
                        {"RESTP", "0A"},
                        {"RSTAT", "0C"},
                        {"QNN", "0D"},
                        {"RQNP", "10"},
                        {"RQMN", "11"},

                        //1 data byte
                        {"KLOC", "21"},
                        {"QLOC", "022"},
                        {"DKEEP", "023"},
                        {"DBG1", "30"},
                        {"EXTC", "3F"},

                        //2 data bytes
                        {"RLOC", "40"},
                        {"SNN", "42"},
                        {"STMOD", "44"},
                        {"PCOM", "45"},
                        {"KCON", "46"},
                        {"DSPD", "47"},
                        {"DFLG", "48"},
                        {"DFNON", "49"},
                        {"DFNOF", "4A"},
                        {"SSTAT", "4C"},
                        {"RQNN", "50"},
                        {"NNREL", "51"},
                        {"NNACK", "52"},
                        {"NNLRN", "53"},
                        {"NNULN", "54"},
                        {"NNCLR", "55"},
                        {"NNEVN", "56"},
                        {"NERD", "57"},
                        {"RQENV", "58"},
                        {"WRACK", "59"},
                        {"RQDAT", "5A"},
                        {"RQDDS", "5B"},
                        {"BOOTM", "5C"},
                        {"ENUM", "5D"},
                        {"EXTC1", "5F"},

                        //3 data bytes
                        {"DFUN", "60"},
                        {"GLOC", "61"},
                        {"ERR", "63"},
                        {"CMDERR", "6F"},
                        {"EVNLF", "70"},
                        {"NVRD", "71"},
                        {"NENRD", "72"},
                        {"RQNPN", "73"},
                        {"NUMEV", "74"},
                        {"CANID", "75"},
                        {"EXTC2", "7F"},

                        //4 data bytes
                        {"RDCC3", "80"},
                        {"WCVO", "82"},
                        {"WCVB", "83"},
                        {"QCVS", "84"},
                        {"PCVS", "85"},
                        {"ACON", "90"},
                        {"ACOF", "91"},
                        {"AREC", "92"},
                        {"ARON", "93"},
                        {"AROF", "94"},
                        {"EVULN", "95"},
                        {"NVSET", "96"},
                        {"NVANS", "97"},
                        {"ASON", "98"},
                        {"ASOF", "99"},
                        {"ASRQ", "9A"},
                        {"PARAN", "9B"},
                        {"REVAL", "9C"},
                        {"ARSON", "9D"},
                        {"ARSOF", "9E"},
                        {"EXTC3", "9F"},

                        //5 data bytes
                        {"RDCC4", "A0"},
                        {"WCVS", "A2"},
                        {"ACON1", "B0"},
                        {"ACOF1", "B1"},
                        {"REQEV", "B2"},
                        {"ARON1", "B3"},
                        {"AROF1", "B4"},
                        {"NEVAL", "B5"},
                        {"PNN", "B6"},
                        {"ASON1", "B8"},
                        {"ASOF1", "B9"},
                        {"ARSON1", "BD"},
                        {"ARSOF1", "BE"},
                        {"EXTC4", "BF"},

                        //6 data bytes
                        {"RDCC5", "C0"},
                        {"WCVOA", "C1"},
                        {"FCLK", "CF"},
                        {"ACOF2", "D0"},
                        {"ACOF2", "D1"},
                        {"EVLRN", "D2"},
                        {"EVANS", "D3"},
                        {"ARON1", "D4"},
                        {"AROF2", "D5"},
                        {"ASON2", "D8"},
                        {"ASOF2", "D9"},
                        {"ARSON2", "DD"},
                        {"ARSOF2", "DE"},
                        {"EXTC5", "DF"},

                        //7 data bytes
                        {"RDCC6", "E0"},
                        {"PLOC", "E1"},
                        {"NAME", "E2"},
                        {"STAT", "E3"},
                        {"PARAMS", "EF"},
                        {"ACON3", "F0"},
                        {"ACOF3", "F1"},
                        {"ENRSP", "F2"},
                        {"ARON3", "F3"},
                        {"AROF3", "F4"},
                        {"EVLRN", "F5"},
                        {"ACDAT", "F6"},
                        {"ARDAT", "F7"},
                        {"ASON3", "F8"},
                        {"ASOF3", "F9"},
                        {"DDES", "FA"},
                        {"DDRS", "FB"},
                        {"ARSON3", "FD"},
                        {"ARSOF3", "FE"},
                        {"EXTC6", "FF"}
                };
    }

    private static int indexOfEvent(String targetEvent) {
        boolean found = false;
        int count = 0;
        while (!found) {
            count = count + 1;
            if (count == getEventStringArray().length) {
                count = 0;
                break;
            }
            if (getEventStringArray()[count][0].equals(targetEvent)) {
                found = true;
            }
        }
        return count;
    }


    private static int indexOfAddress(String targetAddress) {
        boolean found = false;
        int count = 0;
        while (!found) {
            count = count + 1;
            if (count == getEventStringArray().length-1) {
                count = 0;
                break;
            }
            if (getEventStringArray()[count][1].equals(targetAddress)) {
                found = true;
            }
        }
        return count;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public String getEvent(){
        return getEventByAddress(eventAddress);
    }

    public void setEvent(String event) {
        if (event.length() == 2) {
            this.eventAddress = event;
        } else {
            this.eventAddress = getAddressByEvent(event);
        }
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }
}
