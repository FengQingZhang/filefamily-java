package com.web.wps.logic.controller;

import com.web.wps.base.BaseController;
import com.web.wps.logic.service.SysUserDetailServiceImpl;
import com.web.wps.util.jwt.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zqf
 * 用户实现获取wps可预览URL
 */
@RestController
@RequestMapping("/sys")
public class SysController extends BaseController {

    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private SysUserDetailServiceImpl sysUserDetailService;

    @PostMapping("/checkLogin")
    public void checkLogin(HttpServletRequest request, HttpServletResponse response){

        String tokenHeader = request.getHeader(jwtTokenUtil.getHeader());
        if(!StringUtils.isEmpty(tokenHeader)){
            //根据username加载权限
            String username=jwtTokenUtil.getUsernameFromToken(tokenHeader);

            if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails=sysUserDetailService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(tokenHeader,userDetails)){
//                   给使用该令牌的用户进行授权

                }
            }
        }else {

        }
    }
}
