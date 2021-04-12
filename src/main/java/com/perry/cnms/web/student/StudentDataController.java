package com.perry.cnms.web.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perry.cnms.dto.PointExecution;
import com.perry.cnms.dto.StudentExecution;
import com.perry.cnms.entity.Group;
import com.perry.cnms.entity.Point;
import com.perry.cnms.entity.StudentData;
import com.perry.cnms.entity.StudentPicture;
import com.perry.cnms.enums.CommonStateEnum;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.enums.StudentStateEnum;
import com.perry.cnms.exceptions.StudentOperateException;
import com.perry.cnms.service.PointService;
import com.perry.cnms.service.SettingsService;
import com.perry.cnms.service.StudentService;
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
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * @Author: PerryJ
 * @Date: 2020/2/4
 */
@Controller
@RequestMapping(value = "/student")
public class StudentDataController {
    @Autowired
    StudentService studentService;
    @Autowired
    PointService pointService;
    @Autowired
    SettingsService settingsService;

    private double xyAccept = 0.010;
    private double hAccept = 0.010;
    private Logger log = LoggerFactory.getLogger(StudentDataController.class);

    @RequestMapping(value = "/get-student-info", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getStudentInfo(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer groupId = (Integer) request.getSession().getAttribute("groupId");
        StudentExecution studentExecution = studentService.getGroupByGroupID(groupId);
        if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
            modelMap.put("group", studentExecution.getGroup());
            log.info("[WEB-S]获取学生信息-sID" + groupId);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", studentExecution.getStateInfo());
        }

        return modelMap;
    }

    @RequestMapping(value = "/add-picture", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addPeriodOneData(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        Integer groupId = (Integer) request.getSession().getAttribute("groupId");
        if (groupId == null || groupId < 0) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "学生信息出错");
            return modelMap;
        }
        //状态检测，去除
//        Integer groupState = (Integer) request.getSession().getAttribute("groupState");
//        if (groupState == null || groupState < StudentStateEnum.PERIOD_ZERO.getState()
//                || groupState == StudentStateEnum.HISTORY.getState()) {
//            modelMap.put("success", false);
//            modelMap.put("errMsg", "学生状态异常");
//            return modelMap;
//        }

        //1.接收信息并处理
        CommonsMultipartFile tmpDataComFile = null;
        MultipartHttpServletRequest multipartHttpServletRequest = null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request
                .getSession().getServletContext());
        try {
            //处理文件流
            if (commonsMultipartResolver.isMultipart(request)) {
                multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                tmpDataComFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("studentPicture");
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
            log.error("文件处理失败" + e.getMessage());
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        log.info("[WEB-S]数据图片保存-sID" + groupId);


        //2.添加到数据库
        String studentPath = PathUtil.getStudentPicturePath();
        PathUtil.makeDirPath(studentPath);
        String studentDataAddress = studentPath + PathUtil.separator
                + PathUtil.getStudentFileName(groupId) + PathUtil.getFileExtension(tmpDataComFile);
        try {
            //文件保存,实体类创建
            File tmpFile = new File(studentDataAddress);

            //保存新的文件
            tmpDataComFile.transferTo(tmpFile);

            StudentPicture studentPicture = new StudentPicture();
            studentPicture.setGroupId(groupId);
            studentPicture.setPictureAddress(studentDataAddress);
            Integer pictureState = HttpServletRequestUtil.getInt(request, "pictureState");
            studentPicture.setPictureState(pictureState);
            studentPicture.setUpdateTime(new Date());

            StudentExecution studentExecution1 = studentService.getStudentPictureByGroupId(groupId);
            if (studentExecution1.getState() == StateEnum.SUCCESS.getState()) {
                Integer spId = studentExecution1.getStudentPictureList().get(0).getPictureId();
                studentPicture.setPictureId(spId);
                StudentExecution studentExecution2 = studentService.updateStudentPicture(studentPicture);
                if (studentExecution2.getState() == StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", studentExecution2.getStateInfo());
                }
            } else {

                StudentExecution studentExecution3 = studentService.addStudentPicture(studentPicture);

                if (studentExecution3.getState() == StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", studentExecution3.getStateInfo());
                }
            }
            Boolean flag = (Boolean) modelMap.get("success");
            if (flag) {
                Group group = new Group();
                group.setGroupId(groupId);
                group.setGroupState(StudentStateEnum.PERIOD_ONE_A.getState());
                StudentExecution studentExecution = studentService.updateGroup(group);
                if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                    log.info("[WEB-S]阶段一数据-sID" + groupId);
                } else {
                    modelMap.put("success", false);
                    throw new StudentOperateException("学生账号状态更新失败！");
                }
            }

        } catch (IOException e) {
            log.error("图片保存失败" + e.getMessage());
            e.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", "图片保存出错");
            return modelMap;
        } catch (StudentOperateException se) {
            log.error("StudentPicture数据库插入出错" + se.getMessage());
            se.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", "数据库插入出错");
            return modelMap;
        }
        return modelMap;
    }

    @RequestMapping(value = "/get-student-picture", method = RequestMethod.GET)
