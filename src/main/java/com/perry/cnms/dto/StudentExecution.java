package com.perry.cnms.dto;

import com.perry.cnms.entity.Group;
import com.perry.cnms.entity.StudentData;
import com.perry.cnms.entity.StudentPicture;
import com.perry.cnms.enums.StateEnum;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/22
 */
public class StudentExecution {
    private int state;
    private String stateInfo;
    private int count;
    private Group group;
    private List<Group> groupList;
    private List<Integer> idList;
    private List<String> majorCodeList;
    private List<StudentPicture> studentPictureList;
    private List<StudentData> studentDataList;

    public List<StudentData> getStudentDataList() {
        return studentDataList;
    }

    public void setStudentDataList(List<StudentData> studentDataList) {
        this.studentDataList = studentDataList;
    }

    public List<StudentPicture> getStudentPictureList() {
        return studentPictureList;
    }

    public void setStudentPictureList(List<StudentPicture> studentPictureList) {
        this.studentPictureList = studentPictureList;
    }

    public List<String> getMajorCodeList() {
        return majorCodeList;
    }

    public void setMajorCodeList(List<String> majorCodeList) {
        this.majorCodeList = majorCodeList;
    }

    public StudentExecution(StateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public StudentExecution(StateEnum stateEnum, Group group) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.group = group;
    }

    public StudentExecution(StateEnum stateEnum, List<Group> groupList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.groupList = groupList;
    }

    public StudentExecution() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }
}
