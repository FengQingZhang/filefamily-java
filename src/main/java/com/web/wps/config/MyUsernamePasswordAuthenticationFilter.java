package com.web.wps.config;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 登录拦截器
 * @author cpms
 *
 */
public class MyUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


	//拦截url为"/login" 的Post请求
	public MyUsernamePasswordAuthenticationFilter(){
		super(new AntPathRequestMatcher("/login","POST"));
	}


	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		// 从json中获取username和password
		String body=StreamUtils.copyToString(request.getInputStream(),Charset.forName("UTF-8"));
		String username=null,password=null;
		if(StringUtils.hasText(body)){
			JSONObject jObject=JSON.parseObject(body);
			username=jObject.getString("username");
			password=jObject.getString("password");
		}
		if(username ==null){
			username="";
		}
		if(password==null){
			password="";
		}
		username=username.trim();
		//封装到token中提交
		UsernamePasswordAuthenticationToken aToken= new UsernamePasswordAuthenticationToken(username,password);
		return this.getAuthenticationManager().authenticate(aToken);
	}

}
