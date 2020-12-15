package com.web.wps.logic.repository;

import com.web.wps.base.BaseRepository;
import com.web.wps.logic.entity.SysRole;
import com.web.wps.logic.entity.UserEntity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends BaseRepository<UserEntity, String> {

    List<UserEntity> findByIdIn(List<String> id);
    
    
	@Query(nativeQuery = true, value = "select * from w_user_t where username=:username and del_flag=0")
	public UserEntity findByUserName(@Param("username")String username);
	/**
	 * 根据用户id查询权限
	 * @param userId
	 * @return
	 */
	@Query(nativeQuery = true,value = "select r.* from  sys_role_user ru left join w_user_t u on ru.user_id = u.id left join sys_role r on ru.role_id = r.id where u.id=:userId ")
	List<SysRole> getRoleByUserId(int userId);
    

}
