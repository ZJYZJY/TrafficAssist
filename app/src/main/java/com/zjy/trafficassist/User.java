package com.zjy.trafficassist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZJY on 2016/4/14.
 */
public class User {

    private String username;
    private String password;
    private String nickname;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.nickname = username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
