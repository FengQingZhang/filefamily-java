package com.web.wps.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.wps.config.filter.CustomAuthenticatopnFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.web.wps.config.filter.JwTAuthenticationFilter;
import com.web.wps.logic.entity.SysUserDetail;
import com.web.wps.logic.service.SysUserDetailServiceImpl;
import com.web.wps.util.jwt.JwtTokenUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
public class SysSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginAuthProvider loginAuthProvider;
    @Autowired
    private SysUserDetailServiceImpl userDetailService;
    
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // TODO Auto-generated method stub
        auth.authenticationProvider(loginAuthProvider);
       //auth.userDetailsService(userDetailService);
    }

    //jwtAuthenticationProvider
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();//跨域设置
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/image/**").permitAll()//静态资源访问无需认证
                .antMatchers("/admdin/*").hasAnyRole("ROLE_ADMIN")//admin开头的请求需要admin权限
                .anyRequest().authenticated()//默认其他请求都需要认证
                .and()
                .formLogin()
                .successHandler(new SuccessHandler())
                .failureHandler(new FailureHandler())
                ;
                /*.and()
                .addFilter(new JwTAuthenticationFilter(authenticationManager()))*/;
                http.addFilterAfter(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                /*.addFilter(new JwTAuthenticationFilter(authenticationManager()))*/;
        ;
    }

    /**
     * 跨域配置
     * @return 基于URL的跨域配置信息
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 注册跨域配置
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

	/**
	 * 在
	 * @return
	 * @throws Exception
	 */
	@Bean
    CustomAuthenticatopnFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticatopnFilter filter = new CustomAuthenticatopnFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    
    class SuccessHandler implements AuthenticationSuccessHandler {
    	@Override
    	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    			org.springframework.security.core.Authentication authentication) throws IOException, ServletException {
    	
    			Map<String,Object> map =new HashMap<String, Object>();
    			map.put("code", "200");
    			map.put("msg","ok");
    			SysUserDetail userDetails=(SysUserDetail)authentication.getPrincipal();
        		Collection<? extends GrantedAuthority> authorities=userDetails.getAuthorities();
        		//创建Token
        		String token=jwtTokenUtil.generateToken(userDetails,authorities.toString());
        		//设置编码 防止乱码问题
        		response.setCharacterEncoding("UTF-8");
        		response.setContentType("application/json;charset=utf-8");
        		//在请求头里返回创建成功的token
        		//设置请求头为带有"JWTHeaderName" 
        		response.setHeader(jwtTokenUtil.getHeader(),token);
			    response.setContentType("text/json;charset=utf-8");
    			PrintWriter out=response.getWriter();
    			out.write(JSON.toJSONString(map));
    			out.flush();
    			out.close();
    				
    	}
    }



    class FailureHandler implements AuthenticationFailureHandler {

    	@Override
    	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    			org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {

    				Map<String,Object> map = new HashMap<String, Object>();
    				map.put("code", "500");
    				map.put("msg", exception.getMessage());
    				//设置返回请求头
    				response.setContentType("application/json;charset=utf-8");
    				PrintWriter out =response.getWriter();
    				out.write(JSON.toJSONString(map));
    				out.flush();
    				out.close();
    	}
    } 

}
