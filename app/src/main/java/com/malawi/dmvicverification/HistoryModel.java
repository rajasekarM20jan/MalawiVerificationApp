package com.malawi.dmvicverification;

public class HistoryModel {
    String RegNo,ChaNo;

    public HistoryModel(String regNo, String chaNo) {
        RegNo = regNo;
        ChaNo = chaNo;
    }

    public String getRegNo() {
        return RegNo;
    }

    public void setRegNo(String regNo) {
        RegNo = regNo;
    }

    public String getChaNo() {
        return ChaNo;
    }

    public void setChaNo(String chaNo) {
        ChaNo = chaNo;
    }
}
