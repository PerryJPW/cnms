package com.perry.cnms.dao;

import com.perry.cnms.BaseTest;
import com.perry.cnms.entity.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/22
 */
public class PointDaoTest extends BaseTest {
    @Autowired
    PointDao pointDao;

    @Test
    public void TestBatchInsertPoint(){
        List<Point> pointList=new ArrayList<>();
        Point point = new Point();
        point.setAreaId(1);
        point.setPointName("NA");
        point.setNextPoint("NB");
        point.setPointX(13.001);
        point.setPointY(13.002);
        point.setPointH(13.003);
        point.setDistance(13.001);
        point.setPointState(1);
        point.setUpdateTime(new Date());

        pointList.add(point);
        pointDao.batchInsertPoint(pointList);

    }
}
