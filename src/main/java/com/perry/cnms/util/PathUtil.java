package com.perry.cnms.util;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Author: PerryJ
 * @Date: 2019/8/17
 */
public class PathUtil {
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); // 时间格式化的格式
    private static final Random random = new Random();
    public static String separator = System.getProperty("file.separator");

    /**
     * 获取本项目的所有资源的根目录
     *
     * @return 资源的根目录
     */
    public static String getFileBasePath() {


        String os = System.getProperty("os.name");
        String basePath = "";
        if (os.toLowerCase().startsWith("win")) {
            basePath = "D:/ControlNetwork/file/";
        } else {
            basePath = "/ControlNetwork/file/";
        }
        basePath = basePath.replace("/", separator);
        return basePath;
    }

    /**
     * @return 场地图片存储目录
     */
    public static String getAreaImagePath() {
        String imagePath = getFileBasePath() + "area";
        return imagePath.replace("/", separator);
    }

    /**
     * @return 教师文件存储目录
     */
    public static String getTeacherDataPath() {
        String teacherDataPath = getFileBasePath() + "teacher";

        return teacherDataPath.replace("/", separator);

    }

    /**
     * @return 学生图片数据的存储目录，使用年份区分
     */
    public static String getStudentPicturePath() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy"); // 时间格式化的格式
        String year=sDateFormat.format(new Date());
        String studentPath = getFileBasePath() + "student/"+year;
        return studentPath.replace("/", separator);
    }

    /**
     * 获取文件扩展名
     *
     * @param cFile Spring获取的文件流
     * @return 文件扩展名
     */
    public static String getFileExtension(CommonsMultipartFile cFile) {
        String originalFileName = cFile.getOriginalFilename();
        assert originalFileName != null;
        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    /**
     * 判断文件目录是否存在，并创建
     *
     * @param targetPath 要创建的目标目录
     */
    public static void makeDirPath(String targetPath) {

        File dirPath = new File(targetPath);
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
    }

    /**
     * 删除文件或目录
     *
     * @param storePath 要删除的目录
     */
    public static void deleteFileOrPath(String storePath) {
        File fileOrPath = new File(storePath);
        if (fileOrPath.exists()) {
            if (fileOrPath.isDirectory()) {
                for (File file : fileOrPath.listFiles()) {
                    file.delete();
                }
            }
            fileOrPath.delete();
        }
    }

    /**
     * 获取图片的名称（场地名），并返回大写
     * 最长三个字母,大于三返回error
     *
     * @param imageFile Spring获取的文件流
     * @return 提取的名称（大写）
     */
    public static String getImgName(CommonsMultipartFile imageFile) {
        String originalFileName = imageFile.getOriginalFilename();
        int index1 = originalFileName.lastIndexOf(separator);
        int index2 = originalFileName.lastIndexOf(".");
        String name = originalFileName.substring(index1 + 1, index2).toUpperCase();
        if (name.length() > 3) {
            return "error";
        }
        return name;
    }

    /**
     * 返回文件的名称（文件流可能包含路径）
     *
     * @param file Spring文件流
     * @return 文件的名称，去前缀和后缀
     */
    public static String getRealFileName(CommonsMultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        int index1 = originalFileName.lastIndexOf(separator);
        int index2 = originalFileName.lastIndexOf(".");
        return originalFileName.substring(index1 + 1, index2);
    }

    /**
     * @param groupId 小组Id
     * @return 小组文件名，暂定为id加后缀img
     */
    public static String getStudentFileName(Integer groupId) {
        return "P1-gID-"+groupId ;
    }

    /**
     * @return 随机文件名 日期加随机数
     */
    public static String getRandomFileName() {
        // 生成随机文件名：当前年月日时分秒+五位随机数（为了在实际项目中防止文件同名而进行的处理）
        int ranNum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000; // 获取随机数
        String nowTimeStr = sDateFormat.format(new Date()); // 当前时间
        return nowTimeStr + ranNum;
    }

}