//    @ResponseBody
    private Map<String, Object> getPeriodOneData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer groupId = (Integer) request.getSession().getAttribute("groupId");
        if (groupId == null || groupId < 0) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "小组信息出错");
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
                        log.info("[WEB-S]获取图片-sID" + groupId);

                    } catch (Exception e) {
                        log.error("文件读取失败");
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
                            log.error("输入输出流关闭失败" + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                }

            } else if (studentExecution.getState() == StateEnum.EMPTY_RETURN.getState()) {
                modelMap.put("success", true);
                modelMap.put("studentPicture", null);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "阶段一成果获取出错");
            }
        }
        return modelMap;

    }

    @RequestMapping(value = "/add-point-data", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addPeriodTwoData(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        Integer groupId = (Integer) request.getSession().getAttribute("groupId");
        StudentExecution studentExecutionForState = studentService.getGroupByGroupID(groupId);
        int state = studentExecutionForState.getGroup().getGroupState();
        if (state<StudentStateEnum.PERIOD_ONE_C.getState()){
            modelMap.put("success",false);
            modelMap.put("errMsg","请先完成前面的任务");
            return modelMap;
        }
        if (groupId == null || groupId < 0) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "学生信息出错");
            return modelMap;
        }

        Integer pointCount = HttpServletRequestUtil.getInt(request, "pointCount");
        if (pointCount == null || pointCount < 1) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "控制点获取失败");
        }

        List<StudentData> studentDataList = new ArrayList<>();
        Double[] accept = settingsService.getAccept();
        xyAccept = accept[0];
        hAccept = accept[1];
//        boolean passFlag;
//        passFlag = true;
        for (int i = 0; i < pointCount; i++) {
            //接收并转换相应的参数为对应的实体类
            String studentDataJSON = HttpServletRequestUtil.getString(request, "studentData-" + i);
            ObjectMapper mapper = new ObjectMapper();
            //每次都要新建一个对象，不然Java的引用机制会导致所有的AreaUsing是同一个
            StudentData studentData = new StudentData();
            try {
                studentData = mapper.readValue(studentDataJSON, StudentData.class);
            } catch (IOException e) {
                e.printStackTrace();
                modelMap.put("success", false);
                modelMap.put("errMsg", e.getMessage());
                return modelMap;
            }
            Integer pointId = studentData.getPointId();
            try {
                PointExecution pointExecution = pointService.getPointByPointId(pointId);
                if (pointExecution.getState() == StateEnum.SUCCESS.getState()) {
                    Point point = pointExecution.getPoint();
                    double tolerateX = studentData.getPointX() - point.getPointX();
                    double tolerateY = studentData.getPointY() - point.getPointY();
                    double tolerateH = studentData.getPointH() - point.getPointH();
                    studentData.setTolerateX(tolerateX);
                    studentData.setTolerateY(tolerateY);
                    studentData.setTolerateH(tolerateH);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", pointExecution.getStateInfo());
                }


            } catch (Exception e) {
                log.error(e.getMessage());
                modelMap.put("success", false);
                modelMap.put("errMsg", e.getMessage());
                e.printStackTrace();
                return modelMap;
            }
            studentData.setUpdateTime(new Date());
            studentData.setStudentDataState(CommonStateEnum.INITIAL.getState());
            studentData.setGroupId(groupId);
            studentDataList.add(studentData);
        }

        //插入数据库
        try {
            //先将原有数据清空
            studentService.deleteStudentDataByGroupId(groupId);
            //再插入数据
            StudentExecution studentExecution = studentService.addStudentData(studentDataList);
            if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                //成功插入数据就返回true
                modelMap.put("success", true);

                //更新状态
                Group group = new Group();
                group.setGroupId(groupId);

                group.setGroupState(StudentStateEnum.PERIOD_TWO_A.getState());

                StudentExecution studentExecution1 = studentService.updateGroup(group);
                if (studentExecution1.getState() != StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", false);
                    throw new StudentOperateException("学生账号状态更新失败！");
                }
                log.info("[WEB-S]上传阶段二数据-sID" + groupId);

            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", studentExecution.getStateInfo());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new StudentOperateException("插入Data失败");
        }
        return modelMap;
    }

    @RequestMapping(value = "/get-point-data", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getPointData(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer groupId = (Integer) request.getSession().getAttribute("groupId");
        StudentExecution studentExecution = studentService.getStudentDataList(groupId);
        if (studentExecution.getState() == StateEnum.SUCCESS.getState() && studentExecution.getStudentDataList().size() > 0) {
            modelMap.put("success", true);
            modelMap.put("dataList", studentExecution.getStudentDataList());
            log.info("[WEB-S]获取阶段二数据-sID" + groupId);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", studentExecution.getStateInfo());
        }
        return modelMap;
    }

//    private double calDifferOfPoint(StudentData inData, Point point) {
//
//        if (Math.abs(inData.getPointX() - point.getPointX()) > xyAccept) {
//            return false;
//        }
//        if (Math.abs(inData.getPointY() - point.getPointY()) > xyAccept) {
//            return false;
//        }
//        if (Math.abs(inData.getPointH() - point.getPointH()) > hAccept) {
//            return false;
//        }
//        return true;
//
//    }

}
