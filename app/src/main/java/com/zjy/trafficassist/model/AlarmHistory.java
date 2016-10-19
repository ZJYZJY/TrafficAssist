package com.zjy.trafficassist.model;

import android.graphics.Bitmap;

import com.amap.api.maps.model.LatLng;

import java.io.File;

/**
 * Created by ZJY on 2016/4/20.
 * 报警记录信息
 */
public class AlarmHistory {

    private int id;
    private String nickname;
    private String username;
    private String detail;
    private Bitmap picture;
    private File file;
    private LatLng location;
    private boolean isSerious;
    public static String serious;

    public AlarmHistory(boolean isSerious, String detail, String nickname,
                        String username) {
        this.isSerious = isSerious;
        this.detail = detail;
        this.nickname = nickname;
        this.username = username;
        if(isSerious) {
            serious = "true";
        } else {
            serious = "false";
        }
    }

    // 用于构造
    public AlarmHistory(boolean isSerious, String detail, String nickname,
                        String username, Bitmap picture) {
        this(isSerious, detail, nickname, username);
        this.picture = picture;
    }

    // 用于构造上传时的数据
    public AlarmHistory(boolean isSerious, String detail, String nickname,
                        String username, File file, LatLng location) {
        this(isSerious, detail, nickname, username);
        this.file = file;
        this.location = location;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPicture(Bitmap bitmap) {
        picture = bitmap;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
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

    public String getUsername() {
        return username;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
