package com.perry.cnms.dto;

import com.perry.cnms.entity.Teacher;
import com.perry.cnms.entity.TeacherData;
import com.perry.cnms.enums.StateEnum;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/22
 */
public class TeacherExecution {
    private int state;
    private String stateInfo;
    private int count;
    private Teacher teacher;
    private List<Teacher> teacherList;

    public List<TeacherData> getTeacherDataList() {
        return teacherDataList;
    }

    public void setTeacherDataList(List<TeacherData> teacherDataList) {
        this.teacherDataList = teacherDataList;
    }

    private List<TeacherData> teacherDataList;


    public TeacherExecution(StateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public TeacherExecution(StateEnum stateEnum, Teacher teacher) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.teacher = teacher;
    }

    public TeacherExecution(StateEnum stateEnum, List<Teacher> teacherList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.teacherList = teacherList;
    }

    public TeacherExecution() {
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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }
}
