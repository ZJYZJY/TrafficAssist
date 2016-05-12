package com.zjy.trafficassist.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by ZJY on 2016/4/20.
 * 报警记录信息
 */
public class AlarmHistory {

    private int id;
    private String nickname;
    private String username;
    private ArrayList<byte[]> picture;
    private byte[] pic;
    private String detail;
    private boolean isSerious;
    public static String serious;

    public AlarmHistory(boolean isSerious, String detail, String nickname,
                        String username, ArrayList<byte[]> picture) {
        this.isSerious = isSerious;
        this.detail = detail;
        this.nickname = nickname;
        this.username = username;
        this.picture = picture;
        if(isSerious) {
            serious = "true";
        } else {
            serious = "false";
        }
    }

    public AlarmHistory(boolean isSerious, String detail, String nickname,
                        String username, byte[] picture) {
        this.isSerious = isSerious;
        this.detail = detail;
        this.nickname = nickname;
        this.username = username;
        this.pic = picture;
        if(isSerious) {
            serious = "true";
        } else {
            serious = "false";
        }
    }

    public void setSerious(boolean serious) {
        isSerious = serious;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isSerious() {
        return isSerious;
    }

    public String getDetail() {
        return detail;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<byte[]> getPicture() {
        return picture;
    }

    public void setPicture(ArrayList<byte[]> picture) {
        this.picture = picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPic() {
        return pic;
    }
}
