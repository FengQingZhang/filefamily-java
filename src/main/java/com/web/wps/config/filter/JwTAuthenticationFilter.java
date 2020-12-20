package com.web.wps.config.filter;

import com.web.wps.logic.service.SysUserDetailServiceImpl;
import com.web.wps.util.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功后，走此类进行鉴权
 */
@Component
public class JwTAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private SysUserDetailServiceImpl sysUserDetailService;

    /**
     * 在过滤之前和之后执行的事件
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //从请求头部获取jwt令牌
        String tokenHeader=request.getHeader(jwtTokenUtil.getHeader());
        if(!StringUtils.isEmpty(tokenHeader)){
        //根据username加载权限
            String username=jwtTokenUtil.getUsernameFromToken(tokenHeader);

            if(username!=null&&SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails=sysUserDetailService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(tokenHeader,userDetails)){
//                   给使用该令牌的用户进行授权
                    UsernamePasswordAuthenticationToken authenticationToken
                            =new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        chain.doFilter(request, response);
    }

}
