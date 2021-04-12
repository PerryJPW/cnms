package com.perry.cnms.dao;

import com.perry.cnms.entity.Teacher;
import com.perry.cnms.entity.TeacherData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/26
 */
public interface TeacherDao {

    List<Teacher> queryTeacher(@Param("teacherAccount") String teacherAccount,
                               @Param("teacherState") String teacherState,
                               @Param("teacherId") Integer teacherId);

    Integer deleteTeacher(Integer teacherId);

    int batchInsertTeacher(List<Teacher> teacherList);

    int updateTeacher(Teacher teacher);

    int insertTeacherData(TeacherData teacherData);

    List<TeacherData> queryTeacherData(@Param("teacherId") Integer teacherId,
                                       @Param("teacherDataId")Integer teacherDataId);

    int deleteTeacherData(@Param("teacherDataId")Integer teacherDataId);
}
