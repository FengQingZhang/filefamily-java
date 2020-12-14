package com.web.wps.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.web.wps.logic.entity.SysUserDetail;

public interface SysUserDetailRepository extends JpaRepository<SysUserDetail,Integer>{
	
	@Query(nativeQuery = true, value = "select * from user where username=:username and del_flag=0")
	public SysUserDetail findByUserName(@Param("username")String username);

}
