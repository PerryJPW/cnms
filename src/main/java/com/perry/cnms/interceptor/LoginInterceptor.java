package com.perry.cnms.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * admin 的拦截器
 *
 * @Author: PerryJ
 * @Date: 2020/2/7
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从session中获取信息
        Object identity = request.getSession().getAttribute("login");
        //先进行非空判断
        if (identity != null) {
            //再进行权限验证
            Boolean flag=(Boolean) identity;
            if (flag) {
                return true;
            }
        }
        //验证不通过时返回登录界面
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<script>");
        out.println("window.open ('" + request.getContextPath()
                + "/admin/login','_self')");
        out.println("</script>");
        out.println("</html>");

        return false;
    }
}
