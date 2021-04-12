package com.perry.cnms.web.admin;

import com.perry.cnms.dto.AreaExecution;
import com.perry.cnms.dto.AreaUseExecution;
import com.perry.cnms.dto.PointExecution;
import com.perry.cnms.entity.Area;
import com.perry.cnms.entity.AreaUsing;
import com.perry.cnms.entity.Point;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.exceptions.AreaOperateException;
import com.perry.cnms.service.AreaService;
import com.perry.cnms.service.AreaUseService;
import com.perry.cnms.service.PointService;
import com.perry.cnms.util.CalculateUtil;
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
 * @Date: 2020/1/16
 */
@Controller
@RequestMapping("/admin")
public class AreaAdminController {
    //最大上传图片数
    private static final int MAX_IMAGE_COUNT = 100;
    private Logger log = LoggerFactory.getLogger(AreaAdminController.class);
    @Autowired
    private AreaService areaService;

    @Autowired
    private PointService pointService;
    @Autowired
    private AreaUseService areaUseService;

    //HttpServletRequest--网页的所有信息
    @RequestMapping(value = "/add-area", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addArea(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }

        //1.接收信息并处理
        MultipartHttpServletRequest multipartHttpServletRequest = null;
        List<CommonsMultipartFile> areaImgList = new ArrayList<>();
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request
                .getSession().getServletContext());
        try {
            //处理文件流（图片）
            if (commonsMultipartResolver.isMultipart(request)) {
                multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                for (int i = 0; i < MAX_IMAGE_COUNT; i++) {
                    CommonsMultipartFile tmpAreaImg = (CommonsMultipartFile) multipartHttpServletRequest
                            .getFile("areaImg" + i);
                    if (tmpAreaImg != null) {
                        areaImgList.add(tmpAreaImg);
                    } else {
                        break;
                    }
                }
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "上传图片不能为空");
                return modelMap;
            }
        } catch (Exception e) {
            log.error("图片处理失败" + e.getMessage());
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        //2.添加到数据库
        if (areaImgList.size() > 0) {
            File tmpFile = null;
            List<Area> areaList = new ArrayList<>(40);
            String areaAddress = null;
            String areaImagePath = PathUtil.getAreaImagePath();
            PathUtil.makeDirPath(areaImagePath);
            for (int i = 0; i < areaImgList.size(); i++) {
                CommonsMultipartFile img = areaImgList.get(i);
                String imgName = PathUtil.getImgName(img);
                if (imgName.equals("error")) {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "图片处理未完成，已成功" + i + "个");
                    return modelMap;
                } else {
                    areaAddress = areaImagePath + PathUtil.separator + imgName + PathUtil.getFileExtension(img);
                    try {
                        //图片保存,实体类创建
                        boolean isExists = false;
                        tmpFile = new File(areaAddress);
                        //先判断文件是否已存在
                        if (tmpFile.exists()) {
                            isExists = true;
                            PathUtil.deleteFileOrPath(areaAddress);
                        }
                        //保存新的文件
                        img.transferTo(tmpFile);

                        Area area = new Area();
                        area.setUpdateTime(new Date());

                        //文件不存在添加到 新增 数组中，否则执行更新
                        if (!isExists) {
                            area.setAreaName(imgName);
                            area.setAreaPictureAddress(areaAddress);
                            area.setAreaState(1);
                            areaList.add(area);
                        } else {
                            //先获取已存在的area id
                            AreaExecution areaExecution = areaService.getIdByName(imgName);
                            if (areaExecution.getState() == StateEnum.SUCCESS.getState()) {
                                area.setAreaId(areaExecution.getAreaId());
                                areaService.updateArea(area);
                                modelMap.put("success", true);
                            } else {
                                modelMap.put("success", false);
                            }
                        }


                    } catch (IOException e) {
                        log.error("图片保存失败");
                        e.printStackTrace();
                        modelMap.put("success", false);
                        modelMap.put("errMsg", "图片存储失败");
                        return modelMap;
                    }
                }
            }
            log.info("[WEB-A]场地图片保存+" + areaImgList.size());
            if (areaList.size() > 0) {
                try {
                    //执行添加操作
                    AreaExecution areaExecution = areaService.addAreaList(areaList);
                    if (areaExecution.getState() == StateEnum.SUCCESS.getState()) {
                        log.info("[WEB-A]场地创建+" + areaList.size());
                        modelMap.put("success", true);
                    } else {
                        modelMap.put("success", false);
                        modelMap.put("errMsg", areaExecution.getStateInfo());
                    }
                } catch (AreaOperateException aoe) {
                    aoe.printStackTrace();
                    log.error(aoe.getMessage());
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "场地创建失败");
                    return modelMap;
                }
            }
        }
        //3.返回信息到前端
        return modelMap;
    }

    @RequestMapping(value = "/get-areas", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getAreas(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        AreaExecution areaExecution = areaService.getAreaList();
        if (areaExecution.getState() == StateEnum.SUCCESS.getState() && areaExecution.getAreaList() != null) {
            modelMap.put("success", true);
            modelMap.put("areaList", areaExecution.getAreaList());
            log.info("[WEB-A]|[WEB-T]获取场地+" + areaExecution.getAreaList().size());
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "查询场地数据失败");
        }
        return modelMap;
    }

    @RequestMapping(value = "/get-area-info", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getAreaInfoById(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        Integer areaId = HttpServletRequestUtil.getInt(request, "areaId");
        AreaExecution areaExecution = areaService.getAreaById(areaId);
        PointExecution pointExecution = pointService.getPointListByAreaId(areaId);
        if (pointExecution.getState() == StateEnum.EMPTY_RETURN.getState() && areaExecution.getState() == StateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
            modelMap.put("area", areaExecution.getArea());
        } else if (areaExecution.getState() == StateEnum.SUCCESS.getState() && pointExecution.getState() == StateEnum.SUCCESS.getState()) {
            //计算方位角并传入前端，减少前端响应时间
            Area area = areaExecution.getArea();
            List<AreaUsing> areaUsingList = new ArrayList<>();
            if (area.getAreaState() == 3) {
                //获取使用情况
                AreaUseExecution areaUseExecution = areaUseService.getAreaUsingByAreaId(areaId);
                if (areaUseExecution.getState() == StateEnum.SUCCESS.getState()) {
                    areaUsingList = areaUseExecution.getAreaUsingList();
                }
            }
            List<Point> pointList = pointExecution.getPointList();
            double dx = pointList.get(1).getPointX() - pointList.get(0).getPointX();
            double dy = pointList.get(1).getPointY() - pointList.get(0).getPointY();
            double alpha = CalculateUtil.radianToDoubleDegree(CalculateUtil.calculateAlpha(dx, dy));
//            area.setAlpha(alpha);

            modelMap.put("success", true);
            modelMap.put("area", area);
            modelMap.put("alpha", alpha);
            modelMap.put("pointList", pointList);
            modelMap.put("areaUsingList", areaUsingList);
            log.info("[WEB-A]|[WEB-T]获取场地信息-" + areaExecution.getArea().getAreaName());
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "获取场地信息失败");
        }
        return modelMap;
    }

    @RequestMapping(value = "/delete-area", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> deleteArea(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        Integer areaId = HttpServletRequestUtil.getInt(request, "areaId");
        try {
            PointExecution pointExecution0 = pointService.getPointListByAreaId(areaId);
            if (pointExecution0.getState() == StateEnum.EMPTY_RETURN.getState()) {
                AreaExecution areaExecution = areaService.deleteArea(areaId);
                if (areaExecution.getState() == StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                    log.info("[WEB-A]场地（无控制点）删除-aID" + areaId);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "场地删除失败");
                }
            } else {
                PointExecution pointExecution1 = pointService.deletePointByAreaId(areaId);
                if (pointExecution1.getState() == StateEnum.SUCCESS.getState()) {
                    AreaExecution areaExecution = areaService.deleteArea(areaId);
                    if (areaExecution.getState() == StateEnum.SUCCESS.getState()) {
                        log.info("[WEB-A]场地（含控制点）删除-aID" + areaId);
                        modelMap.put("success", true);
                    } else {
                        modelMap.put("success", false);
                        modelMap.put("errMsg", "场地删除失败");
                    }
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "控制点未成功删除");
                }
            }

        } catch (Exception e) {
            log.error("场地删除失败" + e.getMessage());
            modelMap.put("success", false);
            modelMap.put("errMsg", "删除失败");
            e.printStackTrace();
            return modelMap;
        }
        return modelMap;
    }

    @RequestMapping(value = "/get-image", method = RequestMethod.GET)
    private void getPictureByAddress(HttpServletResponse response, HttpServletRequest request) {
        //通过url的areaName参数获取存储在本地的图片，并将流返回至前端
        String name = HttpServletRequestUtil.getString(request, "areaName");
        String imgPath = PathUtil.getAreaImagePath() + PathUtil.separator + name + ".png";
        File imageFile = new File(imgPath);
        if (imageFile.exists()) {
            FileInputStream fis = null;
            OutputStream os = null;
            try {
                //获取输入流并写入到输出流
                fis = new FileInputStream(imageFile);
                os = response.getOutputStream();
                int count = 0;
                byte[] buffer = new byte[1024 * 8];
                while ((count = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                    os.flush();
                }
                log.info("[WEB-A]|[WEB-T]场地图片获取-" + name);

            } catch (Exception e) {
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
                    e.printStackTrace();
                }
            }

        }

    }

    @RequestMapping(value = "/update-area", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> updateArea(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        Integer areaId = HttpServletRequestUtil.getInt(request, "areaId");
        if (areaId == null || areaId < 0) {
            modelMap.put("errMmg", "Area 错误");
            return modelMap;
        } else {
            String editPointIdList = HttpServletRequestUtil.getString(request, "editPointIdList");
            String editPointValueList = HttpServletRequestUtil.getString(request, "editPointValueList");
            String[] pointIdList = null;
            String[] pointValueList = null;

            if (editPointIdList != null && editPointValueList != null
                    && !"".equals(editPointIdList) && !"".equals(editPointValueList)) {
                pointIdList = editPointIdList.split(",");
                pointValueList = editPointValueList.split(",");
            }
            List<Point> afterPointList = new ArrayList<>();
            if (pointIdList != null && pointIdList.length != 0) {
                //从数据库中读取原有数据进行比对是否变换，并留作后续计算
                List<Point> beforePointList = pointService.getPointListByAreaId(areaId).getPointList();
                for (int beforeIndex = 0; beforeIndex < beforePointList.size(); beforeIndex++) {
                    Point tmpPoint = beforePointList.get(beforeIndex);
                    int valueIndex = 0;
                    boolean changeFlag = false;
                    for (int editIndex = 0; editIndex < pointIdList.length; editIndex++) {
                        int pointId = Integer.parseInt(pointIdList[editIndex]);
                        if (pointId == tmpPoint.getPointId()) {
                            valueIndex = editIndex * 3;
                            Point point = new Point();
                            point.setPointId(pointId);
                            point.setPointX(Double.parseDouble(pointValueList[valueIndex]));
                            point.setPointY(Double.parseDouble(pointValueList[valueIndex + 1]));
                            point.setPointH(Double.parseDouble(pointValueList[valueIndex + 2]));
                            point.setUpdateTime(new Date());
                            //将不变更的数据,但是后续要用的数据也读取出来，便于后续计算
                            point.setPointName(tmpPoint.getPointName());
                            point.setNextPoint(tmpPoint.getNextPoint());
                            afterPointList.add(point);
                            changeFlag = true;
                        }

                    }
                    if (!changeFlag) {
                        afterPointList.add(tmpPoint);
                    }
                }
                //计算距离，如果没有数据变动，afterPointList将会为空，不进行距离计算
                if (afterPointList.size() != 0) {

                    for (int i = 0; i < afterPointList.size(); i++) {
                        //当前点
                        Point thisPoint = afterPointList.get(i);
                        String nextPointName = thisPoint.getNextPoint();

                        //从列表中获取下一点数据
                        Point nextPoint = null;
                        for (Point loopPoint : afterPointList) {
                            if (nextPointName.equals(loopPoint.getPointName())){
                                nextPoint=loopPoint;
                                break;
                            }
                        }
                        assert nextPoint != null;
                        afterPointList.get(i).setDistance(CalculateUtil.calDistance(thisPoint, nextPoint));

                    }
                }
            }


            Integer areaState = HttpServletRequestUtil.getInt(request, "areaState");
            Area area = new Area();
            area.setAreaState(areaState);
            area.setAreaId(areaId);
            area.setUpdateTime(new Date());
            try {
                if (afterPointList.size() != 0) {
                    PointExecution pointExecution = pointService.updatePoints(afterPointList);
                    if (pointExecution.getState() != StateEnum.SUCCESS.getState()) {
                        modelMap.put("success", false);
                        modelMap.put("errMsg", "控制点更新失败");
                        return modelMap;
                    }
                }
                AreaExecution areaExecution = areaService.updateArea(area);
                if (areaExecution.getState() == StateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                    log.info("[WEB-A]场地更新-aID" + areaId);
                    return modelMap;
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", areaExecution.getStateInfo());
                }
            } catch (AreaOperateException e) {
                log.error("更新失败" + e.getMessage());
                e.printStackTrace();
                modelMap.put("errMsg", "数据库更新失败");
                return modelMap;
            }

        }

        return modelMap;
    }
}
