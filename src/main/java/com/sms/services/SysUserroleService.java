package com.sms.services;

import java.util.List;

import com.sms.entities.SmsSysUserrole;

public interface SysUserroleService {
	void save(String iduser, int idrole);
	void deleteByIduser(String iduser);
	List<SmsSysUserrole> findAllByUserId(String iduser);	
	String findByRoleId(String userid);
}
