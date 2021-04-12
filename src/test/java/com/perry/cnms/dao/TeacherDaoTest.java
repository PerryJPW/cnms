package com.perry.cnms.dao;

import com.perry.cnms.BaseTest;
import com.perry.cnms.entity.Teacher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/26
 */
public class TeacherDaoTest extends BaseTest {
    @Autowired
    private TeacherDao teacherDao;

    @Test
    public void testQueryTeacher() {
        List<Teacher> teacherList = teacherDao.queryTeacher(null,"%",null);
        for (Teacher teacher :
                teacherList) {
            System.out.println(teacher);
        }

    }
}
