package com.zjy.trafficassist.model;

import android.graphics.Bitmap;

import com.amap.api.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ZJY on 2016/4/20.
 * 报警记录信息
 */
public class AlarmHistory {

    private int id;
    private String nickname;
    private String username;
    private String detail;
    private ArrayList<Bitmap> pictures;
    private ArrayList<File> files;
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
                        String username, ArrayList<Bitmap> pictures) {
        this(isSerious, detail, nickname, username);
        this.pictures = pictures;
    }

    // 用于构造上传时的数据
    public AlarmHistory(boolean isSerious, String detail, String nickname,
                        String username, ArrayList<File> files, LatLng location) {
        this(isSerious, detail, nickname, username);
        this.files = files;
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

    public void setPicture(ArrayList<Bitmap> bitmaps) {
        pictures = bitmaps;
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

    public ArrayList<Bitmap> getPictures() {
        return pictures;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }
}
