package com.sms.services;

import java.sql.SQLException;
import java.util.List;


import com.sms.entities.SmsStatement;

public interface SmsStatementService {

	/**
	 * Danh sach boi user
	 * 
	 * @param page
	 *            PageRequest
	 * @param userid
	 *            user id
	 * @return Page
	 */
	public List<SmsStatement> findAll();

	public List<SmsStatement> findAllByActionTypeAndSmsMonthAndCardBrnAndSmsType(String actionType,String smsMonth,String cardBrn,String smsType);
	
	public List<SmsStatement> findAllBySmsMonthAndCardBrnAndSmsType(String smsMonth,String cardBrn,String smsType);

	public List<SmsStatement> findAllByActionTypeAndSmsMonthAndDueDateAndSmsType(String actionType,String smsMonth,String duedate,String smsType);
	
	public List<SmsStatement> findAllBySmsMonthAndDueDateAndSmsType(String smsMonth,String duedate,String smsType);
	
	void save(SmsStatement smsStatement);
	
	public List<SmsStatement> getDataSMSThongBaoSaoKe(String statementMonth,String crdbrn) throws SQLException;
	
	public List<SmsStatement> getDataSMSNhacNoDueDate(String statementMonth,String duedate) throws SQLException;
	
	public int InsertSMSMessateToEBankGW(String idAlert, String mobile, String message, String msgstat, String smsType) throws SQLException;

	public List<SmsStatement> getSMSThongBaoSaoKeProc(String statementMonth,String crdbrn);

	public List<SmsStatement> getDataSMSNhacNoDueDateProc(String statementMonth,String duedate);
}
