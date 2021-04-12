package com.perry.cnms.service;

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
public class AreaServiceTest extends BaseTest {
    @Autowired
    private AreaService areaService;
    @Test
    public void testGetAreaList(){
//        List<Area> areaList=areaService.getAreaUsingList();
//        assertEquals("A",areaList.get(0).getAreaName());
    }

    @Test
    public void testBatchAddArea(){
        Area area1=new Area();
        area1.setAreaName("Aa");
        area1.setAreaPictureAddress("c://ss/ss.jpg");
        area1.setAreaState(1);
//        area1.setAlpha(180.0000);
        area1.setUpdateTime(new Date());
        List<Area> areaList=new ArrayList<>();
        areaList.add(area1);
        areaService.addAreaList(areaList);
    }
}
