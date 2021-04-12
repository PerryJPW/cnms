package com.perry.cnms.web.admin;

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
@RequestMapping(value = "/admin", method = RequestMethod.GET)
public class HtmlRedirectAdmin {
    @RequestMapping(value = "/area-admin")
    public String addArea() {
        return "admin/AreaAdmin";
    }

    @RequestMapping(value = "/area-info")
    public String getAreaInfo() {
        return "admin/AreaInfo";
    }

    @RequestMapping(value = "/point-admin")
    public String pointAdmin() {
        return "admin/PointAdmin";
    }

    @RequestMapping(value = "/teacher-admin")
    public String teacherAdmin(){
        return "admin/TeacherAdmin";
    }

    @RequestMapping(value = "/settings-admin")
    public String settingsAdmin(){
        return "admin/SettingsAdmin";
    }

    @RequestMapping(value = "/login")
    public String loginAdmin(){
        return "admin/AdminLogin";
    }

    @RequestMapping(value = "")
    public String loginAdmin1(){
        return "admin/AdminLogin";
    }




}
