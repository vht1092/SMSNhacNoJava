package com.sms.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sms.entities.SmsStatement;

@Repository
public interface SmsStatementRepo extends JpaRepository<SmsStatement, String> {
	
	

	List<SmsStatement> findAll();
	
	List<SmsStatement> findAllByActionTypeAndSmsMonthAndCardBrnAndSmsType
	(@Param("actionType") String actionType,@Param("smsMonth") String smsMonth,@Param("cardBrn") String cardBrn,@Param("sSmsType") String smsType);
	
	List<SmsStatement> findAllBySmsMonthAndCardBrnAndSmsType
	(@Param("smsMonth") String smsMonth,@Param("cardBrn") String cardBrn,@Param("sSmsType") String smsType);
	
	
	List<SmsStatement> findAllByActionTypeAndSmsMonthAndDueDateAndSmsType
	(@Param("actionType") String actionType,@Param("smsMonth") String smsMonth,@Param("dueDate") String dueDate,@Param("sSmsType") String smsType);
	
	List<SmsStatement> findAllBySmsMonthAndDueDateAndSmsType
	(@Param("smsMonth") String smsMonth,@Param("dueDate") String dueDate,@Param("sSmsType") String smsType);
	
	@Procedure(name = "GET_SMS_NHAC_NO_SAO_KE")
	List<SmsStatement> getSMSThongBaoSaoKeProc(@Param("p_settl_month") String p_settl_month, @Param("p_card_brn") String p_card_brn);

	
}
