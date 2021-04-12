package com.perry.cnms.service.impl;

import com.perry.cnms.dao.AreaDao;
import com.perry.cnms.dao.PointDao;
import com.perry.cnms.dto.PointExecution;
import com.perry.cnms.entity.Point;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.exceptions.PointOperateException;
import com.perry.cnms.service.PointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/22
 */
@Service
public class PointServiceImpl implements PointService {

    Logger log = LoggerFactory.getLogger(PointServiceImpl.class);

    @Autowired
    PointDao pointDao;
    @Autowired
    AreaDao areaDao;

    @Override
    public PointExecution addPointList(List<Point> pointList) throws PointOperateException {
        if (pointList == null || pointList.size() < 1) {
            return new PointExecution(StateEnum.EMPTY_PARAM);
        } else {
            for (int index = 0; index < pointList.size(); index++) {
                Point point = pointList.get(index);
                String areaName = point.getPointName().substring(0, point.getPointName().length() - 1);
                Integer areaId = areaDao.getAreaIdByName(areaName);
                if (areaId == null || areaId < 1) {
                    return new PointExecution(StateEnum.NULL_AREA_ID);
                }
                point.setAreaId(areaId);
                pointList.set(index, point);
            }
//            log.info("Point 的AreaID获取完毕");

            try {
                int effectNum = pointDao.batchInsertPoint(pointList);
//                log.info("Insert Point" + effectNum + "个");
                if (effectNum < 1) {
                    throw new PointOperateException("添加控制点失败");
                } else {
                    return new PointExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {

                throw new PointOperateException("批量添加控制点失败" + e.getMessage());
            }
        }
    }

    @Override
    public PointExecution getPointList() {
        List<Point> pointList = pointDao.queryPoint();
//        log.info("查询point，共获取" + pointList.size() + "个");
        if (pointList.size() == 0 || pointList == null) {
            return new PointExecution(StateEnum.EMPTY_RETURN);
        } else {
            return new PointExecution(StateEnum.SUCCESS, pointList);
        }
    }

    @Override
    public PointExecution getPointListByAreaId(Integer areaId) {
        if (areaId != null && areaId > 0) {
            List<Point> pointList = pointDao.getPointsByAreaId(areaId);
//            log.info("areaId=" + areaId + ";共获取" + pointList.size() + "个");
            if (pointList.size() == 0 || pointList == null) {
                return new PointExecution(StateEnum.EMPTY_RETURN);
            } else {
                return new PointExecution(StateEnum.SUCCESS, pointList);
            }
        } else {
            return new PointExecution(StateEnum.NULL_AREA_ID);
        }
    }

    @Override
    public PointExecution updatePoints(List<Point> pointList) {
        if (pointList == null || pointList.size() == 0) {
            return new PointExecution(StateEnum.EMPTY_PARAM);
        } else {
            try {
                for (Point point : pointList) {
                    pointDao.updatePoint(point);
                }
                return new PointExecution(StateEnum.SUCCESS);
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
        return new PointExecution(StateEnum.INNER_ERROR);

    }

    @Override
    public PointExecution deletePointByAreaId(Integer areaId) throws PointOperateException {
        if (areaId != null && areaId > 0) {
            int effectNum = pointDao.deletePointByAreaId(areaId);
            if (effectNum > 0) {
                return new PointExecution(StateEnum.SUCCESS);
            } else {
                return new PointExecution(StateEnum.EMPTY_RETURN);
            }
        } else {
            return new PointExecution(StateEnum.NULL_AREA_ID);
        }
    }

    @Override
    public PointExecution getPointByPointId(Integer pointId) {
        if (pointId != null && pointId > 0) {
            Point point = pointDao.getPoint(pointId,null);
//            log.info("pointId=" + pointId);
            if (point == null) {
                return new PointExecution(StateEnum.EMPTY_RETURN);
            } else {
                return new PointExecution(StateEnum.SUCCESS, point);
            }
        } else {
            return new PointExecution(StateEnum.NULL_POINT_ID);
        }
    }

    @Override
    public PointExecution getPointByPointName(String pointName) {
        if (pointName != null && !"".equals(pointName) ) {
            Point point = pointDao.getPoint(null,pointName);
//            log.info("pointId=" + pointId);
            if (point == null) {
                return new PointExecution(StateEnum.EMPTY_RETURN);
            } else {
                return new PointExecution(StateEnum.SUCCESS, point);
            }
        } else {
            return new PointExecution(StateEnum.EMPTY_PARAM);
        }
    }

}
