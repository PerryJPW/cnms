package com.perry.cnms.web.login;

import com.perry.cnms.dto.TeacherExecution;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.service.TeacherService;
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
@RequestMapping(value = "/teacher")
public class TeacherLoginController {

    @Autowired
    private TeacherService teacherService;
    private Logger log = LoggerFactory.getLogger(TeacherLoginController.class);

    @RequestMapping(value = "/check-login", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> checkTeacherLogin(HttpServletRequest request) {
        String ipAddress=request.getRemoteAddr();

        log.info("[Login]-TeacherTry IP:"+ipAddress);

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("success", false);

        if (!VerifyCodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "错误的验证码");
            return modelMap;
        }
        String account = HttpServletRequestUtil.getString(request, "account");
        String password = HttpServletRequestUtil.getString(request, "password");
        if (password == null || account == null) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "账户密码不能为空");
        } else {
            TeacherExecution teacherExecution = teacherService.getTeacherByTeacherAccount(account);
            if (teacherExecution.getState() == StateEnum.EMPTY_RETURN.getState()) {
                modelMap.put("success", false);
                modelMap.put("errMsg", "账户错误");
            } else if (teacherExecution.getState() == StateEnum.SUCCESS.getState()) {
                if (password.equals(teacherExecution.getTeacher().getPassword())) {
                    request.getSession().setAttribute("identity", "teacher");
                    request.getSession().setAttribute("login", true);
                    request.getSession().setAttribute("teacherId", teacherExecution.getTeacher().getTeacherId());

                    modelMap.put("name", teacherExecution.getTeacher().getTeacherName());
                    modelMap.put("success", true);
                    log.info("[Login]-Teacher登录-ID" + teacherExecution.getTeacher().getTeacherId());

                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "密码错误");
                }
            } else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "登陆失败");
            }
        }
        return modelMap;
    }
}
