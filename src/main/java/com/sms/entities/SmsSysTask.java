package com.sms.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the SMS_SYS_TASK database table.
 * 
 */
@Entity
@Table(name="SMS_SYS_TASK")
@NamedQuery(name="SmsSysTask.findAll", query="SELECT f FROM SmsSysTask f")
public class SmsSysTask implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SMS_SYS_TASK_IDTASK_GENERATOR", sequenceName="SQ_SMS_SYS_TASK")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SMS_SYS_TASK_IDTASK_GENERATOR")
	@Column(unique=true, nullable=false)
	private long idtask;

	@Column(nullable=false, length=255)
	private String contenttask;

	@Column(name = "SMS_TYPE", length = 6) 
	private String smsType;
	
	@Column(name = "CARD_BRN", length = 2) 
	private String cardbrn;
	
	@Column(name = "SMS_MONTH", length = 6) 
	private String smsMonth;
	
	@Column(nullable = false, precision = 12)
	private BigDecimal loc;

	private BigDecimal priority;

	@Column(nullable=false, length=10)
	private String typetask;

	@Column(nullable=false, length=12)
	private String userid;
	
	@Column(name = "CREATEDATE", precision=14)
	private BigDecimal createdate;
	
	@Column(nullable=false, length=1)
	private String blod;
	
	@Column(nullable=false, length=1)
	private String sms;
	
	@Column(nullable=false, length=6)
	private String status;
	
	@Column(name = "ASG_UID", nullable=false, length=12)
	private String asgUid;
	
	@Column(name = "ASG_TMS", precision=14)
	private BigDecimal asgTms;
	
	@Column(name = "ASG_NOTE", nullable=false, length=255)
	private String asgNote;
	
	public SmsSysTask() {
	}

	public long getIdtask() {
		return this.idtask;
	}

	public void setIdtask(long idtask) {
		this.idtask = idtask;
	}

	public String getContenttask() {
		return this.contenttask;
	}

	public void setContenttask(String contenttask) {
		this.contenttask = contenttask;
	}

	public BigDecimal getPriority() {
		return this.priority;
	}

	public void setPriority(BigDecimal priority) {
		this.priority = priority;
	}

	public String getTypetask() {
		return this.typetask;
	}

	public void setTypetask(String typetask) {
		this.typetask = typetask;
	}

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public BigDecimal getCreatedate() {
		return createdate;
	}

	public void setCreatedate(BigDecimal createdate) {
		this.createdate = createdate;
	}

	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

	public String getCardbrn() {
		return cardbrn;
	}

	public void setCardbrn(String cardbrn) {
		this.cardbrn = cardbrn;
	}

	public String getSmsMonth() {
		return smsMonth;
	}

	public void setSmsMonth(String smsMonth) {
		this.smsMonth = smsMonth;
	}

	public BigDecimal getLoc() {
		return loc;
	}

	public void setLoc(BigDecimal loc) {
		this.loc = loc;
	}

	public String getBlod() {
		return blod;
	}

	public void setBlod(String blod) {
		this.blod = blod;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAsgUid() {
		return asgUid;
	}

	public void setAsgUid(String asgUid) {
		this.asgUid = asgUid;
	}

	public BigDecimal getAsgTms() {
		return asgTms;
	}

	public void setAsgTms(BigDecimal asgTms) {
		this.asgTms = asgTms;
	}

	public String getAsgNote() {
		return asgNote;
	}

	public void setAsgNote(String asgNote) {
		this.asgNote = asgNote;
	}



}