package com.perry.cnms.entity;

import java.util.Date;

/**
 * @Author: PerryJ
 * @Date: 2020/1/13
 */
public class AreaUsing {
    private Integer areaUseId;
    private Integer areaId;
    private Integer teacherId;
    private Integer groupId;
    private Date startTime;
    private Date endTime;
    private Integer areaUseState;
    private Date updateTime;

    public Integer getAreaUseId() {
        return areaUseId;
    }

    public void setAreaUseId(Integer areaUseId) {
        this.areaUseId = areaUseId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getAreaUseState() {
        return areaUseState;
    }

    public void setAreaUseState(Integer areaUseState) {
        this.areaUseState = areaUseState;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AreaUsing{" +
                "areaUseId=" + areaUseId +
                ", areaId=" + areaId +
                ", teacherId=" + teacherId +
                ", groupId=" + groupId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", areaUseState=" + areaUseState +
                ", updateTime=" + updateTime +
                '}';
    }
}
