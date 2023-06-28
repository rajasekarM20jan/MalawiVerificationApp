package com.malawi.dmvicverification;

public class HistoryModel {
    String RegNo,ChaNo,certNo,status;

    public HistoryModel(String regNo, String chaNo, String certNo, String status) {
        RegNo = regNo;
        ChaNo = chaNo;
        this.certNo = certNo;
        this.status = status;
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

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
