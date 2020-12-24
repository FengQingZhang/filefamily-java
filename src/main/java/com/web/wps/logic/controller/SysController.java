package com.web.wps.logic.controller;

import com.alibaba.fastjson.JSONObject;
import com.web.wps.base.BaseController;
import com.web.wps.logic.service.SysUserDetailServiceImpl;
import com.web.wps.util.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zqf
 * 用户实现获取wps可预览URL
 */
@Controller
public class SysController extends BaseController {

    @Autowired
    private SysUserDetailServiceImpl sysUserDetailService;

    @ResponseBody
    @PostMapping("/checkLogin")
    public JSONObject checkLogin(HttpServletRequest request, HttpServletResponse response){
        return  sysUserDetailService.checkLogin(request,response);
    }
}
