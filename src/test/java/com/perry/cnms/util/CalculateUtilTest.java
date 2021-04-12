package com.perry.cnms.util;

import com.perry.cnms.BaseTest;
import org.junit.Test;

/**
 * @Author: PerryJ
 * @Date: 2020/1/25
 */
public class CalculateUtilTest extends BaseTest {
    @Test
    public void testCalAlpha() {
//        double x1=132806.921;
//        double x2=132902.540;
//        double y1=130136.885;
//        double y2=130137.321;
        double x2 = 132895.807;
        double x1 = 132902.540;
        double y2 = 130039.747;
        double y1 = 130137.321;
        double ra = CalculateUtil.calculateAlpha(x2 - x1, y2 - y1);
        System.out.println(CalculateUtil.radianToDoubleDegree(ra));

    }
}
