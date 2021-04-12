package com.perry.cnms.entity;

import java.util.Date;

/**
 * @Author: PerryJ
 * @Date: 2020/1/13
 */
public class TeacherData {
    private Integer teacherDataId;
    private Integer teacherId;
    private String teacherDataName;
    private String teacherDataAddress;
    private Integer teacherDataState;
    private Date updateTime;

    public String getTeacherDataName() {
        return teacherDataName;
    }

    public void setTeacherDataName(String teacherDataName) {
        this.teacherDataName = teacherDataName;
    }

    public Integer getTeacherDataId() {
        return teacherDataId;
    }

    public void setTeacherDataId(Integer teacherDataId) {
        this.teacherDataId = teacherDataId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherDataAddress() {
        return teacherDataAddress;
    }

    public void setTeacherDataAddress(String teacherDataAddress) {
        this.teacherDataAddress = teacherDataAddress;
    }

    public Integer getTeacherDataState() {
        return teacherDataState;
    }

    public void setTeacherDataState(Integer teacherDataState) {
        this.teacherDataState = teacherDataState;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
