package com.web.wps.logic.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class SysUserDetailServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserStateRepository stateRepository;
	

	@Override
	public UserDetails loadUserByUsername(String userString) throws UsernameNotFoundException {
		// 根据用户名查询用户信息
		UserEntity user=userRepository.findByUserName(userString);
		if(user==null){
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
	
	public List<SysRole> getRoleByUserId(int userId){
		
		return userRepository.getRoleByUserId(userId);
		
	}

}
