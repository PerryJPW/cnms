package com.perry.cnms.entity;

import java.util.Date;

/**
 * @Author: PerryJ
 * @Date: 2020/1/13
 */
public class StudentPicture {
    private Integer pictureId;
    private Integer groupId;
    private String pictureAddress;
    private Integer pictureState;
    private Date updateTime;

    public Integer getPictureId() {
        return pictureId;
    }

    public void setPictureId(Integer pictureId) {
        this.pictureId = pictureId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    public Integer getPictureState() {
        return pictureState;
    }

    public void setPictureState(Integer pictureState) {
        this.pictureState = pictureState;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
