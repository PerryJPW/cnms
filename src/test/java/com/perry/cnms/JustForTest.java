package com.perry.cnms;

import com.perry.cnms.util.PinYinUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: PerryJ
 * @Date: 2020/1/28
 */
public class JustForTest extends BaseTest {
    @Value("${jdbc.driver}")
    String driver;

    @Test
    public void testPinyin() {
        String s = "你好啊";
        System.out.println(PinYinUtil.getPinYin(s));
        System.out.println(PinYinUtil.getPinYinHeadChar(s));
    }

    @Test
    public void testException() {
        try {
            System.out.println("Yes");
            throw new RuntimeException("Error");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    String accept;
    @Test
    public void testProperty() {
        System.out.println(driver);
//        System.out.println(accept);

    }

    @Test
    public void testDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy"); // 时间格式化的格式
        String date=sDateFormat.format(new Date());
        System.out.println(date);
    }
}

