package com.perry.cnms.service;

import com.perry.cnms.dto.StudentExecution;
import com.perry.cnms.entity.Group;
import com.perry.cnms.entity.StudentData;
import com.perry.cnms.entity.StudentPicture;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/29
 */
public interface StudentService {

    //Group
    StudentExecution addGroupList(List<Group> groupList);

    StudentExecution getGroupList();

    StudentExecution getGroupListByTeacherId(Integer teacherId);

    StudentExecution getGroupByGroupAccount(String groupAccount);

    StudentExecution getGroupByGroupID(Integer groupId);

    StudentExecution getGroupIdsByMajorCode(String majorCode);

    StudentExecution getNoneAreaMajorCodeByTeacherId(Integer teacherId);

    StudentExecution getNoneHistoryMajorCodeByTeacherId(Integer teacherId);

    StudentExecution updateGroup(Group group);

    StudentExecution deleteGroupByGroupId(Integer groupId);
    StudentExecution deleteGroupByMajorCode(String majorCode);

    //StudentPicture
    StudentExecution getStudentPictures();

    StudentExecution getStudentPictureByGroupId(Integer groupId);

    StudentExecution addStudentPicture(StudentPicture studentPicture);

    StudentExecution updateStudentPicture(StudentPicture studentPicture);

    //StudentData
    StudentExecution addStudentData(List<StudentData> studentDataList);

    StudentExecution getStudentDataList(Integer groupId);

    StudentExecution deleteStudentDataByGroupId(Integer groupId);

}
