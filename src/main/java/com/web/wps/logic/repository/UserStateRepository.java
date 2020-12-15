package com.web.wps.logic.repository;

import org.springframework.data.jpa.repository.Query;

import com.web.wps.base.BaseRepository;
import com.web.wps.logic.entity.UserState;

public interface UserStateRepository extends BaseRepository<UserState,Integer> {
	
	@Query(nativeQuery=true,value="select * from sys_user_state where userid=:userid")
	public UserState getStateByUserid(Integer userid);

}
