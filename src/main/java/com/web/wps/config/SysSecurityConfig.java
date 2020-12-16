package com.web.wps.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.web.wps.config.filter.JwTAuthenticationFilter;
import com.web.wps.logic.service.SysUserDetailServiceImpl;

@EnableWebSecurity
public class SysSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private LoginAuthProvider loginAuthProvider;
	@Autowired
	private SysUserDetailServiceImpl userDetailService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		//auth.authenticationProvider(loginAuthProvider);
		auth.userDetailsService(userDetailService);
	}

	//jwtAuthenticationProvider
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
		    .antMatchers("/image/**").permitAll()//静态资源访问无需认证
		    .antMatchers("/admdin/*").hasAnyRole("ROLE_ADMIN")//admin开头的请求需要admin权限
		    .anyRequest().authenticated()//默认其他请求都需要认证
		    .and() 
		    .csrf().disable()//crsf 禁用因为不使用session
		    .sessionManagement().disable()//禁用session
		    .formLogin().disable()//禁用form登录
		    .addFilter(new JwTAuthenticationFilter(authenticationManager()))
		    ;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		super.configure(web);
	}
	
	
	
}
