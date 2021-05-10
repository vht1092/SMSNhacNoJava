package com.sms.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the FDS_SYS_USERROLE database table.
 * 
 */
@Entity
@Table(name="SMS_SYS_USERROLE")
@NamedQuery(name="SmsSysUserrole.findAll", query="SELECT f FROM SmsSysUserrole f")
public class SmsSysUserrole implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SmsSysUserrolePK id;

	public SmsSysUserrole() {
	}
	

	public SmsSysUserrole(SmsSysUserrolePK id) {
		super();
		this.id = id;
	}



	public SmsSysUserrolePK getId() {
		return this.id;
	}

	public void setId(SmsSysUserrolePK id) {
		this.id = id;
	}

}