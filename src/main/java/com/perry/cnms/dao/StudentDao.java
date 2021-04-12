package com.perry.cnms.dao;

import com.perry.cnms.entity.Group;
import com.perry.cnms.entity.StudentData;
import com.perry.cnms.entity.StudentPicture;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/29
 */
public interface StudentDao {

    int batchInsertGroup(List<Group> groupList);

    List<Group> queryGroup(@Param("notGroupState") String notGroupState,
                           @Param("teacherId") Integer teacherId,
                           @Param("groupAccount") String groupAccount,
                           @Param("groupId") Integer groupId);

//    List<Group> queryGroupByTeacherId(Integer teacherId);

    List<Integer> queryGroupIdByMajorCode(String majorCode);

    List<String> queryNoneAreaMajorCodeByTeacherId(Integer teacherId);

    List<Group> queryNoneHistoryMajorCodeByTeacherId(Integer teacherId);

    int updateGroup(Group group);

    int deleteGroup(@Param("groupId") Integer groupId,
                    @Param("majorCode") String majorCode);

    List<StudentPicture> queryStudentPicture(@Param("groupId") Integer groupId);

    int insertStudentPicture(StudentPicture studentPicture);

    int updateStudentPicture(StudentPicture studentPicture);

    int insertStudentData(List<StudentData> studentDataList);

    List<StudentData> queryStudentData(Integer groupId);

    int deleteStudentDataByGroupId(Integer groupId);
}
