package com.perry.cnms.util;

import com.perry.cnms.BaseTest;
import com.perry.cnms.entity.Point;
import com.perry.cnms.entity.Teacher;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/22
 */

public class ExcelUtilTest extends BaseTest {
    @Test
    public void testExcelUtil() {
        try {
            InputStream inputStream = new FileInputStream("C:\\Code\\NewJavaCode\\ControlNetwork\\OriginData\\CNpoint.xlsx");
            String fileName = ".xlsx";
            List<Point> pointList = ExcelUtil.getPointsFromExcel(inputStream, fileName);
            for (Point point : pointList) {
                System.out.println(point);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTeacherExcel(){
        try {
            InputStream inputStream=new FileInputStream("C:\\Code\\NewJavaCode\\ControlNetwork\\OriginData\\demo.xlsx");
            String fileName = ".xlsx";
            List<Teacher> teacherList=ExcelUtil.getTeacherNameFromExcel(inputStream,fileName);
            for (Teacher teacher :
                    teacherList) {
                System.out.println(teacher);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
