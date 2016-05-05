package com.zjy.trafficassist;

/**
 * Created by ZJY on 2016/4/20.
 * 报警记录信息
 */
public class AlarmHistory {

    private int id;
    private String username;
    private String detail;
    private boolean isSerious;
    private String serious;

//    public AlarmHistory(AlarmHistory alarmHistory) {
//        this.isSerious = alarmHistory.isSerious();
//        this.detail = alarmHistory.getDetail();
//        this.username = alarmHistory.getUsername();
//        if(isSerious) {
//            serious = "true";
//        } else {
//            serious = "false";
//        }
//    }

    public AlarmHistory(boolean isSerious, String detail, String username) {
        this.isSerious = isSerious;
        this.detail = detail;
        this.username = username;
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
