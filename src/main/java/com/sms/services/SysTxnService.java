package com.sms.services;

import java.util.List;

import com.sms.entities.SmsSysTxn;

public interface SysTxnService {
	List<Object[]> findAllByUserId(String id);
	List<Object[]> findAllByRoleId(int id);
	List<SmsSysTxn> findAll();
}
