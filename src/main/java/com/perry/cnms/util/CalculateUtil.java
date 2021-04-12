package com.perry.cnms.util;

import com.perry.cnms.entity.Point;

/**
 * @Author: PerryJ
 * @Date: 2020/1/25
 */
public class CalculateUtil {
    private static double PI = 3.141592653579893;


    /**
     * 通过坐标差来获取方位角
     *
     * @param DX x2-x1
     * @param DY y2-y1
     * @return 方位角 （弧度）
     */
    public static double calculateAlpha(double DX, double DY) {

        double R, T, AL;

        if (Math.abs(DY) < 0.0000000000001) {
            T = 1.0;
        } else {
            T = DY / Math.abs(DY);
        }
        if (Math.abs(DX) < 0.00000000001) {
            R = T * Math.PI / 2.0;
        } else {
            R = Math.atan(DY / DX);
        }

        if (DX >= 0.0) {
            if (DY >= 0.0) {
                AL = R;
            } else {
                AL = R + 2.0 * Math.PI;
            }
        } else {
            AL = R + Math.PI;
        }
        return AL;
    }

    /**
     * 将弧度化为度分秒形式-- 180.0101=180°01'01"
     *
     * @param radian 弧度参数
     * @return 度分秒
     */
    public static double radianToDoubleDegree(double radian) {
        double T, T1, T2, T3, T4, T5;
        if (Math.abs(radian) < 0.000000000000000000001) {
            T = 1.0;
        } else {
            T = radian / Math.abs(radian);
        }
        T1 = Math.abs(radian) * 180.0 / Math.PI;
        T2 = Math.floor(T1 + 0.0000001);
        T3 = (T1 - T2) * 60.0;
        T4 = Math.floor(T3 + 0.0000001);
        T5 = T2 + T4 / 100.0 + (T3 - T4) * 0.006;
        T1 = T5 * T;
        return T1;
    }

    /**
     * 将double类型的数据保留三位小数
     *
     * @param inNum 输入的double型数据
     * @return 三位小数的数据
     */
    public static Double get3Decimal(double inNum) {
        return (double) Math.round(inNum * 1000) / 1000;
    }


    public static Double calDistance(Point p1, Point p2) {
        double tmpX = Math.pow((p1.getPointX() - p2.getPointX()), 2);
        double tmpY = Math.pow((p1.getPointY() - p2.getPointY()), 2);
        double distance = Math.sqrt(tmpX+tmpY);
        return CalculateUtil.get3Decimal(distance);
    }
}
