package com.perry.cnms.dao;

import com.perry.cnms.entity.AreaUsing;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/2/1
 */
public interface AreaUseDao {
    List<AreaUsing> queryAreaUse(@Param("teacherId") Integer teacherId,
                                 @Param("areaId") Integer areaId,
                                 @Param("groupId") Integer groupId,
                                 @Param("areaUseId") Integer areaUseId);

    int batchInsertAreaUsing(List<AreaUsing> areaUsingList);

    int updateAreaUse(List<AreaUsing> areaUsingList);

    int deleteAreaUse(List<Integer> areaUseId);
}
