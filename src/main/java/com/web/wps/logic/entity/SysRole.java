package com.web.wps.logic.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "sys_role")
public class SysRole {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "code")
	private String code;
	@Column(name = "name")
	private String name;
	@Column(name = "desc")
	private String desc;
	@Column(name = "create_date")
	private Date create_date;
	@Column(name = "create_by")
	private String create_by;
	@Column(name = "update_date")
	private String update_date;
	@Column(name = "update_by")
	private String update_by;
	@Column(name = "del_flag")
	private Integer del_flag;
}
