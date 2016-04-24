package com.zjy.trafficassist;

/**
 * Created by ZJY on 2016/4/20.
 */
public class AlarmHistory {

    private int id;
    private String username;
    private String detail;
    private boolean isSerious;

    public AlarmHistory(int id, boolean isSerious, String detail, String username) {
        this.id = id;
        this.isSerious = isSerious;
        this.detail = detail;
        this.username = username;
    }

    public void setSerious(boolean serious) {
        isSerious = serious;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getUsername() {
        return username;
    }
}
