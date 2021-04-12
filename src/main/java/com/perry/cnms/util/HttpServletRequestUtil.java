package com.perry.cnms.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取request中的参数
 *
 * @Author: PerryJ
 * @Date: 2019/8/19
 */
public class HttpServletRequestUtil {
    public static Integer getInt(HttpServletRequest request, String key) {
        try {
            return Integer.decode(request.getParameter(key));
        } catch (Exception e) {
            return -1;
        }
    }

    public static Long getLong(HttpServletRequest request, String key) {
        try {
            return Long.valueOf(request.getParameter(key));
        } catch (Exception e) {
            return -1L;
        }
    }

    public static Double getDouble(HttpServletRequest request, String key) {
        try {
            return Double.valueOf(request.getParameter(key));
        } catch (Exception e) {
            return -1d;
        }
    }

    public static Boolean getBoolean(HttpServletRequest request, String key) {
        try {
            return Boolean.valueOf(request.getParameter(key));
        } catch (Exception e) {
            return false;
        }
    }

    public static String getString(HttpServletRequest request, String key) {
        try {
            String result = request.getParameter(key);
            if (result != null) {
                result = result.trim();//去两侧空格
            }
            if ("".equals(result)) {
                result = null;
            }
            return result;

        } catch (Exception e) {
            return null;
        }
    }
}
