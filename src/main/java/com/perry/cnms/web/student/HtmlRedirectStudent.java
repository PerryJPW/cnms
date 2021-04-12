package com.perry.cnms.web.student;

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
@RequestMapping(value = "/student", method = RequestMethod.GET)
public class HtmlRedirectStudent {
    @RequestMapping(value = "/area-info")
    public String getStudentManage() {
        return "student/AreaInfoStudent";
    }

    @RequestMapping(value = "/period-1")
    public String getAreaManage() {
        return "student/StudentPeriodOne";
    }

    @RequestMapping(value = "/period-2")
    public String getAreaInfo() {
        return "student/StudentPeriodTwo";
    }

    @RequestMapping(value = "/login")
    public String loginAdmin(){
        return "student/StudentLogin";
    }

    @RequestMapping(value = "")
    public String loginAdmin1(){
        return "student/StudentLogin";
    }


}
