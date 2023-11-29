package com.hazardalert.app.az;

public class Users {


    String email, profileImageUrl,fullName;


    public Users() {
    }

    public Users(String email, String profileImageUrl, String fullName) {
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
