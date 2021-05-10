package com.sms.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sms.entities.SmsSysRoletxn;
import com.sms.repositories.SysRoleTxnRepo;

@Service("sysRoleTxnService")
@Transactional
public class SysRoleTxnServiceImpl implements SysRoleTxnService {

	@Autowired
	private SysRoleTxnRepo sysRoleTxnRepo;

	@Override
	public void save(int roleid, String txnid) {
		SmsSysRoletxn fdsSysRoletxn = new SmsSysRoletxn();
		fdsSysRoletxn.setIdrole(roleid);
		fdsSysRoletxn.setIdtxn(txnid);
		fdsSysRoletxn.setFlgauth(true);
		fdsSysRoletxn.setFlginit(true);
		fdsSysRoletxn.setFlgview(true);
		sysRoleTxnRepo.save(fdsSysRoletxn);
	}

	@Override
	public void deleteByRoleId(int roleid) {
		List<SmsSysRoletxn> fdsSysRoletxn = sysRoleTxnRepo.findAllByIdrole(roleid);
		sysRoleTxnRepo.delete(fdsSysRoletxn);
	}
}
