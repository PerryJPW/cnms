package com.perry.cnms.web.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perry.cnms.dto.AreaExecution;
import com.perry.cnms.dto.AreaUseExecution;
import com.perry.cnms.dto.StudentExecution;
import com.perry.cnms.entity.Area;
import com.perry.cnms.entity.AreaUsing;
import com.perry.cnms.entity.Group;
import com.perry.cnms.entity.Settings;
import com.perry.cnms.enums.CommonStateEnum;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.exceptions.AreaOperateException;
import com.perry.cnms.exceptions.AreaUsingOperateException;
import com.perry.cnms.exceptions.StudentOperateException;
import com.perry.cnms.service.AreaService;
import com.perry.cnms.service.AreaUseService;
import com.perry.cnms.service.SettingsService;
import com.perry.cnms.service.StudentService;
import com.perry.cnms.util.HttpServletRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: PerryJ
 * @Date: 2020/2/10
 */
@Controller
@RequestMapping("/admin")
public class SettingsController {
    private Logger log = LoggerFactory.getLogger(SettingsController.class);

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AreaUseService areaUseService;
    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/get-settings", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getSettings(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        String pass = settingsService.getAdminPassword();
        Double[] accept = settingsService.getAccept();
        if (pass == null || accept == null) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "??????????????????");
        } else {
            modelMap.put("success", true);
            modelMap.put("adminPassword", pass);
            modelMap.put("acceptXy", accept[0]);
            modelMap.put("acceptH", accept[1]);
            log.info("[WEB-A]????????????");
        }
        return modelMap;
    }

    @RequestMapping(value = "/update-settings", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> updateSettings(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        String settingsJSON = HttpServletRequestUtil.getString(request, "settingsString");
        ObjectMapper mapper = new ObjectMapper();
        try {
            Settings settings = mapper.readValue(settingsJSON, Settings.class);
            settings.setSettingId(1);
            int effectNum = settingsService.setSettings(settings);
            if (effectNum != 1) {
                modelMap.put("success", false);
                modelMap.put("errMsg", "????????????");
            } else {
                modelMap.put("success", true);
                log.info("[WEB-A]????????????");

            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return modelMap;
    }

    @RequestMapping(value = "/end-term", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> endTerm(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        //?????????????????????????????????
        StudentExecution studentExecution = studentService.getGroupList();
        if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
            List<Group> groupList = studentExecution.getGroupList();
            try {
                for (Group group : groupList) {
                    group.setGroupState(CommonStateEnum.HISTORY.getState());
                    studentService.updateGroup(group);
                }
                modelMap.put("success",true);
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", "????????????");
                log.error("Student??????????????????" + e.getMessage());
                throw new StudentOperateException("??????????????????" + e.getMessage());
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", studentExecution.getStateInfo());
        }
        //????????????????????????
        AreaUseExecution areaUseExecution=areaUseService.getAreaUsingList();
        if (areaUseExecution.getState() == StateEnum.SUCCESS.getState()) {
            List<AreaUsing> areaUsingList = areaUseExecution.getAreaUsingList();
            List<AreaUsing> areaUsingListAfter = new ArrayList<>();

            try {
                for (AreaUsing areaUsing : areaUsingList) {
                    areaUsing.setAreaUseState(CommonStateEnum.HISTORY.getState());
                    areaUsingListAfter.add(areaUsing);
                }
                areaUseService.updateAreaUse(areaUsingListAfter);
                modelMap.put("success",true);
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", "????????????");
                log.error("AreaUsing??????????????????" + e.getMessage());
                throw new AreaUsingOperateException("??????????????????" + e.getMessage());
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", areaUseExecution.getStateInfo());
        }
        //??????????????????
        AreaExecution areaExecution=areaService.getAreaList();
        if (areaExecution.getState() == StateEnum.SUCCESS.getState()) {
            List<Area> areaList = areaExecution.getAreaList();
            try {
                for (Area area : areaList) {
                    area.setAreaState(CommonStateEnum.INITIAL.getState());
                    areaService.updateArea(area);
                }
                modelMap.put("success",true);
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", "????????????");
                log.error("Area??????????????????" + e.getMessage());
                throw new AreaOperateException("??????????????????" + e.getMessage());
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", areaExecution.getStateInfo());
        }
        return modelMap;
    }
}
