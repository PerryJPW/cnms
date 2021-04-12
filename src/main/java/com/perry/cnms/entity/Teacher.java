package com.perry.cnms.entity;

import java.util.Date;

/**
 * @Author: PerryJ
 * @Date: 2020/1/13
 */
public class Teacher {
    private Integer teacherId;
    private String teacherAccount;//教师账户名，暂定为姓名全拼
    private String password;//密码，需加密
    private String teacherName;
    private Integer teacherState;
    private Date updateTime;

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherAccount() {
        return teacherAccount;
    }

    public void setTeacherAccount(String teacherAccount) {
        this.teacherAccount = teacherAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Integer getTeacherState() {
        return teacherState;
    }

    public void setTeacherState(Integer teacherState) {
        this.teacherState = teacherState;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", teacherAccount='" + teacherAccount + '\'' +
                ", password='" + password + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", teacherState=" + teacherState +
                ", updateTime=" + updateTime +
                '}';
    }
}
