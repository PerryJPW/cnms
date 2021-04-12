package com.perry.cnms.web.student;

import com.perry.cnms.dto.*;
import com.perry.cnms.entity.Point;
import com.perry.cnms.entity.TeacherData;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.service.*;
import com.perry.cnms.util.CalculateUtil;
import com.perry.cnms.util.HttpServletRequestUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: PerryJ
 * @Date: 2020/2/2
 */

@Controller
@RequestMapping(value = "/student")
public class AreaInfoController {

    @Autowired
    private AreaService areaService;
    @Autowired
    private PointService pointService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private AreaUseService areaUseService;
    private Logger log = LoggerFactory.getLogger(AreaInfoController.class);


    @RequestMapping(value = "/get-teacher-data", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getTeacherData(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer groupId = (Integer) request.getSession().getAttribute("groupId");
        StudentExecution studentExecution = studentService.getGroupByGroupID(groupId);
        if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
            Integer teacherId = studentExecution.getGroup().getTeacherId();
            TeacherExecution teacherExecution = teacherService.getTeacherDataListByTeacherId(teacherId);
            if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
                modelMap.put("teacherDataList", teacherExecution.getTeacherDataList());
                log.info("[WEB-S]获取教师资料-ID"+groupId);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", teacherExecution.getStateInfo());
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", studentExecution.getStateInfo());
        }
        return modelMap;
    }

    @RequestMapping(value = "/download-file", method = RequestMethod.GET)
    private ResponseEntity<byte[]> downloadFile(HttpServletRequest request, @RequestHeader("User-Agent") String userAgent) throws IOException {
        Integer teacherDataId = HttpServletRequestUtil.getInt(request, "id");
        TeacherData teacherData = teacherService.getTeacherDataByTeacherDataId(teacherDataId).getTeacherDataList().get(0);
        String filePath = teacherData.getTeacherDataAddress();
        File file = new File(filePath);
        if (file.exists()) {
            ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
            // 内容长度
            builder.contentLength(file.length());
            // application/octet-stream 二进制数据流（最常见的文件下载）
            builder.contentType(MediaType.APPLICATION_OCTET_STREAM);
//            builder.contentType(MediaType.IMAGE_PNG);
            // 使用URLEncoding.decode对文件名进行解码
            String filename = teacherData.getTeacherDataName();
            filename = URLEncoder.encode(filename, "UTF-8");
            // 根据浏览器类型，决定处理方式
            if (userAgent.indexOf("MSIE") > 0) {
                builder.header("Content-Disposition", "attachment; filename=" + filename);
            } else {
                builder.header("Content-Disposition", "attacher; filename*=UTF-8''" + filename);
            }
            log.info("[WEB-S]|[WEB-T]教师资料下载-dataID"+teacherDataId);
            return builder.body(FileUtils.readFileToByteArray(file));
        }
        return null;
    }

    @RequestMapping(value = "/get-area-info", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getAreaInfoOfStudent(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
//        权限
//        request.getSession().setAttribute("groupId", 5);
        Integer groupId = (Integer) request.getSession().getAttribute("groupId");

        AreaUseExecution areaUseExecution = areaUseService.getAreaUsingByGroupId(groupId);
        if (areaUseExecution.getState() == StateEnum.SUCCESS.getState() && areaUseExecution.getAreaUsing() != null) {
            modelMap.put("startTime", areaUseExecution.getAreaUsing().getStartTime());
            modelMap.put("endTime", areaUseExecution.getAreaUsing().getEndTime());
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "没有场地使用数据");
            return modelMap;
        }

        AreaExecution areaExecution = areaService.getAreaById(areaUseExecution.getAreaUsing().getAreaId());
        if (areaExecution.getState() == StateEnum.SUCCESS.getState()) {
            modelMap.put("area", areaExecution.getArea());
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "场地数据获取失败");
            return modelMap;
        }
        PointExecution pointExecution = pointService.getPointListByAreaId(areaUseExecution.getAreaUsing().getAreaId());
        if (pointExecution.getState() == StateEnum.SUCCESS.getState()) {
            List<Point> pointList = pointExecution.getPointList();
            double dx = pointList.get(1).getPointX() - pointList.get(0).getPointX();
            double dy = pointList.get(1).getPointY() - pointList.get(0).getPointY();
            double alpha = CalculateUtil.radianToDoubleDegree(CalculateUtil.calculateAlpha(dx, dy));
            List<SPoint> sPointList = new ArrayList<>();
            modelMap.put("point", pointList.get(0));
            for (Point point : pointList) {
                sPointList.add(new SPoint(point));
            }
            modelMap.put("success", true);
            modelMap.put("alpha", alpha);
            modelMap.put("pointDistanceList", sPointList);
            log.info("[WEB-S]获取场地信息-sID"+groupId);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "距离数据获取失败");
        }
        return modelMap;
    }

    class SPoint {
        private String name1;
        private String name2;
        private Double distance;
        private Integer pointId;

        SPoint(Point point) {
            this.name1 = point.getPointName();
            this.name2 = point.getNextPoint();
            this.distance = point.getDistance();
            this.pointId = point.getPointId();
        }

        public Integer getPointId() {
            return pointId;
        }

        public void setPointId(Integer pointId) {
            this.pointId = pointId;
        }

        public String getName1() {
            return name1;
        }

        public void setName1(String name1) {
            this.name1 = name1;
        }

        public String getName2() {
            return name2;
        }

        public void setName2(String name2) {
            this.name2 = name2;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }
    }

}

