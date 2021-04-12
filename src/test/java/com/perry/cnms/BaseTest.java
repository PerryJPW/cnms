package com.perry.cnms;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 配置Spring和junit整合，Junit启动时加载SpringIOC容器
 * @Author: PerryJ
 * @Date: 2019/8/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit，Spring配置文件位置
 @ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class BaseTest {
}
