package com.perry.cnms.web.teacher;

import com.perry.cnms.dto.StudentExecution;
import com.perry.cnms.dto.TeacherExecution;
import com.perry.cnms.entity.Teacher;
import com.perry.cnms.entity.TeacherData;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.exceptions.StudentOperateException;
import com.perry.cnms.exceptions.TeacherOperateException;
import com.perry.cnms.service.StudentService;
import com.perry.cnms.service.TeacherService;
import com.perry.cnms.util.HttpServletRequestUtil;
import com.perry.cnms.util.PathUtil;
import com.perry.cnms.util.VerifyCodeUtil;
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
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: PerryJ
 * @Date: 2020/2/2
 */
@Controller
@RequestMapping(value = "/teacher")
public class TeacherDataManageController {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;

    private Logger log = LoggerFactory.getLogger(TeacherDataManageController.class);

    @RequestMapping(value = "/get-data", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getTeacherDataList(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");

        if (teacherId == null || teacherId < 0) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "教师信息出错");
        } else {
            TeacherExecution teacherExecution = teacherService.getTeacherDataListByTeacherId(teacherId);
            if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                modelMap.put("dataList", teacherExecution.getTeacherDataList());
                log.info("[WEB-T]获取教师资料");

            } else if (teacherExecution.getState() == StateEnum.EMPTY_RETURN.getState()) {
                modelMap.put("success", true);
                modelMap.put("dataList", null);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "教师资料获取失败");
            }
        }
        return modelMap;
    }


    @RequestMapping(value = "/add-data", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addTeacherData(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        if (teacherId == null || teacherId < 0) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "教师信息出错");
            return modelMap;
        }

        //1.接收信息并处理
        CommonsMultipartFile tmpDataComFile = null;
        MultipartHttpServletRequest multipartHttpServletRequest = null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request
                .getSession().getServletContext());
        try {
            //处理文件流
            if (commonsMultipartResolver.isMultipart(request)) {
                multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                tmpDataComFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("teacherDataFile");
                if (tmpDataComFile == null) {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "上传文件不能为空");
                    return modelMap;
                }

            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "上传文件不能为空");
                return modelMap;
            }
        } catch (Exception e) {
            log.error(e.getMessage() + "文件处理失败");
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }

        //2.添加到数据库
        String teacherDataPath = PathUtil.getTeacherDataPath();
        PathUtil.makeDirPath(teacherDataPath);
        String teacherDataAddress = teacherDataPath + PathUtil.separator
                + PathUtil.getRandomFileName() + PathUtil.getFileExtension(tmpDataComFile);
        try {
            //文件保存,实体类创建
            File tmpFile = new File(teacherDataAddress);

            //保存新的文件
            tmpDataComFile.transferTo(tmpFile);
            log.info("[WEB-T]保存资料");
            TeacherData teacherData = new TeacherData();
            teacherData.setTeacherId(teacherId);
            teacherData.setTeacherDataName(PathUtil.getRealFileName(tmpDataComFile) + PathUtil.getFileExtension(tmpDataComFile));
            teacherData.setTeacherDataAddress(teacherDataAddress);
            Integer teacherDataState = HttpServletRequestUtil.getInt(request, "teacherDataState");
            teacherData.setTeacherDataState(teacherDataState);
            teacherData.setUpdateTime(new Date());

            TeacherExecution teacherExecution = teacherService.addTeacherData(teacherData);
            if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                log.info("[WEB-T]添加资料-tID" + teacherId);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", teacherExecution.getStateInfo());
            }


        } catch (IOException e) {
            log.error("文件保存失败" + e.getMessage());
            e.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", "文件存储失败");
            return modelMap;
        } catch (TeacherOperateException te) {
            log.error("数据库插入出错" + te.getMessage());
            te.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", "数据库插入出错");
            return modelMap;
        }

        return modelMap;
    }

    @RequestMapping(value = "/delete-data", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> downloadFile(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }

        Integer teacherDataId = HttpServletRequestUtil.getInt(request, "teacherDataId");
        try {

            TeacherExecution teacherExecution = teacherService.deleteTeacherData(teacherDataId);
            if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                log.info("[WEB-T]删除资料dataID" + teacherDataId);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "删除失败");
            }
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "删除失败");
            e.printStackTrace();
            return modelMap;
        }
        return modelMap;
    }

    @RequestMapping(value = "/check-password", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> checkPassword(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        String pass = HttpServletRequestUtil.getString(request, "pass");
        TeacherExecution teacherExecution = teacherService.getTeacherByTeacherId(teacherId);
        if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
            if (teacherExecution.getTeacher().getPassword().equals(pass)) {
                modelMap.put("success", true);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "密码错误");
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", teacherExecution.getStateInfo());
        }

        return modelMap;
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> changePassword(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        String pass = HttpServletRequestUtil.getString(request, "pass");
        Teacher teacher = new Teacher();
        teacher.setTeacherId(teacherId);
        teacher.setPassword(pass);
        try {
            TeacherExecution teacherExecution = teacherService.updateTeacher(teacher);
            if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                log.info("[WEB-T]密码修改ID" + teacherId);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", teacherExecution.getStateInfo());
            }
        } catch (TeacherOperateException e) {
            log.error(e.getMessage());
            throw new TeacherOperateException("更新失败");
        }
        return modelMap;
    }

    @RequestMapping(value = "/get-use-teacher-class", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getTeacherNameById(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer loginTeacherId = (Integer) request.getSession().getAttribute("teacherId");
        Integer teacherId = HttpServletRequestUtil.getInt(request, "teacherId");
        Integer groupId = HttpServletRequestUtil.getInt(request, "groupId");
        if (loginTeacherId.equals(teacherId)) {
            modelMap.put("ownerUseTeacher", true);
        }
        try {
            TeacherExecution teacherExecution = teacherService.getTeacherByTeacherId(teacherId);
            StudentExecution studentExecution = studentService.getGroupByGroupID(groupId);
            if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {

                modelMap.put("success", true);
                modelMap.put("teacherName", teacherExecution.getTeacher().getTeacherName());
                modelMap.put("className", studentExecution.getGroup().getMajorCode());

//                log.info("[WEB-T]密码修改ID" + teacherId);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", teacherExecution.getStateInfo());
            }
        } catch (TeacherOperateException e) {
            log.error(e.getMessage());
            throw new TeacherOperateException("获取教师失败");
        } catch (StudentOperateException se) {
            log.error(se.getMessage());
            throw new StudentOperateException("获取班级失败");
        }
        return modelMap;
    }

}
