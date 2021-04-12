package com.perry.cnms.service;

import com.perry.cnms.dto.PointExecution;
import com.perry.cnms.entity.Point;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/22
 */
public interface PointService {
    /**
     * 批量添加控制点
     *
     * @param pointList
     * @return
     */
    PointExecution addPointList(List<Point> pointList);

    /**
     * 查询Point
     *
     * @return
     */
    PointExecution getPointList();

    /**
     * 查询场地下的所有控制点
     *
     * @param areaId
     * @return
     */
    PointExecution getPointListByAreaId(Integer areaId);

    PointExecution updatePoints(List<Point> pointList);

    PointExecution deletePointByAreaId(Integer areaId);

    PointExecution getPointByPointId(Integer pointId);

    PointExecution getPointByPointName(String pointName);
}
