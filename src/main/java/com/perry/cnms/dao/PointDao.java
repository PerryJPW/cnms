package com.perry.cnms.dao;

import com.perry.cnms.entity.Point;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/22
 */
public interface PointDao {
    /**
     * 批量添加控制点
     *
     * @param pointList
     * @return
     */
    int batchInsertPoint(List<Point> pointList);

    /**
     * 查询场地
     *
     * @return
     */
    List<Point> queryPoint();

    /**
     * 查询某场地下的所有控制点
     *
     * @return
     */
    List<Point> getPointsByAreaId(Integer areaId);

    int updatePoint(Point point);

    /**
     * 删除某场地的控制点
     *
     * @param areaId
     * @return
     */
    int deletePointByAreaId(Integer areaId);

    Point getPoint(@Param("pointId") Integer pointId,
                   @Param("pointName") String pointName);
}
