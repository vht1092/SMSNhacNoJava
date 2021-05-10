package com.sms.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the SMS_SYS_TASK database table.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name="SMS_NHAC_NO")
@NamedQuery(name="SmsStatement.findAll", query="SELECT f FROM SmsStatement f")
public class SmsStatement implements Serializable {
//	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(unique=true, nullable=false, length=24)
	private String id;  //SMS_TYPE + LOC + KY_SAO_KE
	
	@Column(name = "SMS_TYPE",nullable = false, length = 6) 
    private String smsType;
	
	@Column(name = "SMS_DETAIL",nullable = false, length = 300) 
    private String smsDetail; 
	
	@Column(name = "DEST_MOBILE",nullable = false, length = 22) 
    private String desMobile; 
	
	@Column(name = "GET_TRANS_DATETIME",nullable = false, length = 14) 
    private String dateTime;
	
	@Column(name = "INSERT_TRANS_DATETIME",nullable = false, length = 14) 
    private String insertTransDateTime; //time insert qua db ebank
	
	@Column(name = "PAN",nullable = false, length = 19) 
    private String pan;//so the ma hoa
	
	@Column(name = "CARD_BRN",nullable = false, length = 10) 
    private String cardBrn; //VS, MC
	
	@Column(name = "CARD_TYPE",nullable = false, length = 10) 
	private String cardType; //G, W, 
	
	@Column(name = "SMS_MONTH",nullable = false, length = 6) 
    private String smsMonth; //NUMBER(6,0) YYYYMM
	
	@Column(name = "CLOSING_BALANCE",nullable = false, length = 18) 
    private String closingBalance; 
	
	@Column(name = "DUE_DATE",nullable = false, length = 8) 
	private String dueDate;
	
	@Column(name = "MINIMUM_PAYMENT",nullable = false, length = 18) 
	private String minimumPayment;
	
	@Column(name = "ACTION_TYPE",nullable = false, length = 1) 
    private String actionType; //N: tao moi, Y: da gui qua eb, F: gui qua eb failed, D: ko gui
    
	@Column(name = "TOL_BAL_IPP",nullable = false, length = 18) 
	private String totalBalIpp;
	
	@Column(name = "VIP",nullable = false, length = 1) 
    private String vip; 
	
	@Column(name = "CIF_VIP",nullable = false, length = 1) 
    private String cifVip;
	
	@Column(name = "CARD_NO",nullable = false, length = 16) 
    private String cardNo; //so the che
	
	@Column(name = "LOC",nullable = false, length = 12) 
    private String loc;
	
	@Column(name = "ID_STATEMENT",nullable = false, length = 11) 
    private String idStatement;
	
	public SmsStatement() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

	public String getSmsDetail() {
		return smsDetail;
	}

	public void setSmsDetail(String smsDetail) {
		this.smsDetail = smsDetail;
	}

	public String getDesMobile() {
		return desMobile;
	}

	public void setDesMobile(String desMobile) {
		this.desMobile = desMobile;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getInsertTransDateTime() {
		return insertTransDateTime;
	}

	public void setInsertTransDateTime(String insertTransDateTime) {
		this.insertTransDateTime = insertTransDateTime;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getCardBrn() {
		return cardBrn;
	}

	public void setCardBrn(String cardBrn) {
		this.cardBrn = cardBrn;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getSmsMonth() {
		return smsMonth;
	}

	public void setSmsMonth(String smsMonth) {
		this.smsMonth = smsMonth;
	}

	public String getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(String closingBalance) {
		this.closingBalance = closingBalance;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getMinimumPayment() {
		return minimumPayment;
	}

	public void setMinimumPayment(String minimumPayment) {
		this.minimumPayment = minimumPayment;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getTotalBalIpp() {
		return totalBalIpp;
	}

	public void setTotalBalIpp(String totalBalIpp) {
		this.totalBalIpp = totalBalIpp;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getCifVip() {
		return cifVip;
	}

	public void setCifVip(String cifVip) {
		this.cifVip = cifVip;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getIdStatement() {
		return idStatement;
	}

	public void setIdStatement(String idStatement) {
		this.idStatement = idStatement;
	}



}