package com.zjy.trafficassist.model;

/**
 * com.zjy.trafficassist.model
 * Created by 73958 on 2017/6/2.
 */

public enum IssueType {

    ROAD_JAM("road_jam"),
    ROAD_CLOSE("road_close"),
    ROAD_WATER("road_water"),
    ROAD_CONSTRUCTION("road_construction");

    private String type;

    IssueType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
