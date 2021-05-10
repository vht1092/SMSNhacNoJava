package com.sms.components;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.sms.ReloadComponent;
import com.sms.SecurityUtils;
import com.sms.SpringContextHelper;
import com.sms.services.SysUserroleService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Man hinh thong ke chung
 * 
 */
@SpringComponent
@ViewScope
public class SystemStatistic extends VerticalLayout implements ReloadComponent {

	private static final long serialVersionUID = 1L;
	public static final String CAPTION = "THÔNG TIN ĐĂNG NHẬP";
	private final Label lbLatestLogin = new Label();
	private final Label lbTotalAssignedCases = new Label();
	private final SysUserroleService sysUserroleService;
	private String sUserId = "";
	private String CheckUserId = "";
	private String roleDescription = "";
	private String note = "";

	public SystemStatistic() {
		setCaption(CAPTION);
		setSpacing(true);
		setMargin(true);
		SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		sysUserroleService = (SysUserroleService) helper.getBean("sysUserroleService");
		
		this.sUserId = SecurityUtils.getUserId();
		CheckUserId = sysUserroleService.findByRoleId(sUserId);
		
		final Date date = new Date();
		final SimpleDateFormat simpledateformat_current = new SimpleDateFormat("dd/M/yyyy");

		switch(CheckUserId) {
			case "1": 
				roleDescription = "QUYỀN USER TNT YÊU CẦU GHI NHẬN THÔNG TIN";
				note = "User phòng tác nghiệp thẻ cung cấp ghi thông tin ghi nhận cho phòng KTT, \ndữ liệu chỉ hiển thị theo user đăng nhập tương ứng";
				break;
			case "2": 
				roleDescription = "QUYỀN USER KTT PHẢN HỒI THÔNG TIN GHI NHẬN";
				note = "User phòng KTT phản hồi cho NEW case hoặc update thông tin ghi nhận cho phòng TNT, \ndữ liệu hiển thị theo trạng thái hoàn tất hoặc cập nhật theo user xử lý và theo new case";
				break;
			case "3": 
				
				break;
			case "4": 
				roleDescription = "TOÀN QUYỀN USER TRÊN TẤT CẢ CÁC CASE GHI NHẬN";
				note = "User quản lý thấy tất cả các case của tất cả các user đã tạo và xử lý case";
				break;
		}
		final Label label_title = new Label(roleDescription);
		label_title.setStyleName(ValoTheme.LABEL_H3);
		
		final Label label_note = new Label("Ghi chú: " + note);
		label_note.setStyleName(ValoTheme.LABEL_H4);
		
		lbLatestLogin.setValue("Latest login: [" + SecurityUtils.getLastLogin() + "]");
//		lbTotalAssignedCases.setValue("Tổng số case đã xử lý: [" + "" + "]");

		addComponent(label_title);
		addComponent(label_note);
		addComponent(lbLatestLogin);
		addComponent(lbTotalAssignedCases);
	}

	@Override
	public void eventReload() {
		lbLatestLogin.setValue("Latest login: [" + SecurityUtils.getLastLogin() + "]");
//		lbTotalAssignedCases.setValue("Tổng số case đã xử lý: [" + "" + "]");

	}

}
