package com.example.blooddonationorganization.Util;

import android.app.Application;

public class DonorsApi extends Application {
    private String username;
    private String city;
    private String userId;
    private String bloodGrp;
    private String mobile;
    private String email;
    private String password;
    private static DonorsApi instance;

    public static DonorsApi getInstance() {
        if (instance == null)
            instance = new DonorsApi();
        return instance;

    }

    public DonorsApi() {}

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBloodGrp() {
        return bloodGrp;
    }

    public void setBloodGrp(String bloodGrp) {
        this.bloodGrp = bloodGrp;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
