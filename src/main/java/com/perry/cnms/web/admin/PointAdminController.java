package com.perry.cnms.web.admin;

import com.perry.cnms.dto.PointExecution;
import com.perry.cnms.entity.Point;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.service.PointService;
import com.perry.cnms.util.ExcelUtil;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: PerryJ
 * @Date: 2020/1/22
 */

@Controller
@RequestMapping(value = "/admin")
public class PointAdminController {

    @Autowired
    PointService pointService;
    private Logger log = LoggerFactory.getLogger(PointAdminController.class);


    @RequestMapping(value = "/add-point")
    @ResponseBody
    private Map<String, Object> addPointByExcel(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        List<Point> pointList = new ArrayList<>();
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
                CommonsMultipartFile tmpPointExcel = (CommonsMultipartFile) multipartHttpServletRequest
                        .getFile("pointExcel");
                if (tmpPointExcel != null) {
                    InputStream inputStream = tmpPointExcel.getInputStream();
                    String fileExtension = PathUtil.getFileExtension(tmpPointExcel);
                    pointList = ExcelUtil.getPointsFromExcel(inputStream, fileExtension);
                }
            }
        } catch (IOException ioe) {
            log.error("控制点表的InputStream获取失败");
            ioe.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg","Excel表读取失败");
            return modelMap;
        }
        if (pointList == null) {
            modelMap.put("success", false);
        } else {
            PointExecution pointExecution = null;
            try {
                pointExecution = pointService.addPointList(pointList);
                if (pointExecution.getState() == StateEnum.SUCCESS.getState()) {
                    log.info("[WEB]控制点添加+"+pointList.size());
                    modelMap.put("success", true);
                } else if (pointExecution.getState() == StateEnum.NULL_AREA_ID.getState()){
                    modelMap.put("success", false);
                    modelMap.put("errMsg","未创建场地");
                }
            } catch (Exception e) {
                log.error("控制点添加出错"+e.getMessage());
                e.printStackTrace();
                modelMap.put("success",false);
                modelMap.put("errMsg","插入数据库失败");
                return modelMap;
            }
        }
        return modelMap;
    }

    @RequestMapping(value = "get-points",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getPoints(HttpServletRequest request){
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        PointExecution pointExecution=pointService.getPointList();
        if (pointExecution.getState()==StateEnum.SUCCESS.getState()){
            modelMap.put("success", true);
            modelMap.put("pointList", pointExecution.getPointList());
            log.info("[WEB]控制点获取"+pointExecution.getPointList().size());
        }else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "查询控制点数据失败");
        }

        return modelMap;

    }
}
