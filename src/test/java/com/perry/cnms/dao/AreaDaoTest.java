package com.perry.cnms.dao;

import com.perry.cnms.BaseTest;
import com.perry.cnms.entity.Area;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * @Author: PerryJ
 * @Date: 2020/1/15
 */
public class AreaDaoTest extends BaseTest {
    @Autowired
    private AreaDao areaDao;

    @Test
    public void testQueryArea(){
        List<Area> areaList=areaDao.queryArea();
        assertEquals(3,areaList.size());
    }

    @Test
    public void testBatchInsertArea(){
        Area area1=new Area();
        area1.setAreaName("AA");
        area1.setAreaPictureAddress("c://ss/ss.jpg");
        area1.setAreaState(1);
//        area1.setAlpha(180.0110);
        area1.setUpdateTime(new Date());
        List<Area> areaList=new ArrayList<>();
        areaList.add(area1);
        int re=areaDao.batchInsertArea(areaList);
        System.out.println(re);
    }

    @Test
    public void testUpdateArea(){
        Area area1=new Area();
        area1.setAreaId(1);
        area1.setAreaName("AA");
        area1.setAreaState(2);
        area1.setUpdateTime(new Date());
        int re=areaDao.updateArea(area1);

        System.out.println(re);
    }

    @Test
    public void testGetAreaById(){
        Area area=areaDao.getAreaById(1);
        System.out.println(area.getAreaId());
        System.out.println(area.getAreaName());
    }
}
