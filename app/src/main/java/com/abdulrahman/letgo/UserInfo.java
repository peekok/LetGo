package com.abdulrahman.letgo;

public class UserInfo {
    private String userFirstName = "";
    private String userLastName = "";
    private String userEmail = "";
    private String userUID = "";
    private String userMoney = "";
    public UserInfo() {
    }
    public String getUserFirstName() {
        return userFirstName;
    }
    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }
    public String setUserLastName() {
        return userLastName;
    }
    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getUserMoney() {
        return userMoney;
    }
    public void setUserMoney(String userMoney) {
        this.userMoney = userMoney;
    }
    public String getUserUID() {
        return userUID;
    }
    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
