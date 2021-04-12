package com.perry.cnms.web.login;

import com.perry.cnms.service.SettingsService;
import com.perry.cnms.util.HttpServletRequestUtil;
import com.perry.cnms.util.VerifyCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: PerryJ
 * @Date: 2020/2/7
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminLoginController {
    @Autowired
    private SettingsService settingsService;
    private Logger log = LoggerFactory.getLogger(AdminLoginController.class);

    @RequestMapping(value = "/check-login", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> checkAdminLogin(HttpServletRequest request) {
        String ipAddress=request.getRemoteAddr();
        log.info("[Login]-AdminTry IP:"+ipAddress);
        Map<String, Object> modelMap = new HashMap<>();
        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        String password = HttpServletRequestUtil.getString(request, "password");
        if (password == null) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "密码不能为空");
        } else {
            //从数据库中获取正确的管理员密码
            String adminPassword = settingsService.getAdminPassword();
            if (adminPassword == null) {
                //长时间无连接会使数据库连接池资源释放，再次访问时要先建立连接，所以可能会有null的情况
                modelMap.put("success", false);
                modelMap.put("errMsg", "系统内部错误，请重试！");
                return modelMap;
            }
            if (adminPassword.equals(password)) {
                request.getSession().setAttribute("identity", "admin");
                request.getSession().setAttribute("login", true);
                modelMap.put("success", true);
                log.info("[Login]-Admin登录");

            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "管理员密码错误");
            }
        }
        return modelMap;
    }
}
