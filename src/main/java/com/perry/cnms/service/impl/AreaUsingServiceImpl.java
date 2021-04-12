package com.perry.cnms.service.impl;

import com.perry.cnms.dao.AreaUseDao;
import com.perry.cnms.dto.AreaUseExecution;
import com.perry.cnms.entity.AreaUsing;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.exceptions.AreaUsingOperateException;
import com.perry.cnms.service.AreaUseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/2/1
 */
@Service
public class AreaUsingServiceImpl implements AreaUseService {
    private Logger log = LoggerFactory.getLogger(AreaUsingServiceImpl.class);

    @Autowired
    private AreaUseDao areaUseDao;

    @Override
    public AreaUseExecution getAreaUsingByTeacherId(Integer teacherId) {

        List<AreaUsing> areaUsingList = areaUseDao.queryAreaUse(teacherId, null, null,null);
//        log.info("查询areaUsing(teacherId)，共获取" + areaUsingList.size() + "个");
        if (areaUsingList.size() == 0 || areaUsingList == null) {
            return new AreaUseExecution(StateEnum.EMPTY_RETURN);
        } else {
            return new AreaUseExecution(StateEnum.SUCCESS, areaUsingList);
        }
    }

    @Override
    public AreaUseExecution getAreaUsingByAreaId(Integer areaId) {
        List<AreaUsing> areaUsingList = areaUseDao.queryAreaUse(null, areaId, null,null);
//        log.info("查询areaUsing(areaId)，共获取" + areaUsingList.size() + "个");
        if (areaUsingList.size() == 0 || areaUsingList == null) {
            return new AreaUseExecution(StateEnum.EMPTY_RETURN);
        } else {
            return new AreaUseExecution(StateEnum.SUCCESS, areaUsingList);
        }
    }

    @Override
    public AreaUseExecution getAreaUsingByAreaUseId(Integer areaUseId) {
        List<AreaUsing> areaUsingList = areaUseDao.queryAreaUse(null, null, null,areaUseId);
//        log.info("查询areaUsing(areaId)，共获取" + areaUsingList.size() + "个");
        if (areaUsingList.size() == 0 || areaUsingList == null) {
            return new AreaUseExecution(StateEnum.EMPTY_RETURN);
        } else {
            return new AreaUseExecution(StateEnum.SUCCESS, areaUsingList.get(0));
        }
    }

    @Override
    public AreaUseExecution getAreaUsingByGroupId(Integer groupId) {
        List<AreaUsing> areaUsingList = areaUseDao.queryAreaUse(null, null, groupId,null);
//        log.info("查询areaUsing(groupId)，共获取" + areaUsingList.size() + "个");
        if (areaUsingList.size() == 0 || areaUsingList == null) {
            return new AreaUseExecution(StateEnum.EMPTY_RETURN);
        } else {
            return new AreaUseExecution(StateEnum.SUCCESS, areaUsingList.get(0));
        }
    }

    @Override
    public AreaUseExecution getAreaUsingList() {
        List<AreaUsing> areaUsingList = areaUseDao.queryAreaUse(null, null, null,null);
        return new AreaUseExecution(StateEnum.SUCCESS, areaUsingList);
    }

    @Override
    public AreaUseExecution addAreaUsingList(List<AreaUsing> areaUsingList) throws AreaUsingOperateException {
        if (areaUsingList != null && areaUsingList.size() > 0) {
            try {
                int effectNum = areaUseDao.batchInsertAreaUsing(areaUsingList);
//                log.info("areaUsing插入" + effectNum + "条数据");
                if (effectNum <= 0) {
                    throw new AreaUsingOperateException("areaUsing创建失败");
                } else {
                    return new AreaUseExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new AreaUsingOperateException("area批量添加失败" + e.getMessage());
            }

        } else {
            return new AreaUseExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public AreaUseExecution updateAreaUse(List<AreaUsing> areaUsingList) throws AreaUsingOperateException {
        if (areaUsingList != null && areaUsingList.size() > 0) {
            try {
                int effectNum = areaUseDao.updateAreaUse(areaUsingList);
//                log.info("Group更新" + effectNum + "条,ID=" + group.getGroupId());
                if (effectNum <= 0) {
                    throw new AreaUsingOperateException("AreaUsing更新失败");
                } else {
                    return new AreaUseExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new AreaUsingOperateException("AreaUsing更新失败" + e.getMessage());
            }
        } else {
            return new AreaUseExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public AreaUseExecution deleteArea(List<Integer> areaUseIdList) throws AreaUsingOperateException{
        if (areaUseIdList != null && areaUseIdList.size() > 0) {
            int effectNum=areaUseDao.deleteAreaUse(areaUseIdList);
            if (effectNum>0){
                return new AreaUseExecution(StateEnum.SUCCESS);
            }else {
                log.error("AreaUse删除失败");
                throw new AreaUsingOperateException("删除失败");
            }
        }else {
            return new AreaUseExecution(StateEnum.NULL_AREA_ID);
        }
    }
}
