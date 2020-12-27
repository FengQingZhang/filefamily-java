package com.web.wps.logic.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.web.wps.util.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.web.wps.logic.entity.SysRole;
import com.web.wps.logic.entity.SysUserDetail;
import com.web.wps.logic.entity.UserEntity;
import com.web.wps.logic.entity.UserState;
import com.web.wps.logic.repository.UserRepository;
import com.web.wps.logic.repository.UserStateRepository;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class SysUserDetailServiceImpl implements UserDetailsService{
	
	@Resource
	private UserRepository userRepository;
	@Resource
	private UserStateRepository stateRepository;
	@Resource
	private JwtTokenUtil jwtTokenUtil;
	

	@Override
	public UserDetails loadUserByUsername(String userString) throws UsernameNotFoundException {
		// 根据用户名查询用户信息
		UserEntity user=userRepository.findByUserName(userString);
		if(user!=null){
			SysUserDetail sysUserDetail=new SysUserDetail();
			sysUserDetail.setId(user.getId());
			sysUserDetail.setUsername(userString);
			sysUserDetail.setPassword(user.getPassword());
			UserState state =stateRepository.getStateByUserid(user.getId());
			sysUserDetail.setEnabled(state.getEnabled()==1?true:false);
			sysUserDetail.setAccountNonLocked(state.getAccountNonLocked()==1?true:false);
			sysUserDetail.setAccountNonExpired(state.getAccountNonExpired()==1?true:false);
			sysUserDetail.setCredentialsNonExpired(state.getCredentialsNonExpired()==1?true:false);
			return sysUserDetail;
		}else 
		throw new UsernameNotFoundException("该账户不存在");
	}
	
	public List<Map<String,Object>> getRoleByUserId(int userId){
		
		return userRepository.getRoleByUserId(userId);
		
	}

	public JSONObject checkLogin(HttpServletRequest request,HttpServletResponse response){
		JSONObject jsonObject = new JSONObject();
		String tokenHeader=request.getHeader(jwtTokenUtil.getHeader());
		if(!StringUtils.isEmpty(tokenHeader)){
			//根据username加载权限
			String username=jwtTokenUtil.getUsernameFromToken(tokenHeader);
			if(username!=null&& SecurityContextHolder.getContext().getAuthentication()!=null){
				UserDetails userDetails=loadUserByUsername(username);
				if (jwtTokenUtil.validateToken(tokenHeader,userDetails)){
					jsonObject.put("state","ok");
					jsonObject.put("msg","已登录");
				}else {
					jsonObject.put("state","fail");
					jsonObject.put("msg","登录状态过期请重新登录");
				}
			}
		}else {
			jsonObject.put("state","fail");
			jsonObject.put("msg","请您登录后再进行操作");
		}
		return jsonObject;
	}


}
