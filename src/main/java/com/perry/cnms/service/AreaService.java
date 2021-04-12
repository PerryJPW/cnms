package com.perry.cnms.service;

import com.perry.cnms.dto.AreaExecution;
import com.perry.cnms.entity.Area;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/15
 */
public interface AreaService {
    /**
     * 获取场地列表
     *
     * @return
     */
    AreaExecution getAreaList();


    /**
     * 批量添加场地
     *
     * @param areaList
     * @return
     */
    AreaExecution addAreaList(List<Area> areaList);

    /**
     * 获取id
     *
     * @param areaName
     * @return
     */
    AreaExecution getIdByName(String areaName);

    /**
     * 更新
     *
     * @param area
     * @return
     */
    AreaExecution updateArea(Area area);

    /**
     * 通过id获取Area
     *
     * @param areaId
     * @return
     */
    AreaExecution getAreaById(Integer areaId);

    /**
     * 删除
     *
     * @param areaId
     * @return
     */
    AreaExecution deleteArea(Integer areaId);

}
