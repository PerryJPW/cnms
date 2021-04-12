package com.perry.cnms.entity;

import java.util.Date;

/**
 * @Author: PerryJ
 * @Date: 2020/1/13
 */
public class Group {
    private Integer groupId;
    private Integer teacherId;
    private String groupAccount;    //学生账户名=（major+group 如：dz170301）
    private String password;
    private String majorCode;
    private String groupCode;
    private Integer groupState;
    private Date updateTime;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getGroupAccount() {
        return groupAccount;
    }

    public void setGroupAccount(String groupAccount) {
        this.groupAccount = groupAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Integer getGroupState() {
        return groupState;
    }

    public void setGroupState(Integer groupState) {
        this.groupState = groupState;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", teacherId=" + teacherId +
                ", groupAccount='" + groupAccount + '\'' +
                ", password='" + password + '\'' +
                ", majorCode='" + majorCode + '\'' +
                ", groupCode='" + groupCode + '\'' +
                ", groupState=" + groupState +
                ", updateTime=" + updateTime +
                '}';
    }
}
