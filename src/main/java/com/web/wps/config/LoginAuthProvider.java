package com.web.wps.config;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.web.wps.logic.entity.SysRole;
import com.web.wps.logic.entity.SysUserDetail;
import com.web.wps.logic.service.SysUserDetailServiceImpl;

/**
 * 
 * @author cpms
 * 登录处理
 *
 */
@Component
public class LoginAuthProvider implements AuthenticationProvider{
	
	@Autowired
	private SysUserDetailServiceImpl sysUserDetailServiceImpl;
	
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		// TODO Auto-generated method stub
		//获取用户名
		String userName=auth.getName();
		//获取密码
		String password=(String) auth.getCredentials();
		
		SysUserDetail userDetail=(SysUserDetail) sysUserDetailServiceImpl.loadUserByUsername(userName);
		
		if (!userDetail.isEnabled()){
			throw new DisabledException("该账号已被禁用,请联系管理员");
		}else if(!userDetail.isAccountNonLocked()){
			throw new LockedException("该账号已被锁定");
		}else if(!userDetail.isAccountNonExpired()){
			throw new AccountExpiredException("该账号已过期，请联系管理员");
		}else if(!userDetail.isCredentialsNonExpired()){
			throw new CredentialsExpiredException("该账户的登录凭证已过期，请重新登录");
		}
		
		BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
		System.out.println(passwordEncoder.encode(password));
		if(!passwordEncoder.matches(password,userDetail.getPassword())){
			throw new BadCredentialsException("密码错误请重新输入");
		}
		
		//查询用户权限
		List<GrantedAuthority> authorities =new ArrayList<>();
		List<Map<String,Object>> roles=sysUserDetailServiceImpl.getRoleByUserId(userDetail.getId());
		for (Map<String,Object> one:roles) {
			SimpleGrantedAuthority authority=new SimpleGrantedAuthority((String) one.get("code"));
			authorities.add(authority);
		}
		userDetail.setAuthorities(authorities);
		
		return new UsernamePasswordAuthenticationToken(userDetail,password,userDetail.getAuthorities());
	}

	// supports函数用来指明该Provider是否适用于该类型的认证，如果不合适，则寻找另一个Provider进行验证处理。
	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
