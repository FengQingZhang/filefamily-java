package com.web.wps.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 
 * @author cpms
 *
 */
@Component
public class LoginAuthProvider implements AuthenticationProvider{
	
	
	
	@Override
	public Authentication authenticate(Authentication authen) throws AuthenticationException {
		// TODO Auto-generated method stub
		//获取用户名
		String userName=authen.getName();
		//获取密码
		String password=(String) authen.getCredentials();
		
		
		
		return null;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
