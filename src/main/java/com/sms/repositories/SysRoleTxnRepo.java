package com.sms.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sms.entities.SmsSysRoletxn;

@Repository
public interface SysRoleTxnRepo extends CrudRepository<SmsSysRoletxn, Long> {
	List<SmsSysRoletxn> findAllByIdrole(int roleid);	
}
