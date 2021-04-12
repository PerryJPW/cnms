package com.perry.cnms.dao;

import com.perry.cnms.entity.Area;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/15
 */
public interface AreaDao {
    /**
     * 查询场地列表
     * @return
     */
    List<Area> queryArea();

    /**
     * 批量添加场地
     * @param areaList
     * @return
     */
    int batchInsertArea(List<Area> areaList);

    /**
     * 更新场地（图片,状态等）
     * @param area
     * @return
     */
    int updateArea(Area area);

    /**
     * 通过场地名称获取id（更新时使用）
     * @param name
     * @return
     */
    Integer getAreaIdByName(String name);

    /**
     * 通过id获取Area
     * @param areaId
     * @return
     */
    Area getAreaById(Integer areaId);

    /**
     * 删除
     * @param areaId
     * @return
     */
    int deleteArea(Integer areaId);

}
