package com.perry.cnms.web.login;

import com.perry.cnms.dto.StudentExecution;
import com.perry.cnms.dto.TeacherExecution;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.service.StudentService;
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
@RequestMapping(value = "/student")
public class StudentLoginController {

    @Autowired
    private StudentService studentService;

    private Logger log = LoggerFactory.getLogger(StudentLoginController.class);
    @RequestMapping(value = "/check-login", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> checkStudentLogin(HttpServletRequest request) {
        String ipAddress=request.getRemoteAddr();
        log.info("[Login]-StudentTry IP:"+ipAddress);
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
            StudentExecution studentExecution=studentService.getGroupByGroupAccount(account);
            if (studentExecution.getState() == StateEnum.EMPTY_RETURN.getState()) {
                modelMap.put("success", false);
                modelMap.put("errMsg", "账户错误");
            } else if (studentExecution.getState() == StateEnum.SUCCESS.getState()) {
                if (password.equals(studentExecution.getGroup().getPassword())) {
                    request.getSession().setAttribute("identity", "student");
                    request.getSession().setAttribute("login", true);
                    request.getSession().setAttribute("groupId",studentExecution.getGroup().getGroupId());
                    request.getSession().setAttribute("groupState",studentExecution.getGroup().getGroupState());
//                    request.getSession().setAttribute("name",studentExecution.getGroup().getMajorCode());
                    String groupName=studentExecution.getGroup().getMajorCode()+"组"+studentExecution.getGroup().getGroupCode();
                    modelMap.put("name",groupName);
                    modelMap.put("success", true);
                    log.info("[Login]-Student登录-ID"+studentExecution.getGroup().getGroupId());

                }else {
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
