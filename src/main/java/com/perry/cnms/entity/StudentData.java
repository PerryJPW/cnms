package com.perry.cnms.entity;

import java.util.Date;

/**
 * @Author: PerryJ
 * @Date: 2020/1/13
 */
public class StudentData {
    private Integer studentDataId;
    private Integer groupId;
    private Integer pointId;
    private Double pointX;
    private Double tolerateX;
    private Double pointY;
    private Double tolerateY;
    private Double pointH;
    private Double tolerateH;
    private Integer studentDataState;
    private Date updateTime;

    public Double getTolerateX() {
        return tolerateX;
    }

    public void setTolerateX(Double tolerateX) {
        this.tolerateX = tolerateX;
    }

    public Double getTolerateY() {
        return tolerateY;
    }

    public void setTolerateY(Double tolerateY) {
        this.tolerateY = tolerateY;
    }

    public Double getTolerateH() {
        return tolerateH;
    }

    public void setTolerateH(Double tolerateH) {
        this.tolerateH = tolerateH;
    }

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    public Integer getStudentDataId() {
        return studentDataId;
    }

    public void setStudentDataId(Integer studentDataId) {
        this.studentDataId = studentDataId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

    public Integer getStudentDataState() {
        return studentDataState;
    }

    public void setStudentDataState(Integer studentDataState) {
        this.studentDataState = studentDataState;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
