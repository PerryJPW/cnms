package com.perry.cnms.entity;

import java.util.Date;

/**
 * @Author: PerryJ
 * @Date: 2020/1/13
 */
public class Area {
    private Integer areaId;
    private String areaName;
    private String areaPictureAddress;
    private Integer areaState;
//    private Double alpha;
    private Date updateTime;

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaPictureAddress() {
        return areaPictureAddress;
    }

    public void setAreaPictureAddress(String areaPictureAddress) {
        this.areaPictureAddress = areaPictureAddress;
    }

    public Integer getAreaState() {
        return areaState;
    }

    public void setAreaState(Integer areaState) {
        this.areaState = areaState;
    }

//    public Double getAlpha() {
//        return alpha;
//    }
//
//    public void setAlpha(Double alpha) {
//        this.alpha = alpha;
//    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Area{" +
                "areaId=" + areaId +
                ", areaName='" + areaName + '\'' +
                ", areaPictureAddress='" + areaPictureAddress + '\'' +
                ", areaState=" + areaState +
//                ", alpha=" + alpha +
                ", updateTime=" + updateTime +
                '}';
    }
}
