package com.perry.cnms.util;

import com.perry.cnms.BaseTest;
import org.junit.Test;

/**
 * @Author: PerryJ
 * @Date: 2020/2/14
 */
public class PasswordUtilTest extends BaseTest {
    @Test
    public void  testRandom(){
        System.out.println(PasswordUtil.getRandomPassword(7));
    }
}
