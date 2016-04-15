package com.zjy.trafficassist;

/**
 * Created by ZJY on 2016/4/14.
 */
public class User {

    private String username;
    private String password;

    public void User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
