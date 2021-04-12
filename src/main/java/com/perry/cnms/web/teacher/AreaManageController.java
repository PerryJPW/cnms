package com.perry.cnms.web.teacher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perry.cnms.dto.AreaExecution;
import com.perry.cnms.dto.AreaUseExecution;
import com.perry.cnms.dto.StudentExecution;
import com.perry.cnms.entity.Area;
import com.perry.cnms.entity.AreaUsing;
import com.perry.cnms.entity.Group;
import com.perry.cnms.enums.CommonStateEnum;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.enums.StudentStateEnum;
import com.perry.cnms.exceptions.AreaOperateException;
import com.perry.cnms.exceptions.StudentOperateException;
import com.perry.cnms.service.AreaService;
import com.perry.cnms.service.AreaUseService;
import com.perry.cnms.service.StudentService;
import com.perry.cnms.util.HttpServletRequestUtil;
import com.perry.cnms.util.VerifyCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @Author: PerryJ
 * @Date: 2020/2/1
 */
@Controller
@RequestMapping(value = "/teacher")
public class AreaManageController {
    @Autowired
    private AreaUseService areaUseService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AreaService areaService;
    private Logger log = LoggerFactory.getLogger(AreaManageController.class);

    @RequestMapping(value = "/get-area-use-teacher", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getAreaUseOfTeacher(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");

        AreaUseExecution areaUseExecution = areaUseService.getAreaUsingByTeacherId(teacherId);
        if (areaUseExecution.getState() == StateEnum.SUCCESS.getState()) {

            List<AreaUsing> areaUsingList = areaUseExecution.getAreaUsingList();
            List<TmpAreaUse> reList = new ArrayList<>();
            String tmpClassName = "init";
            for (AreaUsing areaUsing : areaUsingList) {
                String className = studentService.getGroupByGroupID(areaUsing.getGroupId()).getGroup().getMajorCode();
                if (tmpClassName.equals(className)) {
                    continue;
                } else {
                    tmpClassName = className;
                    TmpAreaUse tmpAreaUse = new TmpAreaUse();
                    Integer areaId = areaUsing.getAreaId();
                    tmpAreaUse.areaId = areaId;
                    tmpAreaUse.areaName = areaService.getAreaById(areaId).getArea().getAreaName();
                    tmpAreaUse.className = className;
                    reList.add(tmpAreaUse);
                }
            }
            modelMap.put("success", true);
            modelMap.put("areaUsingList", reList);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", areaUseExecution.getStateInfo());
        }

        return modelMap;
    }

    @RequestMapping(value = "/apply-area", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> applyForArea(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        Integer areaId = null;
        //获取对应专业班级的小组,并将其id填入实体类中
        String majorCode = HttpServletRequestUtil.getString(request, "majorCode");
        StudentExecution studentExecution = studentService.getGroupIdsByMajorCode(majorCode);
        List<AreaUsing> areaUsingList = new ArrayList<>();
        List<Integer> groupIdList = new ArrayList<>();
        if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
            //接收并转换相应的参数为对应的实体类
            String areaUsingJSON = HttpServletRequestUtil.getString(request, "areaUsingString");
            ObjectMapper mapper = new ObjectMapper();
            groupIdList = studentExecution.getIdList();
            for (Integer groupId : groupIdList) {
                //每次都要新建一个对象，不然Java的引用机制会导致所有的AreaUsing是同一个
                AreaUsing areaUsing = new AreaUsing();
                try {
                    areaUsing = mapper.readValue(areaUsingJSON, AreaUsing.class);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    modelMap.put("success", false);
                    modelMap.put("errMsg", e.getMessage());
                    return modelMap;
                }
                areaId = areaUsing.getAreaId();

                areaUsing.setTeacherId(teacherId);
                areaUsing.setUpdateTime(new Date());
                areaUsing.setGroupId(groupId);
                areaUsingList.add(areaUsing);
            }

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "班级匹配出错");
            return modelMap;
        }
        //插入数据库
        try {
            AreaUseExecution areaUseExecution = areaUseService.addAreaUsingList(areaUsingList);
            if (areaUseExecution.getState() == StateEnum.SUCCESS.getState()) {
                //更新Area状态
                Area area = new Area();
                area.setAreaId(areaId);
                area.setAreaState(3);
                AreaExecution areaExecution = areaService.updateArea(area);
                if (areaExecution.getState() == StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "area状态更新失败");
                    throw new AreaOperateException("area状态更新失败");
                }
                //student更新
                for (Integer groupId : groupIdList) {
                    Group group = new Group();
                    group.setGroupId(groupId);
                    group.setGroupState(StudentStateEnum.PERIOD_ZERO.getState());
                    group.setUpdateTime(new Date());
                    StudentExecution studentExecution1 = studentService.updateGroup(group);
                    if (studentExecution1.getState() != StateEnum.SUCCESS.getState()) {
                        throw new StudentOperateException("Student 状态更新失败");
                    }
                }
                log.info("[WEB-T]场地申请-tID" + teacherId);

            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", areaUseExecution.getStateInfo());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("essMsg", "插入数据库失败");
            return modelMap;
        }

        return modelMap;
    }

    @RequestMapping(value = "get-none-area-class-name", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getNoneAreaMajorCodeByTeacherId(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        StudentExecution studentExecution = studentService.getNoneAreaMajorCodeByTeacherId(teacherId);
        if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
            modelMap.put("classNameList", studentExecution.getMajorCodeList());
            log.info("[WEB-T]获取无场地班级名称");
        } else if (studentExecution.getState() == StateEnum.EMPTY_RETURN.getState()) {
            modelMap.put("success", true);
            modelMap.put("classNameList", null);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", studentExecution.getStateInfo());
        }
        return modelMap;
    }

