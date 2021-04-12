package com.perry.cnms.service.impl;

import com.perry.cnms.dao.AreaDao;
import com.perry.cnms.dto.AreaExecution;
import com.perry.cnms.entity.Area;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.exceptions.AreaOperateException;
import com.perry.cnms.service.AreaService;
import com.perry.cnms.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/15
 */
@Service
public class AreaServiceImpl implements AreaService {

    Logger log = LoggerFactory.getLogger(AreaServiceImpl.class);
    @Autowired
    private AreaDao areaDao;

    @Override
    public AreaExecution getAreaList() {
        List<Area> areaList = areaDao.queryArea();
//        log.info("查询area，共获取" + areaList.size() + "个");
        if (areaList.size()==0||areaList == null) {
            return new AreaExecution(StateEnum.EMPTY_RETURN);
        } else {
            return new AreaExecution(StateEnum.SUCCESS, areaList);
        }
    }

    @Override
    public AreaExecution addAreaList(List<Area> areaList) throws AreaOperateException {
        if (areaList != null && areaList.size() > 0) {
            try {
                int effectNum = areaDao.batchInsertArea(areaList);
//                log.info("area插入" + effectNum + "条数据");
                if (effectNum <= 0) {
                    throw new AreaOperateException("area创建失败");
                } else {
                    return new AreaExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new AreaOperateException("area批量添加失败" + e.getMessage());
            }

        } else {
            return new AreaExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public AreaExecution getIdByName(String areaName) {
        Integer id = areaDao.getAreaIdByName(areaName);
//        log.info("name=a" + areaName + "id=" + id);
        if (id == null) {
            return new AreaExecution(StateEnum.EMPTY_RETURN);
        } else {
            return new AreaExecution(StateEnum.SUCCESS, id);
        }
    }

    @Override
    public AreaExecution updateArea(Area area) throws AreaOperateException{
        if (area != null) {
            try {
                int effectNum = areaDao.updateArea(area);
//                log.info("area更新" + effectNum + "条,ID=" + area.getAreaId());
                if (effectNum <= 0) {
                    throw new AreaOperateException("area更新失败");
                } else {
                    return new AreaExecution(StateEnum.SUCCESS, area.getAreaId());
                }
            } catch (Exception e) {
                throw new AreaOperateException("area更新失败" + e.getMessage());
            }

        } else {
            return new AreaExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public AreaExecution getAreaById(Integer areaId) {
        Area area = areaDao.getAreaById(areaId);
//        log.info("getAreaUsing id=" + areaId);
        if (area != null && areaId > 0) {
            return new AreaExecution(StateEnum.SUCCESS, area);
        } else {
            return new AreaExecution(StateEnum.EMPTY_RETURN);
        }
    }

    @Override
    public AreaExecution deleteArea(Integer areaId)  throws AreaOperateException{
        if (areaId != null && areaId > 0) {
            String deleteAreaPath= areaDao.getAreaById(areaId).getAreaPictureAddress();
            int effectNum=areaDao.deleteArea(areaId);
            if (effectNum>0){
                PathUtil.deleteFileOrPath(deleteAreaPath);
                return new AreaExecution(StateEnum.SUCCESS);
            }else {
                return new AreaExecution(StateEnum.EMPTY_RETURN);
            }
        }else {
            return new AreaExecution(StateEnum.NULL_AREA_ID);
        }
    }
}
