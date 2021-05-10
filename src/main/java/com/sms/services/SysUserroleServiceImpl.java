package com.sms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sms.entities.SmsSysUserrole;
import com.sms.entities.SmsSysUserrolePK;
import com.sms.repositories.SysUserroleRepo;

@Service("sysUserroleService")
public class SysUserroleServiceImpl implements SysUserroleService {

	@Autowired
	private SysUserroleRepo sysUserroleRepo;

	@Override
	public void save(String iduser, int idrole) {
		SmsSysUserrole fdsSysUserrole = new SmsSysUserrole();
		fdsSysUserrole.setId(new SmsSysUserrolePK(iduser, idrole));
		sysUserroleRepo.save(fdsSysUserrole);
	}

	@Override
	public List<SmsSysUserrole> findAllByUserId(String iduser) {
		return sysUserroleRepo.findAllByIdUser(iduser);
	}

	@Override
	public void deleteByIduser(String iduser) {
		List<SmsSysUserrole> fdsSysUserrole = sysUserroleRepo.findAllByIdUser(iduser);
		if (fdsSysUserrole != null) {
			sysUserroleRepo.delete(fdsSysUserrole);
		}
	}
	
	@Override
	public String findByRoleId(String userid) {
		return sysUserroleRepo.findByRoleID(userid);
	};

}
