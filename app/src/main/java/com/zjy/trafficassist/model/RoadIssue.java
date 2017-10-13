package com.zjy.trafficassist.model;

import com.amap.api.maps.model.LatLng;

import java.util.Objects;

/**
 * com.zjy.trafficassist.model
 * Created by 73958 on 2017/6/2.
 */

public class RoadIssue {

    private String imageUrl;
    private String issueType;
    private IssueType type;
    private String direction;
    private String detail_tag;
    private String detail;
    private String address;
    private double longitude;
    private double latitude;

    public RoadIssue(String imageUrl, String issueType, String direction,
                     String detail_tag, String detail, String address,
                     double longitude, double latitude) {
        this.imageUrl = imageUrl;
        this.issueType = issueType;
        this.direction = direction;
        this.detail_tag = detail_tag;
        this.detail = detail;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        if (Objects.equals(issueType, "road_jam")) {
            type = IssueType.ROAD_JAM;
        } else if (Objects.equals(issueType, "road_close")) {
            type = IssueType.ROAD_CLOSE;
        } else if (Objects.equals(issueType, "road_water")) {
            type = IssueType.ROAD_WATER;
        } else if (Objects.equals(issueType, "road_construction")) {
            type = IssueType.ROAD_CONSTRUCTION;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDetail_tag() {
        return detail_tag;
    }

    public void setDetail_tag(String detail_tag) {
        this.detail_tag = detail_tag;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public IssueType getType() {
        return type;
    }
}
