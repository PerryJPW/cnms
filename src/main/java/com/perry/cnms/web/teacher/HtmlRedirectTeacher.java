package com.perry.cnms.web.teacher;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 网页访问转发
 * 前缀和后缀（.html）在配置文件中已经声明，不需要写
 *
 * @Author: PerryJ
 * @Date: 2020/1/17
 */
@Controller
@RequestMapping(value = "/teacher", method = RequestMethod.GET)
public class HtmlRedirectTeacher {
    @RequestMapping(value = "/student-manage")
    public String getStudentManage() {
        return "teacher/StudentManage";
    }

    @RequestMapping(value = "/area-manage")
    public String getAreaManage() {
        return "teacher/AreaManage";
    }

    @RequestMapping(value = "/area-info")
    public String getAreaInfo() {
        return "teacher/AreaInfoTeacher";
    }

    @RequestMapping(value = "/file-manage")
    public String pointAdmin() {
        return "teacher/TeacherDataManage";
    }

    @RequestMapping(value = "/login")
    public String loginTeacher0(){
        return "teacher/TeacherLogin";
    }

    @RequestMapping(value = "")
    public String loginTeacher1(){
        return "teacher/TeacherLogin";
    }

    @RequestMapping(value = "/student-data")
    public String studentData() {
        return "teacher/StudentData";
    }


}
