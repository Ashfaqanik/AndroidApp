package com.example.blooddonationorganization.Model;

public class DonorDataModel {
    private String username;
    private String city;
    private String bloodGrp;
    private String mobile;
    private String email;


    public DonorDataModel() {
    }

    public DonorDataModel(String username, String city, String bloodGrp, String mobile, String email) {
        this.username = username;
        this.city = city;
        this.bloodGrp = bloodGrp;
        this.mobile = mobile;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
