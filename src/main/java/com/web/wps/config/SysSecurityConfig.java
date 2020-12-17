package com.web.wps.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.wps.config.filter.CustomAuthenticatopnFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.web.wps.config.filter.JwTAuthenticationFilter;
import com.web.wps.logic.service.SysUserDetailServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
public class SysSecurityConfig extends WebSecurityConfigurerAdapter {

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
        http.cors().and().csrf().disable();//跨域设置
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/image/**").permitAll()//静态资源访问无需认证
                .antMatchers("/admdin/*").hasAnyRole("ROLE_ADMIN")//admin开头的请求需要admin权限
                .anyRequest().authenticated()//默认其他请求都需要认证
                .and()
                .formLogin()/*.and()
                .addFilter(new JwTAuthenticationFilter(authenticationManager()))*/;
                http.addFilterAfter(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
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

    @Bean
    CustomAuthenticatopnFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticatopnFilter filter = new CustomAuthenticatopnFilter();
        filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter out = resp.getWriter();
                Map<String,Object> map=new HashMap<>();
                map.put("state","ok");
                map.put("msg","成功");
                out.write(new ObjectMapper().writeValueAsString(map));
                out.flush();
                out.close();
            }
        });
        filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter out = resp.getWriter();
                Map<String,Object> map=new HashMap<>();
                map.put("state","fail");
                map.put("msg","失败");
                out.write(new ObjectMapper().writeValueAsString(map));
                out.flush();
                out.close();
            }
        });
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }


}
