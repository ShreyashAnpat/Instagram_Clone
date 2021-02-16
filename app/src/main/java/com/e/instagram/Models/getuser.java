package com.e.instagram.Models;

public class getuser {
    String Name ,Profile,userName;

    public getuser(String name, String profile, String userName) {
        Name = name;
        Profile = profile;
        this.userName = userName;
    }

    public getuser() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
