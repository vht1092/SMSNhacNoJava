package com.sms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.entities.SmsSysUser;

@Repository
public interface SysUserRepo extends JpaRepository<SmsSysUser, String> {
	SmsSysUser findByEmail(String email);

	List<SmsSysUser> findAllUserByActiveflagIsTrue();
}
