package com.perry.cnms.util;

import com.google.code.kaptcha.Constants;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: PerryJ
 * @Date: 2019/8/24
 */
public class VerifyCodeUtil {
    /**
     * 校验输入的验证码是否正确
     *
     * @param request http请求
     * @return 是否通过校验
     */
    public static boolean checkVerifyCode(HttpServletRequest request) {
        //图片中的验证码
        String verifyCodeExpected = (String) request.getSession()
                .getAttribute(Constants.KAPTCHA_SESSION_KEY);
        verifyCodeExpected = verifyCodeExpected.toLowerCase();
        //用户输入的验证码
        String verifyCodeActual = HttpServletRequestUtil.getString(request, "verifyCodeActual");

        assert verifyCodeActual != null;
        verifyCodeActual = verifyCodeActual.toLowerCase();
        return verifyCodeActual.equals(verifyCodeExpected);
    }
}
