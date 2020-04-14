package com.lti.mfa.data.model;

public class Session {

    private int userId;
    private String sessionId;
    private String mobileNo;
    private String displayName;

    public Session(int userId, String sessionId, String mobileNo, String displayName) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.mobileNo = mobileNo;
        this.displayName = displayName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


}
