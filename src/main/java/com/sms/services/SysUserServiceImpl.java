package com.sms.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sms.TimeConverter;
import com.sms.entities.SmsSysUser;
import com.sms.entities.SmsSysUserrole;
import com.sms.entities.SmsSysUserrolePK;
import com.sms.repositories.SysUserRepo;
import com.sms.repositories.SysUserroleRepo;

@Service("sysUserService")
@Transactional
public class SysUserServiceImpl implements SysUserService {

	@Autowired
	private SysUserRepo sysUserRepo;

//	@Autowired
//	private SysRoleRepo roleRepository;
	@Autowired
	private SysUserroleRepo userroleRepo;
	private final TimeConverter timeConverter = new TimeConverter();

	@Override
	public SmsSysUser findAllByEmail(String email) {
		return sysUserRepo.findByEmail(email);
	}

	@Override
	public SmsSysUser findByUserid(String userid) {
		return sysUserRepo.findOne(userid);
	}

	@Override
	public String createNew(String userid, String email, String fullname) {
		SmsSysUser sysUser = new SmsSysUser();
		sysUser.setUserid(userid);
		sysUser.setActiveflag(true);// Mac dinh duoc active
		sysUser.setEmail(email);
		sysUser.setFullname(fullname);
		sysUser.setUsertype("OFF");// User mac dinh la officer
		sysUser.setCreatedate(timeConverter.getCurrentTime());
		sysUser.setLastlogin(timeConverter.getCurrentTime());
		sysUserRepo.save(sysUser);
		// Gan role mac dinh
//		List<GstsSysRole> listDefaultRole = roleRepository.findAllByDefaultroleIsTrue();
//
//		for (GstsSysRole list : listDefaultRole) {
//			GstsSysUserrolePK id = new GstsSysUserrolePK();
//			id.setIdrole(list.getId());
//			id.setIduser(sysUser.getUserid());
//			userroleRepo.save(new GstsSysUserrole(id));
//		}
		return sysUser.getUserid();
	}

	@Override
	public void updateLastLogin(String userid) {
		SmsSysUser user = sysUserRepo.findOne(userid);
		user.setLastlogin(timeConverter.getCurrentTime());
		sysUserRepo.save(user);
	}

	@Override
	public List<SmsSysUser> findAllUser() {
		return sysUserRepo.findAll();
	}

	@Override
	public void updateUserByUserId(String userid, String fullname, String usertype, Boolean active) {
		SmsSysUser GstsSysUser = sysUserRepo.findOne(userid);
		if (GstsSysUser == null) {
			GstsSysUser = new SmsSysUser();
			GstsSysUser.setUserid(userid);
		}
		GstsSysUser.setFullname(fullname);
		GstsSysUser.setUsertype(usertype);
		GstsSysUser.setActiveflag(active);
		GstsSysUser.setUpdatedate(timeConverter.getCurrentTime());
		sysUserRepo.save(GstsSysUser);

	}

	@Override
	public List<SmsSysUser> findAllUserByActiveflagIsTrue() {
		return sysUserRepo.findAllUserByActiveflagIsTrue();
	}

}
