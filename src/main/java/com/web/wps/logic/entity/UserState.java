package com.web.wps.logic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.web.wps.base.BaseEntity;

import lombok.Data;

/**
 * 用户状态类
 * @author cpms
 *
 */
@Data
@Entity
@Table(name="sys_user_state")
public class UserState extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name="userid")
	private String userid;
	@Column(name="account_non_expired")
	private int accountNonExpired;
	@Column(name="account_non_locked")
	private int accountNonLocked;
	@Column(name="credentials_non_expired")
	private int credentialsNonExpired;
	@Column(name="enabled")
	private int enabled;
}
