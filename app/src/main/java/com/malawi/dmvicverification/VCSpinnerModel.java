package com.malawi.dmvicverification;

public class VCSpinnerModel {
    String memberCompanyName,memberCompanyId;

    public VCSpinnerModel(String memberCompanyId, String memberCompanyName) {
        this.memberCompanyName = memberCompanyName;
        this.memberCompanyId = memberCompanyId;
    }

    public String getMemberCompanyName() {
        return memberCompanyName;
    }

    public void setMemberCompanyName(String memberCompanyName) {
        this.memberCompanyName = memberCompanyName;
    }

    public String getMemberCompanyId() {
        return memberCompanyId;
    }

    public void setMemberCompanyId(String memberCompanyId) {
        this.memberCompanyId = memberCompanyId;
    }
}
