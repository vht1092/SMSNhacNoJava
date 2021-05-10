package com.sms.services;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sms.TimeConverter;
import com.sms.entities.SmsSysTask;
import com.sms.repositories.SysTaskRepo;

@Service("sysTaskService")
@Transactional
public class SysTaskServiceImpl implements SysTaskService {

	@Value("${spring.jpa.properties.hibernate.default_schema}")
	private String sSchema;
	@Autowired
	private SysTaskRepo sysTaskRepo;
	private TimeConverter timeConverter;
	
	@Override
	public SmsSysTask findOneByIdtask(Long idtask){
		return sysTaskRepo.findOneByIdtask(idtask);
	}

	@Override
	public Iterable<SmsSysTask> findAllByUseridWithCurrentTime(String userid, String type) {
		return sysTaskRepo.findAllByUseridWithCurrentTime(userid, type);
	}

	@Override
	public List<SmsSysTask> findAllBySmsTypeAndCardbrnAndSmsMonthAndUserId(String smsType, String cardbrn, String smsMonth, String usrId){
		return sysTaskRepo.findAllBySmsTypeAndCardbrnAndSmsMonthAndUserid(smsType, cardbrn, smsMonth, usrId);
	}
	
	@Override
	public List<SmsSysTask> findAllBySmsTypeAndCardbrnAndSmsMonth(String smsType, String cardbrn, String smsMonth){
		return sysTaskRepo.findAllBySmsTypeAndCardbrnAndSmsMonth(smsType, cardbrn, smsMonth);
	}
	
	@Override
	public List<SmsSysTask> findAllBySmsTypeAndCardbrnAndSmsMonthByNotStatusOrAsgUid(String smsType, String cardbrn, String smsMonth, String status, String asgUid){
		return sysTaskRepo.findAllBySmsTypeAndCardbrnAndSmsMonthByNotStatusOrAsgUid(smsType, cardbrn, smsMonth, status, asgUid);
	}
	
	@Override
	public void save(SmsSysTask smsSysTask) {
//		SmsSysTask tempSmsSysTask = sysTaskRepo.findOneByIdtask(smsSysTask.getIdtask());
//		if (tempSmsSysTask != null) {
//			tempSmsSysTask.setContenttask(smsSysTask.getContenttask());
//			tempSmsSysTask.setBlod(smsSysTask.getBlod());
//			tempSmsSysTask.setSms(smsSysTask.getSms());
//			tempSmsSysTask.setStatus(smsSysTask.getStatus());
//			tempSmsSysTask.setAsgUid(smsSysTask.getAsgUid());
//			tempSmsSysTask.setAsgTms(smsSysTask.getAsgTms());
//			tempSmsSysTask.setAsgNote(smsSysTask.getAsgNote());
//			sysTaskRepo.save(tempSmsSysTask);
//		}
//		else
			sysTaskRepo.save(smsSysTask);
	}
	
	@Override
	public void save(String object, BigDecimal fromdate, BigDecimal todate, String content, String type, String userid, BigDecimal createDate, String merchant, String posmode) {

		SmsSysTask SmsSysTask = new SmsSysTask();
//		SmsSysTask tempSmsSysTask = sysTaskRepo.findOneByObjecttaskAndTypetask(object, type);
//		if (tempSmsSysTask != null) {
//			SmsSysTask = tempSmsSysTask;
//		}
		SmsSysTask.setContenttask(content);
		SmsSysTask.setTypetask(type);
		SmsSysTask.setUserid(userid);
		SmsSysTask.setCreatedate(createDate);
		sysTaskRepo.save(SmsSysTask);
	}

	@Override

	public void save(String object, String content, String type, String userid) {
		SmsSysTask SmsSysTask = new SmsSysTask();
		timeConverter = new TimeConverter();
		SmsSysTask.setContenttask(content);
		SmsSysTask.setTypetask(type);
		SmsSysTask.setUserid(userid);
		sysTaskRepo.save(SmsSysTask);
	}

//	@Override
//	public SmsSysTask findOneByObjectTaskAndTypeTask(String object, String type) {
//		return sysTaskRepo.findOneByObjecttaskAndTypetask(object, type);
//	}

//	@Override
//	public SmsSysTask findOneByObjectAndCurrentTime(String object, String type) {
//		return sysTaskRepo.findOneByObjectAndCurrentTime(object, type);
//	}

//	@Override
//	public void update(String object, BigDecimal fromdate, BigDecimal todate, String content, String type, String userid) {
//		SmsSysTask SmsSysTask = sysTaskRepo.findOneByObjecttaskAndTypetask(object, type);
//		SmsSysTask.setFromdate(fromdate);
//		SmsSysTask.setTodate(todate);
//		SmsSysTask.setContenttask(content);
//		SmsSysTask.setUserid(userid);
//		sysTaskRepo.save(SmsSysTask);
//
//	}
	
//	@Override
//	public SmsSysTask findOneByObject(String object) {
//		return sysTaskRepo.findOneByObject(object);
//	}

//	@Override
//	public void delete(String userid, String object, String type) {
//		sysTaskRepo.deleteByUseridAndObjecttaskAndTypetask(userid, object, type);
//	}

	@Override
	public void delete(String userid, String type) {
		sysTaskRepo.deleteByUseridAndTypetask(userid, type);
	}

	@Override
	public List<SmsSysTask> findAllByTypeTask(String typetask) {
		return sysTaskRepo.findAllByTypetask(typetask);
	}

//	@Override
//	public void deleteByObjecttaskAndTypetask(String object, String type) {
//		sysTaskRepo.deleteByObjecttaskAndTypetask(object, type);
//
//	}
	
//	@Override
//	public List<Object[]> getUserUpdate(String objectTask) {
//		return sysTaskRepo.getUserUpdate(objectTask);
//	}


}
