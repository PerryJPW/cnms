package com.perry.cnms.util;

import com.perry.cnms.entity.Point;
import com.perry.cnms.entity.Teacher;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 利用 POI 来进行Excel文件的读取（写入）
 *
 * @Author: PerryJ
 * @Date: 2020/1/20
 */
@Component
public class ExcelUtil {

    /**
     * 读取固定形式的Excel中的控制点数据
     *
     * @param inputStream   excel文件流
     * @param fileExtension excel后缀名 .xls  .xlsx
     * @return 控制点列表
     */
    public static List<Point> getPointsFromExcel(InputStream inputStream, String fileExtension) {

        //根据类型获取不同的workbook
        Workbook workbook = null;
        try {
            System.out.println("获取Point表");
            if (".xls".equals(fileExtension)) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (".xlsx".equals(fileExtension)) {
                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (workbook == null) {
            return null;
        }
        List<Point> pointList = new ArrayList<>();
        //逐sheet读取
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int i = sheet.getLastRowNum();
            //逐行读取
            for (int rowIndex = 0; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                if (rowIndex % 2 == 1 || rowIndex == 0 || rowIndex == sheet.getLastRowNum() - 1) {
                    continue;
                }
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                try {
                    //读取一行中某列的内容
                    String pointName = row.getCell(0).getStringCellValue().toUpperCase();

                    Double pointX = CalculateUtil.get3Decimal(row.getCell(4).getNumericCellValue());
                    Double pointY = CalculateUtil.get3Decimal(row.getCell(5).getNumericCellValue());
                    Double pointH = CalculateUtil.get3Decimal(row.getCell(6).getNumericCellValue());
                    Double distance = CalculateUtil.get3Decimal(sheet.getRow(rowIndex + 1).getCell(3).getNumericCellValue());
                    String nextPoint = sheet.getRow(rowIndex + 2).getCell(0).getStringCellValue();

                    //可能会读到没有数据的空行，要进行剔除
                    if (pointX == 0.0 || pointY == 0.0 || pointH == 0.0 || distance == 0.0) {
                        continue;
                    }
                    Point point = new Point();
                    point.setPointName(pointName);
                    point.setNextPoint(nextPoint);
                    point.setPointX(pointX);
                    point.setPointY(pointY);
                    point.setPointH(pointH);
                    point.setDistance(distance);
                    point.setPointState(1);
                    point.setUpdateTime(new Date());


                    pointList.add(point);
                } catch (NullPointerException npe) {
                    continue;
                }


            }
        }
        return pointList;
    }

    /**
     * 获取固定格式的教师信息列表
     *
     * @param inputStream   excel流
     * @param fileExtension 文件扩展名
     * @return 教师列表
     */
    public static List<Teacher> getTeacherNameFromExcel(InputStream inputStream, String fileExtension) {
        //根据类型获取不同的workbook
        Workbook workbook = null;
        try {
            System.out.println("获取Teacher表");
            if (".xls".equals(fileExtension)) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (".xlsx".equals(fileExtension)) {
                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (workbook == null) {
            return null;
        }
        List<Teacher> teacherList = new ArrayList<>();
        //读取第一个sheet
        Sheet sheet = workbook.getSheetAt(0);
        int i = sheet.getLastRowNum();
        //逐行读取
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            try {
                //读取一行中某列的内容
                String teacherName = row.getCell(0).getStringCellValue();
                String teacherAccount = null;
                String password = null;
                Integer teacherState = null;
                if (null == row.getCell(1) || "".equals(row.getCell(1).getStringCellValue())) {
                    teacherAccount = PinYinUtil.getPinYin(teacherName);
                } else {
                    teacherAccount = row.getCell(1).getStringCellValue();
                }
                if (null == row.getCell(2) || "".equals(row.getCell(2).getStringCellValue())) {
                    password = teacherAccount;
                } else {
                    password = row.getCell(2).getStringCellValue();
                }
                if (null == row.getCell(3)) {
                    teacherState = 1;
                } else {
                    teacherState = (int) row.getCell(3).getNumericCellValue();
                }

                Teacher teacher = new Teacher();
                teacher.setTeacherName(teacherName);
                teacher.setTeacherAccount(teacherAccount);
                teacher.setPassword(password);
                teacher.setTeacherState(teacherState);
                teacher.setUpdateTime(new Date());
                teacherList.add(teacher);

            } catch (NullPointerException npe) {
                continue;
            }


        }

        return teacherList;
    }

}
