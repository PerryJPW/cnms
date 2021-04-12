package com.perry.cnms.entity;

import java.util.Date;

/**
 * @Author: PerryJ
 * @Date: 2020/1/13
 */
public class Point {
    private Integer pointId;
    private Integer areaId;
    private String pointName;
    private String nextPoint;
    private Double pointX;
    private Double pointY;
    private Double pointH;
    private Double distance;
    private Integer pointState;
    private Date updateTime;

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getNextPoint() {
        return nextPoint;
    }

    public void setNextPoint(String nextPoint) {
        this.nextPoint = nextPoint;
    }

    public Double getPointX() {
        return pointX;
    }

    public void setPointX(Double pointX) {
        this.pointX = pointX;
    }

    public Double getPointY() {
        return pointY;
    }

    public void setPointY(Double pointY) {
        this.pointY = pointY;
    }

    public Double getPointH() {
        return pointH;
    }

    public void setPointH(Double pointH) {
        this.pointH = pointH;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getPointState() {
        return pointState;
    }

    public void setPointState(Integer pointState) {
        this.pointState = pointState;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Point{" +
                "pointId=" + pointId +
                ", areaId=" + areaId +
                ", pointName='" + pointName + '\'' +
                ", nextPoint='" + nextPoint + '\'' +
                ", pointX=" + pointX +
                ", pointY=" + pointY +
                ", pointH=" + pointH +
                ", distance=" + distance +
                ", pointState=" + pointState +
                ", updateTime=" + updateTime +
                '}';
    }
}
