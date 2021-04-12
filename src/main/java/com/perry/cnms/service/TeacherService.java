package com.perry.cnms.service;

import com.perry.cnms.dto.TeacherExecution;
import com.perry.cnms.entity.Teacher;
import com.perry.cnms.entity.TeacherData;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/26
 */
public interface TeacherService {
    /**
     * @return 教师列表
     */
    TeacherExecution getTeacherList();

    /**
     * @param teacherAccount 查询的教师账户名
     * @return 单个教师
     */
    TeacherExecution getTeacherByTeacherAccount(String teacherAccount);

    /**
     *
     * @param teacherId 查询的教师ID
     * @return 单个教师
     */
    TeacherExecution getTeacherByTeacherId(Integer teacherId);

    /**
     * @param teacherId 要删除的教师ID
     * @return 状态信息
     */
    TeacherExecution deleteTeacher(Integer teacherId);

    /**
     * @param teacherList 要添加的教师列表
     * @return 状态信息
     */
    TeacherExecution addTeacherList(List<Teacher> teacherList);

    /**
     * @param teacher 要更新的单个教师实体
     * @return 状态信息
     */
    TeacherExecution updateTeacher(Teacher teacher);

    //TeacherData

    /**
     * @param teacherData 要添加的单个教师数据实体
     * @return 状态信息
     */
    TeacherExecution addTeacherData(TeacherData teacherData);

    /**
     * @param teacherId 查询的数据所属的教师id
     * @return 教师数据列表
     */
    TeacherExecution getTeacherDataListByTeacherId(Integer teacherId);

    /**
     * @param teacherDataId 查询的数据id
     * @return 教师数据实体
     */
    TeacherExecution getTeacherDataByTeacherDataId(Integer teacherDataId);

    /**
     * @param teacherDataId 要删除的数据id
     * @return 状态信息
     */
    TeacherExecution deleteTeacherData(Integer teacherDataId);
}
