package com.example.blooddonationorganization.Model;

import com.google.firebase.Timestamp;


public class RequestDataModel {
        private String city;
        private String username;
        private String request;
        private String imageUrl;
        private String userId;
        private String mobile;
        private Timestamp timeAdded;

    public RequestDataModel() {
    }

    public RequestDataModel(String city,String username,String request, String imageUrl, String userId, Timestamp timeAdded) {
        this.city = city;
        this.username = username;
        this.request = request;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timeAdded = timeAdded;
    }


    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getRequest() {
            return request;
        }

        public void setRequest(String request) {
            this.request = request;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Timestamp getTimeAdded() {
            return timeAdded;
        }

        public void setTimeAdded(Timestamp timeAdded) {
            this.timeAdded = timeAdded;
        }


}
