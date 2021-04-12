package com.perry.cnms.service;

import com.perry.cnms.dto.AreaUseExecution;
import com.perry.cnms.entity.AreaUsing;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/2/1
 */
public interface AreaUseService {
    AreaUseExecution getAreaUsingByTeacherId(Integer teacherId);

    AreaUseExecution getAreaUsingByAreaId(Integer areaId);

    AreaUseExecution getAreaUsingByAreaUseId(Integer areaUseId);

    /**
     * 只返回最近的，有效的第一个数据
     *
     * @param groupId
     * @return
     */
    AreaUseExecution getAreaUsingByGroupId(Integer groupId);

    AreaUseExecution getAreaUsingList();

    AreaUseExecution addAreaUsingList(List<AreaUsing> areaUsingList);

    AreaUseExecution updateAreaUse(List<AreaUsing> areaUsingList);
    AreaUseExecution deleteArea(List<Integer> areaUseIdList);
}
