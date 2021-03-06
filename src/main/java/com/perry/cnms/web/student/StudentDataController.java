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
            log.info("[WEB-S]??????????????????-sID" + groupId);
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
            modelMap.put("errMsg", "??????????????????");
            return modelMap;
        }
        Integer groupId = (Integer) request.getSession().getAttribute("groupId");
        if (groupId == null || groupId < 0) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "??????????????????");
            return modelMap;
        }
        //?????????????????????
//        Integer groupState = (Integer) request.getSession().getAttribute("groupState");
//        if (groupState == null || groupState < StudentStateEnum.PERIOD_ZERO.getState()
//                || groupState == StudentStateEnum.HISTORY.getState()) {
//            modelMap.put("success", false);
//            modelMap.put("errMsg", "??????????????????");
//            return modelMap;
//        }

        //1.?????????????????????
        CommonsMultipartFile tmpDataComFile = null;
        MultipartHttpServletRequest multipartHttpServletRequest = null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request
                .getSession().getServletContext());
        try {
            //???????????????
            if (commonsMultipartResolver.isMultipart(request)) {
                multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                tmpDataComFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("studentPicture");
                if (tmpDataComFile == null) {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "????????????????????????");
                    return modelMap;
                }

            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "????????????????????????");
                return modelMap;
            }
        } catch (Exception e) {
            log.error("??????????????????" + e.getMessage());
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        log.info("[WEB-S]??????????????????-sID" + groupId);


        //2.??????????????????
        String studentPath = PathUtil.getStudentPicturePath();
        PathUtil.makeDirPath(studentPath);
        String studentDataAddress = studentPath + PathUtil.separator
                + PathUtil.getStudentFileName(groupId) + PathUtil.getFileExtension(tmpDataComFile);
        try {
            //????????????,???????????????
            File tmpFile = new File(studentDataAddress);

            //??????????????????
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
                    log.info("[WEB-S]???????????????-sID" + groupId);
                } else {
                    modelMap.put("success", false);
                    throw new StudentOperateException("?????????????????????????????????");
                }
            }

        } catch (IOException e) {
            log.error("??????????????????" + e.getMessage());
            e.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", "??????????????????");
            return modelMap;
        } catch (StudentOperateException se) {
            log.error("StudentPicture?????????????????????" + se.getMessage());
            se.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", "?????????????????????");
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
            modelMap.put("errMsg", "??????????????????");
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
                        //???????????????????????????response?????????
                        fis = new FileInputStream(imageFile);
                        os = response.getOutputStream();
                        int count = 0;
                        byte[] buffer = new byte[1024 * 8];
                        while ((count = fis.read(buffer)) != -1) {
                            os.write(buffer, 0, count);
                            os.flush();
                        }
                        log.info("[WEB-S]????????????-sID" + groupId);

                    } catch (Exception e) {
                        log.error("??????????????????");
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
                            log.error("???????????????????????????" + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                }

            } else if (studentExecution.getState() == StateEnum.EMPTY_RETURN.getState()) {
                modelMap.put("success", true);
                modelMap.put("studentPicture", null);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "???????????????????????????");
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
            modelMap.put("errMsg", "??????????????????");
            return modelMap;
        }
        Integer groupId = (Integer) request.getSession().getAttribute("groupId");
        StudentExecution studentExecutionForState = studentService.getGroupByGroupID(groupId);
        int state = studentExecutionForState.getGroup().getGroupState();
        if (state<StudentStateEnum.PERIOD_ONE_C.getState()){
            modelMap.put("success",false);
            modelMap.put("errMsg","???????????????????????????");
            return modelMap;
        }
        if (groupId == null || groupId < 0) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "??????????????????");
            return modelMap;
        }

        Integer pointCount = HttpServletRequestUtil.getInt(request, "pointCount");
        if (pointCount == null || pointCount < 1) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "?????????????????????");
        }

        List<StudentData> studentDataList = new ArrayList<>();
        Double[] accept = settingsService.getAccept();
        xyAccept = accept[0];
        hAccept = accept[1];
//        boolean passFlag;
//        passFlag = true;
        for (int i = 0; i < pointCount; i++) {
            //???????????????????????????????????????????????????
            String studentDataJSON = HttpServletRequestUtil.getString(request, "studentData-" + i);
            ObjectMapper mapper = new ObjectMapper();
            //???????????????????????????????????????Java?????????????????????????????????AreaUsing????????????
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

        //???????????????
        try {
            //????????????????????????
            studentService.deleteStudentDataByGroupId(groupId);
            //???????????????
            StudentExecution studentExecution = studentService.addStudentData(studentDataList);
            if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                //???????????????????????????true
                modelMap.put("success", true);

                //????????????
                Group group = new Group();
                group.setGroupId(groupId);

                group.setGroupState(StudentStateEnum.PERIOD_TWO_A.getState());

                StudentExecution studentExecution1 = studentService.updateGroup(group);
                if (studentExecution1.getState() != StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", false);
                    throw new StudentOperateException("?????????????????????????????????");
                }
                log.info("[WEB-S]?????????????????????-sID" + groupId);

            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", studentExecution.getStateInfo());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new StudentOperateException("??????Data??????");
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
            log.info("[WEB-S]?????????????????????-sID" + groupId);
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
