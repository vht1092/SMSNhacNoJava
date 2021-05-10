package com.sms.services;

import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.result.Output;
import org.hibernate.result.ResultSetOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sms.SecurityDataSourceConfig;
import com.sms.entities.SmsStatement;
import com.sms.repositories.SmsStatementRepo;

import oracle.jdbc.OracleTypes;

@Service("smsStatementService")
@Transactional
public class SmsStatementServiceImpl implements SmsStatementService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsStatementServiceImpl.class);
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
	private String sSchema;
	@Autowired
	private SmsStatementRepo smsStatementRepo;
	
	protected DataSource localDataSource;
	protected SecurityDataSourceConfig securityDataSourceConfig = new SecurityDataSourceConfig();
	
	@PersistenceContext
    private EntityManager em;

	@Override
	public List<SmsStatement> findAll() {
		return smsStatementRepo.findAll();
	}
	
	@Override
	public List<SmsStatement> findAllByActionTypeAndSmsMonthAndCardBrnAndSmsType(String actionType,String smsMonth,String cardBrn,String smsType){
		return smsStatementRepo.findAllByActionTypeAndSmsMonthAndCardBrnAndSmsType(actionType, smsMonth, cardBrn, smsType);
	}

	@Override
	public List<SmsStatement> findAllBySmsMonthAndCardBrnAndSmsType(String smsMonth,String cardBrn,String smsType){
		return smsStatementRepo.findAllBySmsMonthAndCardBrnAndSmsType(smsMonth, cardBrn, smsType);
	}
	
	@Override
	public List<SmsStatement> findAllByActionTypeAndSmsMonthAndDueDateAndSmsType(String actionType,String smsMonth,String duedate,String smsType){
		return smsStatementRepo.findAllByActionTypeAndSmsMonthAndDueDateAndSmsType(actionType, smsMonth, duedate, smsType);
	}
	
	@Override
	public List<SmsStatement> findAllBySmsMonthAndDueDateAndSmsType(String smsMonth,String duedate,String smsType){
		return smsStatementRepo.findAllBySmsMonthAndDueDateAndSmsType(smsMonth, duedate, smsType);
	}
	
	
	@Override
	public void save(SmsStatement smsStatement) {

		smsStatementRepo.save(smsStatement);
	}
	
	@Override
	public List<SmsStatement> getDataSMSThongBaoSaoKe(String statementMonth,String crdbrn) throws SQLException {
		Connection connect = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		
		String sp_GetSMSNhacNoSaoKe = "{call GET_SMS_NHAC_NO_SAO_KE(?,?,?)} ";
			
		List<SmsStatement> smsStatementList = new ArrayList<SmsStatement>();
			
		try {
			connect = securityDataSourceConfig.securityDataSourceIM().getConnection();
			
			callableStatement = connect.prepareCall(sp_GetSMSNhacNoSaoKe);
			callableStatement.setString(1, statementMonth);
			callableStatement.setString(2, crdbrn);
			callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
			callableStatement.executeUpdate();
			rs = (ResultSet) callableStatement.getObject(3);
			
			while(rs.next()) {
				
				SmsStatement smsStatement = new SmsStatement();
				smsStatement.setId(rs.getString("ID"));
				smsStatement.setSmsType(rs.getString("SMS_TYPE"));
				smsStatement.setSmsDetail(rs.getString("SMS_DETAIL"));
				smsStatement.setDesMobile(rs.getString("DEST_MOBILE"));
				smsStatement.setDateTime(rs.getString("GET_TRANS_DATETIME"));
				smsStatement.setInsertTransDateTime(rs.getString("INSERT_TRANS_DATETIME"));
				smsStatement.setPan(rs.getString("PAN"));
				smsStatement.setCardBrn(rs.getString("CARD_BRN"));
				smsStatement.setCardType(rs.getString("CRD_PRD"));
				smsStatement.setSmsMonth(rs.getString("SMS_MONTH"));
				smsStatement.setClosingBalance(rs.getString("TOTAL_CLO_BAL"));
				smsStatement.setDueDate(rs.getString("DUE_DATE"));
				smsStatement.setMinimumPayment(rs.getString("TOTAL_REPAY_MIN"));
				smsStatement.setActionType(rs.getString("ACTION_TYPE"));
				smsStatement.setTotalBalIpp(rs.getString("TOL_BAL_IPP"));
				smsStatement.setVip(rs.getString("VIP"));
				smsStatement.setCifVip(rs.getString("CIF_VIP"));
				smsStatement.setCardNo(rs.getString("CARD_NO"));
				smsStatement.setLoc(rs.getString("LOC"));
				smsStatement.setIdStatement(rs.getString("ID_STATEMENT"));
	
				smsStatementList.add(smsStatement);
			}
		}
		catch (Exception ex){
			LOGGER.error(ex.getMessage());
		}
		finally{
			connect.close();
			callableStatement.close();
			rs.close();
		}
		
		return smsStatementList;
	}
	
	@Override
	public List<SmsStatement> getDataSMSNhacNoDueDate(String statementMonth,String duedate) throws SQLException {
		
		ResultSet rs = null;
		CallableStatement callableStatement = null;
		Connection connect = null;
		
		String sp_GetSMSNhacNoDueDate = "{call GET_SMS_NHAC_NO_DUE_DATE(?,?,?)}";
		
		List<SmsStatement> smsStatementList = new ArrayList<SmsStatement>();
		
		try {
			connect = securityDataSourceConfig.securityDataSourceIM().getConnection();
			callableStatement = connect.prepareCall(sp_GetSMSNhacNoDueDate);
			callableStatement.setString(1, statementMonth);
			callableStatement.setString(2, duedate);
			callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
			callableStatement.executeUpdate();
			rs = (ResultSet) callableStatement.getObject(3);
			
			while (rs.next()) {
				SmsStatement smsStatement = new SmsStatement();
				smsStatement.setId(rs.getString("ID"));
				smsStatement.setSmsType(rs.getString("SMS_TYPE"));
				smsStatement.setSmsDetail(rs.getString("SMS_DETAIL"));
				smsStatement.setDesMobile(rs.getString("DEST_MOBILE"));
				smsStatement.setDateTime(rs.getString("GET_TRANS_DATETIME"));
				smsStatement.setInsertTransDateTime(rs.getString("INSERT_TRANS_DATETIME"));
				smsStatement.setPan(rs.getString("PAN"));
				smsStatement.setCardBrn(rs.getString("CARD_BRN"));
				smsStatement.setCardType(rs.getString("CRD_PRD"));
				smsStatement.setSmsMonth(rs.getString("SMS_MONTH"));
				smsStatement.setClosingBalance(rs.getString("TOTAL_CLO_BAL"));
				smsStatement.setDueDate(rs.getString("DUE_DATE"));
				smsStatement.setMinimumPayment(rs.getString("TOTAL_REPAY_MIN"));
				smsStatement.setActionType(rs.getString("ACTION_TYPE"));
				smsStatement.setTotalBalIpp(rs.getString("TOL_BAL_IPP"));
				smsStatement.setVip(rs.getString("VIP"));
				smsStatement.setCifVip(rs.getString("CIF_VIP"));
				smsStatement.setCardNo(rs.getString("CARD_NO"));
				smsStatement.setLoc(rs.getString("LOC"));
				smsStatement.setIdStatement(rs.getString("ID_STATEMENT"));
	
				smsStatementList.add(smsStatement);
			}
		}
		catch (Exception ex){
			LOGGER.error(ex.getMessage());
		}
		finally{
			callableStatement.close();
			rs.close();
			connect.close();
		}
		
		return smsStatementList;
	}
	
	@Override
	public int InsertSMSMessateToEBankGW(String idAlert, String mobile, String message,
            String msgstat, String smsType) throws SQLException {
		
		securityDataSourceConfig = new SecurityDataSourceConfig();
		Connection con = securityDataSourceConfig.securityDataSourceEB().getConnection();
		String sp_GetSMSNhacNoSaoKe = "{call SMS_SCB.PROC_INS_MASTERCARD_KM(?,?,?,?,?)}";
		
		CallableStatement callableStatement = null;
        try { 
        	callableStatement = con.prepareCall(sp_GetSMSNhacNoSaoKe);
			
        	callableStatement.setString(1, idAlert);
        	callableStatement.setString(2, mobile.trim());
        	callableStatement.setString(3, message);
        	callableStatement.setString(4, msgstat);
        	callableStatement.setString(5, smsType);
    		
        	int val = callableStatement.executeUpdate();
	        return val;
		}catch (SQLException e) {
			try {
				LOGGER.error(e.getMessage());
				callableStatement.close();
				con.close();
			} catch (SQLException ex) {
				LOGGER.error(ex.getMessage());
			}
			return 0;
		} 
        
	}
	
	@Override
	public List<SmsStatement> getSMSThongBaoSaoKeProc(String statementMonth,String crdbrn){
		Session session = em.unwrap(Session.class);
		ProcedureCall call = session
		.createStoredProcedureCall("GET_SMS_NHAC_NO_SAO_KE@IM");
		call.registerParameter(1, String.class, ParameterMode.IN).bindValue(statementMonth);
		call.registerParameter(2, String.class, ParameterMode.IN).bindValue(crdbrn);
		call.registerParameter(3, Class.class, ParameterMode.REF_CURSOR);
		Output output = call.getOutputs().getCurrent();
		List<SmsStatement> smsStatementList = new ArrayList<SmsStatement>();
		if(output.isResultSet()) {
			smsStatementList = ((ResultSetOutput) output).getResultList();
		}
		return smsStatementList;
		 
		
	}
	
	@Override
	public List<SmsStatement> getDataSMSNhacNoDueDateProc(String statementMonth,String duedate){
		Session session = em.unwrap(Session.class);
		ProcedureCall call = session
		.createStoredProcedureCall("GET_SMS_NHAC_NO_DUE_DATE");
		call.registerParameter(1, String.class, ParameterMode.IN).bindValue(statementMonth);
		call.registerParameter(2, String.class, ParameterMode.IN).bindValue(duedate);
		call.registerParameter(3, SmsStatement.class, ParameterMode.REF_CURSOR);
		Output output = call.getOutputs().getCurrent();
		List<SmsStatement> smsStatementList = new ArrayList<SmsStatement>();
		if(output.isResultSet()) {
			List<Object[]> objectList = ((ResultSetOutput) output).getResultList();
		}
		
		return smsStatementList;
	}
	

	
}
