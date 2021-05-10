package com.sms.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.repository.query.Param;

import com.sms.entities.SmsSysTask;

public interface SysTaskService {
	Iterable<SmsSysTask> findAllByUseridWithCurrentTime(String userid, String type);

//	SmsSysTask findOneByObjectTaskAndTypeTask(String object, String type);

//	SmsSysTask findOneByObjectAndCurrentTime(String object, String type);

	List<SmsSysTask> findAllBySmsTypeAndCardbrnAndSmsMonthAndUserId(String smsType, String cardbrn, String smsMonth, String usrId);
	
	List<SmsSysTask> findAllBySmsTypeAndCardbrnAndSmsMonth(String smsType, String cardbrn, String smsMonth);

	List<SmsSysTask> findAllBySmsTypeAndCardbrnAndSmsMonthByNotStatusOrAsgUid(String smsType, String cardbrn, String smsMonth, String status, String asgUid);
	
	SmsSysTask findOneByIdtask(Long idtask);
	
	List<SmsSysTask> findAllByTypeTask(String typetask);
	
//	SmsSysTask findOneByObject(String object);
	void save(SmsSysTask smsSysTask);

	void save(String object, String content, String type, String userid);

	void save(String object, BigDecimal fromdate, BigDecimal todate, String content, String type, String userid, BigDecimal createDate, String merchant, String posmode);

//	void update(String object, BigDecimal fromdate, BigDecimal todate, String content, String type, String userid);

//	void delete(String userid, String object, String type);

	void delete(String userid, String type);

//	void deleteByObjecttaskAndTypetask(String object, String type);
	
	
	/**
	 * 
	 * @param objectTask
	 * @return
	 */
//	public List<Object[]> getUserUpdate(String objectTask);

}
