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
    private String accidentTags;
    private ArrayList<Bitmap> pictures;
    private ArrayList<File> files;
    private LatLng location;

    public AlarmHistory(String accidentTags, String nickname,
                        String username) {
        this.accidentTags = accidentTags;
        this.nickname = nickname;
        this.username = username;
    }

    // 用于构造
    public AlarmHistory(String accidentTags, String nickname,
                        String username, ArrayList<Bitmap> pictures) {
        this(accidentTags, nickname, username);
        this.pictures = pictures;
    }

    // 用于构造上传时的数据
    public AlarmHistory(String accidentTags, String nickname,
                        String username, ArrayList<File> files, LatLng location) {
        this(accidentTags, nickname, username);
        this.files = files;
        this.location = location;
    }

    public void setaccidentTags(String accidentTags) {
        this.accidentTags = accidentTags;
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

    public String getaccidentTags() {
        return accidentTags;
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
