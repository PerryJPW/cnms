package com.perry.cnms.dao;

import com.perry.cnms.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: PerryJ
 * @Date: 2020/2/1
 */
public class TestAreaUseDao extends BaseTest {
    @Autowired
    private AreaUseDao areaUseDao;

    @Test
    public void testQueryAreaUse(){
//        System.out.println(areaUseDao.queryAreaUse(1,null,null));
        System.out.println(areaUseDao.queryAreaUse(null,null,1,null));
    }
}
