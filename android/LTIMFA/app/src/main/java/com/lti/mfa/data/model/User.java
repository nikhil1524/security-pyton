package com.lti.mfa.data.model;

public class User {

    private String mobileNo;
    private String password;

    public User(String mobileNo, String password) {
        this.mobileNo = mobileNo;
        this.password = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