    @RequestMapping(value = "get-none-history-class-name", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getNoneHistoryMajorCodeByTeacherId(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        StudentExecution studentExecution = studentService.getNoneHistoryMajorCodeByTeacherId(teacherId);
        if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
            modelMap.put("classNameList", studentExecution.getGroupList());
            log.info("[WEB-T]获取全部班级名称");
        } else if (studentExecution.getState() == StateEnum.EMPTY_RETURN.getState()) {
            modelMap.put("success", true);
            modelMap.put("classNameList", null);
            log.debug("ForDifference");
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", studentExecution.getStateInfo());
        }
        return modelMap;
    }

    @RequestMapping(value = "/update-area-use", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> updateAreaUse(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        try {
            String areaUsingJSON = HttpServletRequestUtil.getString(request, "areaUsingString");
            ObjectMapper mapper = new ObjectMapper();
            AreaUsing tmpAreaUsing = mapper.readValue(areaUsingJSON, AreaUsing.class);

            Integer areaUseId = tmpAreaUsing.getAreaUseId();
            Date startTime = tmpAreaUsing.getStartTime();
            Date endTime = tmpAreaUsing.getEndTime();

            Integer groupId = areaUseService.getAreaUsingByAreaUseId(areaUseId).getAreaUsing().getGroupId();
            String className = studentService.getGroupByGroupID(groupId).getGroup().getMajorCode();
            List<Integer> groupIdList = studentService.getGroupIdsByMajorCode(className).getIdList();
            List<AreaUsing> areaUsingList = new ArrayList<>();
            for (Integer gId : groupIdList) {
                Integer aUId=areaUseService.getAreaUsingByGroupId(gId).getAreaUsing().getAreaUseId();
                AreaUsing areaUsing = new AreaUsing();
                areaUsing.setAreaUseId(aUId);
                areaUsing.setStartTime(startTime);
                areaUsing.setEndTime(endTime);
                areaUsing.setUpdateTime(new Date());
                areaUsingList.add(areaUsing);
            }

            if (areaUsingList.size() == 0) {
                modelMap.put("success", false);
                modelMap.put("errMsg", "更新失败，参数错误");
                return modelMap;
            }

            AreaUseExecution areaUseExecution = areaUseService.updateAreaUse(areaUsingList);
            if (areaUseExecution.getState() == StateEnum.SUCCESS.getState()) {
                modelMap.put("success", true);
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", areaUseExecution.getStateInfo());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            modelMap.put("errMsg", e.getMessage());
            modelMap.put("success", false);
            e.printStackTrace();
        }

        return modelMap;
    }


    @RequestMapping(value = "delete-area-use", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> deleteAreaUse(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer areaUseId = HttpServletRequestUtil.getInt(request, "areaUseId");
        AreaUsing areaUsing = areaUseService.getAreaUsingByAreaUseId(areaUseId).getAreaUsing();
        Integer areaId = areaUsing.getAreaId();
        Integer groupId = areaUsing.getGroupId();
        String className = studentService.getGroupByGroupID(groupId).getGroup().getMajorCode();
        List<Integer> groupIdList = studentService.getGroupIdsByMajorCode(className).getIdList();
        List<Integer> areaUseIdList = new ArrayList<>();
        for (Integer id : groupIdList) {
            Integer aUId = areaUseService.getAreaUsingByGroupId(id).getAreaUsing().getAreaUseId();
            areaUseIdList.add(aUId);
        }
        try {
            AreaUseExecution areaUseExecution = areaUseService.deleteArea(areaUseIdList);
            if (areaUseExecution.getState() == StateEnum.SUCCESS.getState()) {
                AreaUseExecution areaUseExecution1 = areaUseService.getAreaUsingByAreaId(areaId);
                if (areaUseExecution1.getState() == StateEnum.EMPTY_RETURN.getState()
                        || areaUseExecution1.getAreaUsingList().size() == 0) {
                    Area area = new Area();
                    area.setAreaId(areaId);
                    area.setAreaState(CommonStateEnum.INITIAL.getState());
                    AreaExecution areaExecution = areaService.updateArea(area);
                    if (areaExecution.getState() == StateEnum.SUCCESS.getState()) {
                        modelMap.put("success", true);
                        log.info("[WEB-T]删除场地使用");
                    } else {
                        log.error("场地状态改变失败");
                        throw new AreaOperateException("场地状态改变失败");
                    }
                } else {
                    modelMap.put("success", true);
                    log.info("[WEB-T]删除场地使用");
                }
                for (Integer gId : groupIdList) {
                    Group group = new Group();
                    group.setGroupId(gId);
                    group.setGroupState(StudentStateEnum.INITIAL.getState());
                    group.setUpdateTime(new Date());
                    StudentExecution studentExecution = studentService.updateGroup(group);
                    if (studentExecution.getState() != StateEnum.SUCCESS.getState()) {
                        modelMap.put("success",false);
                        modelMap.put("errMsg","学生状态更新失败");
                        log.error("学生状态更新失败");
                        throw new StudentOperateException("学生状态更新失败");
                    }
                }
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", areaUseExecution.getStateInfo());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error");
        }
        return modelMap;
    }

    class TmpAreaUse {
        private Integer areaId;
        private String areaName;
        private String className;

        TmpAreaUse() {
        }

        public Integer getAreaId() {
            return areaId;
        }

        public void setAreaId(Integer areaId) {
            this.areaId = areaId;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }
}
