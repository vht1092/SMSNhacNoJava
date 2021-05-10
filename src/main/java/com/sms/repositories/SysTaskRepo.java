package com.sms.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sms.entities.SmsSysTask;

@Repository
public interface SysTaskRepo extends CrudRepository<SmsSysTask, Long> {
	
	@Query(value = "select *  from Sms_sys_task t where t.userid = :userid and substr(t.todate, 0, 12) = to_char(to_date('201610311631','yyyyMMddHH24MI'), 'yyyyMMddHH24MI') and t.type_task = :type", nativeQuery = true)
	Iterable<SmsSysTask> findAllByUseridWithCurrentTime(@Param("userid") String userid, @Param("type") String type);

//	@Query("select f from SmsSysTask f where f.loc=:loc and to_number(to_char(sysdate, 'yyyyMMddHH24MISSSSS')) between f.fromdate and f.todate and f.typetask=:type")
//	SmsSysTask findOneByObjectAndCurrentTime(@Param("loc") String loc, @Param("type") String type);

//	@Query("select f from SmsSysTask f where f.loc=:loc and f.typetask=:type")
//	Iterable<SmsSysTask> findAllByObjectTask(@Param("loc") String loc, @Param("type") String type);

	SmsSysTask findOneByIdtask(Long idtask);
	
	List<SmsSysTask> findAllByTypetask(String type);
	
	List<SmsSysTask> findAllBySmsTypeAndCardbrnAndSmsMonthAndUserid(String smsType, String cardbrn, String smsMonth, String usrId);
		
	List<SmsSysTask> findAllBySmsTypeAndCardbrnAndSmsMonth(String smsType, String cardbrn, String smsMonth);
	
	@Query(value = "SELECT * FROM SMS_SYS_TASK WHERE SMS_TYPE=:smsType AND CARD_BRN=:cardbrn AND SMS_MONTH=:smsMonth AND (STATUS <> :status OR ASG_UID=:asgUid)", nativeQuery = true)
	List<SmsSysTask> findAllBySmsTypeAndCardbrnAndSmsMonthByNotStatusOrAsgUid(@Param("smsType") String smsType, @Param("cardbrn") String cardbrn, @Param("smsMonth") String smsMonth, @Param("status") String status, @Param("asgUid") String asgUid);
		
	// Dung cho exception case
//	@Query("select f from SmsSysTask f where f.loc=:loc and f.typetask=:type")
//	SmsSysTask findOneByObjecttaskAndTypetask(@Param("loc") String loc, @Param("type") String type);

	@Query(value = "select to_number(to_char(SYSDATE, 'yyyyMMddHH24MISSSSS')) from dual", nativeQuery = true)
	BigDecimal getCurrentTime();


//	void deleteByUseridAndObjecttaskAndTypetask(@Param("userid") String userid, @Param("loc") String loc, @Param("type") String type);

	void deleteByUseridAndTypetask(@Param("userid") String userid, @Param("type") String type);

//	void deleteByObjecttaskAndTypetask(@Param("loc") String loc, @Param("type") String type);
	
//	@Query("select t from SmsSysTask t where loc =:loc")
//	SmsSysTask findOneByObject(@Param("loc") String loc);
	
	@Query(value = "select USERID, CREATEDATE from {h-schema}Sms_SYS_TASK where loc = :loc", nativeQuery = true)
	public List<Object[]> getUserUpdate(@Param("loc") String loc);
}
