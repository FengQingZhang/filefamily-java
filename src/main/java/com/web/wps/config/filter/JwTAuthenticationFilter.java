package com.web.wps.config.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.web.wps.logic.entity.SysUserDetail;
import com.web.wps.util.jwt.JwtTokenUtil;

/**
 * 验证用户名密码正确后，生成一个token并返回给客服端
 * @author cpms
 *
 */
public class JwTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
    public JwTAuthenticationFilter(AuthenticationManager authenticationManager) {
		// TODO Auto-generated constructor stub
    	this.authenticationManager=authenticationManager;
	}

    /**
     * 验证操作 接收并解析用户凭证
     */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		//从输入流中获取到登录的信息
		// 创建一个token并调用authenticationManager.authenticate() 让Spring security进行验证
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getAttribute("username"),request.getAttribute("password")));
	}

	/**
	 * 若验证成功 生成token并返回
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// TODO Auto-generated method stub
		SysUserDetail userDetails=(SysUserDetail)authResult.getPrincipal();
		Collection<? extends GrantedAuthority> authorities=userDetails.getAuthorities();
		//创建Token
		String token=jwtTokenUtil.generateToken(userDetails,authorities.toString());
		//设置编码 防止乱码问题
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");
		//在请求头里返回创建成功的token
		//设置请求头为带有"JWTHeaderName" 
		response.setHeader(jwtTokenUtil.getHeader(),token);
		//处理编码方式  防止中文乱码
		response.setContentType("text/json;charset=uft-8");
		//将反馈塞到HttpServletResponse中返回给前台
		response.getWriter().write(JSON.toJSONString("登录成功"));
		
		
	}

	/**
	 *  验证失败调用方法
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String returnData="";
		//账号过期
		if (failed instanceof AccountExpiredException) {
			returnData="账号过期";
		}else if(failed instanceof BadCredentialsException){
			returnData="密码错误";
		}else if(failed instanceof CredentialsExpiredException){
			returnData="密码过期";
		}else if(failed instanceof DisabledException){
			returnData="账号不可用";
		}else if(failed instanceof LockedException){
			returnData="账号锁定";
		}else if(failed instanceof InternalAuthenticationServiceException){
			returnData="用户不存在";
		}else{
			returnData="未知异常";
		}
		
		//处理编码方式，防止中文乱码
		response.setContentType("text/json;charset=utf-8");
		//将反馈塞到HttpServletResponse中返回给前台
		response.getWriter().write(JSON.toJSONString(returnData));
	}
	
}
