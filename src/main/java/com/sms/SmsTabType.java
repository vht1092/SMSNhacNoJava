package com.sms;

import com.sms.components.*;
import com.vaadin.ui.Component;

/**
 * Danh sach cac component duoc them vao tabsheet
 * Khi them moi cap nhat vao table sms_sys_txn de load vao menu, sms_sys_txn.DESCRIPTION can co noi dung nhu caption cua class
 * @see com.sms.views.MainView
 * */

public enum SmsTabType {

	EXCEPTIONCASE(ExceptionCase.class,ExceptionCase.CAPTION),
	SENDSMSSTATEMENT(SendSmsStatement.class,SendSmsStatement.CAPTION),
	SENDSMSDUEDATE(SendSmsDueDate.class,SendSmsDueDate.CAPTION);
	
	private final String caption;
	private final Class<? extends Component> classComponent;

	private SmsTabType(Class<? extends Component> classComponent,String caption) {
		this.caption = caption;
		this.classComponent = classComponent;
	}

	public String getCaption() {
		return caption;
	}

	public Class<? extends Component> getClassComponent() {
		return classComponent;
	}	
	
	public static SmsTabType getTabType(final String caption){
		SmsTabType result=null;
		for (SmsTabType tabType:values()){
			if(tabType.getCaption().equals(caption)){
				result=tabType;
				break;
			}
		}
		return result;
	}
	

}
