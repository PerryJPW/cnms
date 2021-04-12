package com.perry.cnms.web.teacher;

import com.perry.cnms.dto.PointExecution;
import com.perry.cnms.dto.StudentExecution;
import com.perry.cnms.entity.Group;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.enums.StudentStateEnum;
import com.perry.cnms.exceptions.StudentOperateException;
import com.perry.cnms.service.AreaUseService;
import com.perry.cnms.service.PointService;
import com.perry.cnms.service.SettingsService;
import com.perry.cnms.service.StudentService;
import com.perry.cnms.util.HttpServletRequestUtil;
import com.perry.cnms.util.PinYinUtil;
import com.perry.cnms.util.VerifyCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * @Author: PerryJ
 * @Date: 2020/1/29
 */
@Controller
@RequestMapping(value = "/teacher")
public class StudentManageController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private AreaUseService areaUseService;
    @Autowired
    private PointService pointService;
    @Autowired
    private SettingsService settingsService;
    private Logger log = LoggerFactory.getLogger(StudentManageController.class);


    @RequestMapping(value = "/add-group", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addGroup(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }

        //接收并转换相应的参数为对应的实体类
        List<Group> groupList = new ArrayList<>();
        String year = HttpServletRequestUtil.getString(request, "year");
        year = year.substring(2);
        String classNumber = HttpServletRequestUtil.getString(request, "classNumber");
        int groupCount = HttpServletRequestUtil.getInt(request, "groupCount");
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        String password = HttpServletRequestUtil.getString(request, "password");
        Integer groupState = HttpServletRequestUtil.getInt(request, "groupState");
        String majorName = HttpServletRequestUtil.getString(request, "majorName");
        String majorCode = majorName + year + "_" + classNumber;
        assert majorName != null;
        String tmpGroupAccount = PinYinUtil.getPinYinHeadChar(majorName) + year;

        for (int groupNum = 1; groupNum <= groupCount; groupNum++) {
            Group group = new Group();
            group.setTeacherId(teacherId);
            group.setMajorCode(majorCode);
            String groupAccount = tmpGroupAccount + "0" + classNumber;
            if (groupNum < 10) {
                groupAccount = groupAccount + "0" + groupNum;
            } else {
                groupAccount = groupAccount + groupNum;
            }
            group.setGroupAccount(groupAccount);
            String password_in = "";
            if (password == null || "".equals(password)) {

                password_in = groupAccount + "@" + groupNum + (char) (96 + groupNum);
            } else {
                password_in = password;
            }
            group.setPassword(password_in);
            group.setGroupCode(String.valueOf(groupNum));
            group.setGroupState(groupState);
            group.setUpdateTime(new Date());
            groupList.add(group);
        }


        //插入数据库
        try {
            StudentExecution studentExecution = studentService.addGroupList(groupList);
            if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                log.info("[WEB-T]添加小组账号-tID" + teacherId);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", studentExecution.getStateInfo());
            }
        } catch (StudentOperateException e) {
            log.error("添加小组账号失败" + e.getMessage());
            modelMap.put("success", false);
            modelMap.put("errMsg", "请重新添加班级");
            e.printStackTrace();
            return modelMap;
        }

        return modelMap;
    }


    @RequestMapping(value = "/get-groups", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getGroups(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        StudentExecution studentExecution = studentService.getGroupListByTeacherId(teacherId);
        if (studentExecution.getState() == StateEnum.SUCCESS.getState() && studentExecution.getGroupList() != null) {
            modelMap.put("success", true);
            modelMap.put("groupList", studentExecution.getGroupList());
            log.info("[WEB-T]获取小组-tID" + teacherId);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "查询场地数据失败");
        }

        return modelMap;
    }

    @RequestMapping(value = "/update-group", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> updateGroup(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
//        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        Integer groupId = HttpServletRequestUtil.getInt(request, "groupId");
        Integer groupState = HttpServletRequestUtil.getInt(request, "groupState");
        String groupPassword = HttpServletRequestUtil.getString(request, "groupPassword");
        if (groupPassword == null || "".equals(groupPassword)) {
            groupPassword = null;
        }
        Group group = new Group();
        group.setGroupId(groupId);
        group.setPassword(groupPassword);
        group.setGroupState(groupState);
        group.setUpdateTime(new Date());
        try {
            StudentExecution studentExecution = studentService.updateGroup(group);
            if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                log.info("[WEB-T]更新小组账号-sID" + groupId);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", studentExecution.getStateInfo());
            }
        } catch (StudentOperateException e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "更新失败");
            e.printStackTrace();
            return modelMap;
        }
        return modelMap;
    }


    @RequestMapping(value = "/add-class-group", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addGroupByClass(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        String majorCode = HttpServletRequestUtil.getString(request, "majorCode");
        Integer groupCode = HttpServletRequestUtil.getInt(request, "groupCode");
        if (groupCode == null || majorCode == null || "".equals(majorCode)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "班级出错");
            return modelMap;
        }
        Integer gId= studentService.getGroupIdsByMajorCode(majorCode).getIdList().get(0);
        Integer gState= studentService.getGroupByGroupID(gId).getGroup().getGroupState();
        if (gState!=StudentStateEnum.INITIAL.getState()){
            modelMap.put("success",false);
            modelMap.put("errMsg","非初始状态，不允许添加");
            return modelMap;
        }
        groupCode=groupCode+1;


        Group group = new Group();
        group.setTeacherId(teacherId);
        group.setMajorCode(majorCode);
        int index= majorCode.indexOf('_');
        majorCode=majorCode.substring(0,index)+"0"+majorCode.substring(index+1);
        String groupAccount = PinYinUtil.getPinYinHeadChar(majorCode) + "0" + (groupCode);
        group.setGroupAccount(groupAccount);
        group.setPassword(groupAccount + "@" + groupCode + (char) (96 + groupCode));
        group.setGroupCode(String.valueOf(groupCode));
        group.setGroupState(StudentStateEnum.INITIAL.getState());
        group.setUpdateTime(new Date());

        try {
            List<Group> groupList = new ArrayList<>();
            groupList.add(group);
            StudentExecution studentExecution = studentService.addGroupList(groupList);
            if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                log.info("[WEB-T]添加一个学生账号");
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", studentExecution.getStateInfo());
            }
        } catch (StudentOperateException e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "添加失败");
            e.printStackTrace();
            return modelMap;
        }
        return modelMap;
    }
    @RequestMapping(value = "/delete-class-group", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> deleteGroupByClass(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        String majorCode = HttpServletRequestUtil.getString(request, "majorCode");
        if (majorCode == null || "".equals(majorCode)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "班级出错");
            return modelMap;
        }
        Integer gId= studentService.getGroupIdsByMajorCode(majorCode).getIdList().get(0);
        Integer gState= studentService.getGroupByGroupID(gId).getGroup().getGroupState();
        if (gState!=StudentStateEnum.INITIAL.getState()){
            modelMap.put("success",false);
            modelMap.put("errMsg","非初始状态，不允许删除");
            return modelMap;
        }

        try {

            StudentExecution studentExecution = studentService.deleteGroupByMajorCode(majorCode);
            if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                log.info("[WEB-T]删除班级");
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", studentExecution.getStateInfo());
            }
        } catch (StudentOperateException e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "删除失败");
            e.printStackTrace();
            return modelMap;
        }
        return modelMap;
    }

    @RequestMapping(value = "/delete-group", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> deleteGroup(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
//        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        Integer groupId = HttpServletRequestUtil.getInt(request, "groupId");
        Group group = studentService.getGroupByGroupID(groupId).getGroup();
        if (group.getGroupState() == StudentStateEnum.INITIAL.getState()) {

            try {
                StudentExecution studentExecution = studentService.deleteGroupByGroupId(groupId);
                if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                    log.info("[WEB-T]删除小组账号-sID" + groupId);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", studentExecution.getStateInfo());
                }
            } catch (StudentOperateException e) {
                log.error("删除出错");
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "学生不是初始状态，无法删除！");
            return modelMap;
        }

        return modelMap;
    }

    @RequestMapping(value = "/get-student-data", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getStudentData(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer groupId = HttpServletRequestUtil.getInt(request, "groupId");
        StudentExecution studentExecution2 = studentService.getGroupByGroupID(groupId);
        if (studentExecution2.getState() == StateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
            modelMap.put("group", studentExecution2.getGroup());
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", studentExecution2.getStateInfo());
            return modelMap;
        }
        Double[] accept = settingsService.getAccept();
        modelMap.put("acceptXY", accept[0]);
        modelMap.put("acceptH", accept[1]);

        StudentExecution studentExecution1 = studentService.getStudentDataList(groupId);
        if (studentExecution1.getState() == StateEnum.SUCCESS.getState() && studentExecution1.getStudentDataList().size() > 0) {
            modelMap.put("success", true);
            modelMap.put("dataList", studentExecution1.getStudentDataList());
            log.info("[WEB-T]获取学生数据-sID" + groupId);
        } else {
//            modelMap.put("success", false);
            modelMap.put("errMsg", studentExecution1.getStateInfo());
            return modelMap;
        }
        return modelMap;
    }

    @RequestMapping(value = "/get-point-name", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getPointName(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer pointId = HttpServletRequestUtil.getInt(request, "pointId");

        PointExecution pointExecution = pointService.getPointByPointId(pointId);
        if (pointExecution.getState() == StateEnum.SUCCESS.getState()) {
            String pointName = pointExecution.getPoint().getPointName();
            modelMap.put("success", true);
            modelMap.put("pointName", pointName);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", pointExecution.getStateInfo());
        }
        return modelMap;
    }

    @RequestMapping(value = "/get-student-picture", method = RequestMethod.GET)
//    @ResponseBody
    private Map<String, Object> getPeriodOneData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer groupId = HttpServletRequestUtil.getInt(request, "groupId");
        if (groupId == null || groupId < 0) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "小组信息错误");
        } else {
            StudentExecution studentExecution = studentService.getStudentPictureByGroupId(groupId);
            if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                String imgPath = studentExecution.getStudentPictureList().get(0).getPictureAddress();
                File imageFile = new File(imgPath);
                if (imageFile.exists()) {
                    FileInputStream fis = null;
                    OutputStream os = null;
                    try {
                        //获取输入流并写入到response输出流
                        fis = new FileInputStream(imageFile);
                        os = response.getOutputStream();
                        int count = 0;
                        byte[] buffer = new byte[1024 * 8];
                        while ((count = fis.read(buffer)) != -1) {
                            os.write(buffer, 0, count);
                            os.flush();
                        }
//                        log.info("图片文件写入到输出流");
                    } catch (Exception e) {
                        log.error("图片读取失败");
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fis != null) {
                                fis.close();
                            }
                            if (os != null) {
                                os.close();
                            }
                        } catch (IOException e) {
                            log.error("输入输出流关闭出错");
                            e.printStackTrace();
                        }
                    }

                }
                log.info("[WEB-T]获取学生阶段一图片+sID" + groupId);
            } else if (studentExecution.getState() == StateEnum.EMPTY_RETURN.getState()) {
                modelMap.put("success", true);
                modelMap.put("studentPicture", null);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "学生阶段一成果获取出错");
            }
        }
        return modelMap;

    }


}
