package com.sms.services;

import java.util.List;

import com.sms.entities.SmsSysUser;

public interface SysUserService {
	public SmsSysUser findAllByEmail(String email);

	public List<SmsSysUser> findAllUser();
	
	public List<SmsSysUser> findAllUserByActiveflagIsTrue();

	public String createNew(String userid, String email, String fullname);

	public void updateLastLogin(String userid);

	public void updateUserByUserId(String userid, String fullname, String usertype, Boolean active);

	public SmsSysUser findByUserid(String userid);
}
