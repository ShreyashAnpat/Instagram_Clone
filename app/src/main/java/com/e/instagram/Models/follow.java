package com.e.instagram.Models;

public class follow {
    String userID ,user_name ,profile;

    public follow(String userID, String user_name, String profile) {
        this.userID = userID;
        this.user_name = user_name;
        this.profile = profile;
    }

    public follow() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUser_name() {
        return user_name;
    }

    public String setUser_name(String user_name) {
        this.user_name = user_name;
        return user_name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
