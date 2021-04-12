package com.perry.cnms.web.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perry.cnms.dto.TeacherExecution;
import com.perry.cnms.entity.Teacher;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.exceptions.TeacherOperateException;
import com.perry.cnms.service.TeacherService;
import com.perry.cnms.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Author: PerryJ
 * @Date: 2020/1/26
 */

@Controller
@RequestMapping(value = "/admin")
public class TeacherAdminController {

    private Logger log = LoggerFactory.getLogger(TeacherAdminController.class);
    @Autowired
    private TeacherService teacherService;

    @RequestMapping(value = "/get-teachers", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getTeachers(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        TeacherExecution teacherExecution = teacherService.getTeacherList();
        if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
            modelMap.put("teacherList", teacherExecution.getTeacherList());
            log.info("[WEB]获取教师+"+teacherExecution.getTeacherList().size());
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "查询教师数据失败");
        }

        return modelMap;
    }

    @RequestMapping(value = "/delete-teacher", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> deleteTeacher(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        //获取id，插入数据库
        Integer teacherId = HttpServletRequestUtil.getInt(request, "teacherId");
        try {
            TeacherExecution teacherExecution = teacherService.deleteTeacher(teacherId);
            if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                log.info("[WEB]删除教师-id"+teacherId);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "删除失败");
            }
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "删除失败");
            log.error(e.getMessage());
            e.printStackTrace();
            return modelMap;
        }
        return modelMap;
    }

    @RequestMapping(value = "/add-teacher", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addTeacher(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        //接收并转换相应的参数为对应的实体类
        String teacherJSON = HttpServletRequestUtil.getString(request, "teacherString");
        ObjectMapper mapper = new ObjectMapper();
        Teacher teacher = null;
        try {
            teacher = mapper.readValue(teacherJSON, Teacher.class);
        } catch (IOException e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
            return modelMap;
        }

        if (teacher != null) {
            //teacher 处理，名字是否为空，account和密码处理
            if (teacher.getTeacherName() == null || "".equals(teacher.getTeacherName())) {
                modelMap.put("success", false);
                modelMap.put("errMsg", "姓名为空");
                return modelMap;
            }

            String teacherAccount = PinYinUtil.getPinYin(teacher.getTeacherName());
            if (teacher.getTeacherAccount() == null || "".equals(teacher.getTeacherAccount())) {
                teacher.setTeacherAccount(teacherAccount);
            }
            if (teacher.getPassword() == null || "".equals(teacher.getPassword())) {
                teacher.setPassword(teacher.getTeacherAccount());
            }
            teacher.setUpdateTime(new Date());

            //插入数据库
            List<Teacher> teacherList = new ArrayList<>();
            teacherList.add(teacher);
            try {
                TeacherExecution teacherExecution = teacherService.addTeacherList(teacherList);
                if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                    log.info("[WEB]教师添加");
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", teacherExecution.getStateInfo());
                }
            } catch (TeacherOperateException toe) {
                toe.printStackTrace();
                log.error(toe.getMessage());
                modelMap.put("success", false);
                modelMap.put("errMsg", "教师添加失败");
                return modelMap;
            }

        }
        return modelMap;

    }

    @RequestMapping(value = "/add-teacher-excel")
    @ResponseBody
    private Map<String, Object> addTeacherByExcel(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        List<Teacher> teacherList = new ArrayList<>();
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }

        MultipartHttpServletRequest multipartHttpServletRequest = null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request
                .getSession().getServletContext());
        try {
            if (commonsMultipartResolver.isMultipart(request)) {
                multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                CommonsMultipartFile tmpTeacherExcel = (CommonsMultipartFile) multipartHttpServletRequest
                        .getFile("teacherExcel");
                if (tmpTeacherExcel != null) {
                    InputStream inputStream = tmpTeacherExcel.getInputStream();
                    String fileExtension = PathUtil.getFileExtension(tmpTeacherExcel);
                    teacherList = ExcelUtil.getTeacherNameFromExcel(inputStream, fileExtension);
                }
            }
        } catch (IOException ioe) {
            log.error("教师表的InputStream获取失败"+ioe.getMessage());
            ioe.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", "Excel表读取失败");
            return modelMap;
        }
        if (teacherList == null) {
            modelMap.put("success", false);
        } else {
            TeacherExecution teacherExecution = null;
            try {
                teacherExecution = teacherService.addTeacherList(teacherList);
                if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                    log.info("[WEB]教师添加+"+teacherList.size());
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "教师添加失败");
                }
            } catch (Exception e) {
                log.error("教师添加出错"+e.getMessage());
                e.printStackTrace();
                modelMap.put("success", false);
                modelMap.put("errMsg", "插入数据库失败");
                return modelMap;
            }
        }
        return modelMap;
    }

    @RequestMapping(value = "/update-teacher", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> updateTeacher(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        Integer teacherId = HttpServletRequestUtil.getInt(request, "teacherId");
        if (teacherId == null || teacherId < 0) {
            modelMap.put("errMsg", "教师信息出错");
            return modelMap;
        }
        Integer teacherState = HttpServletRequestUtil.getInt(request, "teacherState");
        String password = HttpServletRequestUtil.getString(request, "password");
        String teacherAccount = HttpServletRequestUtil.getString(request, "teacherAccount");
        Teacher teacher = new Teacher();
        teacher.setTeacherId(teacherId);
        teacher.setTeacherAccount(teacherAccount);
        teacher.setPassword(password);
        teacher.setTeacherState(teacherState);
        teacher.setUpdateTime(new Date());

        try {
            TeacherExecution teacherExecution = teacherService.updateTeacher(teacher);
            if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                log.info("[WEB]教师更新-id"+ teacherId);
                return modelMap;
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", teacherExecution.getStateInfo());
                return modelMap;
            }
        } catch (TeacherOperateException e) {
            log.error(e.getMessage());
            modelMap.put("errMsg", "更新失败");
            e.printStackTrace();
            return modelMap;
        }

    }

}
