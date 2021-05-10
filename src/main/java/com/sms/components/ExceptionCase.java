package com.sms.components;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.context.annotation.Scope;

import com.sms.ReloadAutoComponent;
import com.sms.ReloadComponent;
import com.sms.SecurityUtils;
import com.sms.SpringContextHelper;
import com.sms.TimeConverter;
import com.sms.entities.SmsSysTask;
import com.sms.services.DescriptionService;
import com.sms.services.SysTaskService;
import com.sms.services.SysUserroleService;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;
/**
 * tanvh1 Aug 20, 2019
 *
 */
@SpringComponent
@Scope("prototype")
public class ExceptionCase extends CustomComponent implements ReloadAutoComponent, ReloadComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String CAPTION = "GHI NHẬN THÔNG TIN SMS";
	private static final String CARD_BRN = "LOẠI THẺ";
	private static final String SMSTYPE = "SMS TYPE";
	private static final String SMSMONTH = "KỲ";
	private static final String LOC = "LOC";
	private static final String CONTENT = "NỘI DUNG YÊU CẦU";
	private static final String SUBMIT = "SUBMIT";
	private static final String DONE = "HOÀN THÀNH";
	private static final String UPDATE = "CẬP NHẬT";
	public static final String TYPE = "EXCEPTION";
	public static final String STATUSNEW = "NEW";
	private static final String TASKNOTE = "Thêm nội dung";
	
	
	public final transient FormLayout formLayout = new FormLayout();
	
	
	private final SysUserroleService sysUserroleService;
	private SysTaskService sysTaskService;
	
	public final transient Grid gridContent;
	public final transient IndexedContainer containerContent;
	
	public final transient ComboBox cbbSmsType;
	public final transient ComboBox cbbCardbrn;
	public final transient ComboBox cbbSmsMonth;
	public final transient TextField tfLOC;
	TextArea txtareaComment;
	TextArea txtareaTasknote;
	TextArea txtareaTasknoteFull;
	Label lbTaskNoteFull;
	final Button btSubmit = new Button(SUBMIT);
	final Button btDone = new Button(DONE);
	final Button btUpdate = new Button(UPDATE);
	private final transient Panel panelResponse = new Panel("THÔNG TIN PHẢN HỒI");
	
	private Window confirmDialog = new Window();
	private Button bYes;
	private Button bNo;
	private transient String sUserId;
	private String CheckUserId = "";
	final TimeConverter timeConverter = new TimeConverter();
	
	Long gridIdtask;
	String gridUserId = "";
	String gridCreateDate = "";
			
	
	public ExceptionCase() {
		//TAN 20190815
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		final DescriptionService descService = (DescriptionService) helper.getBean("descriptionService");
		sysUserroleService = (SysUserroleService) helper.getBean("sysUserroleService");
		sysTaskService = (SysTaskService) helper.getBean("sysTaskService");
		
		this.sUserId = SecurityUtils.getUserId();
		CheckUserId = sysUserroleService.findByRoleId(sUserId);
		
		Label lbSmsType = new Label(SMSTYPE);
		cbbSmsType = new ComboBox();
		cbbSmsType.setNullSelectionAllowed(false);
//		cbbSmsType.setWidth("20%");
		cbbSmsType.addItems("DEBT01","DEBT02");
		cbbSmsType.setItemCaption("DEBT01", "SMS SAO KÊ");
		cbbSmsType.setItemCaption("DEBT02", "SMS NHẮC NỢ");
		cbbSmsType.setValue("DEBT01");
		
		Label lbCardbrn = new Label(CARD_BRN);
		lbCardbrn.setWidth(65f, Unit.PIXELS);
		cbbCardbrn = new ComboBox();
		cbbCardbrn.setNullSelectionAllowed(false);
//		cbbCardbrn.setWidth("20%");
		cbbCardbrn.addItems("MC","VS");
		cbbCardbrn.setValue("MC");
		
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		Label lbSmsMonth = new Label(SMSMONTH);
		cbbSmsMonth = new ComboBox();
		cbbSmsMonth.setNullSelectionAllowed(false);
		cbbSmsMonth.setPageLength(12);
		descService.findAllByTypeByOrderBySequencenoDesc("KYSAOKE").forEach(item -> {
			cbbSmsMonth.addItem(item.getId());
			cbbSmsMonth.setItemCaption(item.getId(),item.getDescription());
		});
		SimpleDateFormat formatterSmsMonth = new SimpleDateFormat("yyyyMM");
		String cbbSmsMonthdefault = formatterSmsMonth.format(cal.getTime());
		cbbSmsMonth.setValue(cbbSmsMonthdefault);
		
		Label lbLoc = new Label(LOC);
		lbLoc.setWidth(58.11f, Unit.PIXELS);
		tfLOC = new TextField();
//		tfLOC.setWidth("20%");
		
		Label lbComment = new Label(CONTENT);
		lbComment.setWidth(65f, Unit.PIXELS);
		txtareaComment = new TextArea();
		txtareaComment.setSizeFull();
		txtareaComment.setWidth(335, Unit.PIXELS);
		txtareaComment.setHeight(70, Unit.PIXELS);
		
		final CheckBox chBoxBLOD = new CheckBox("BLOD");
		chBoxBLOD.setDescription("Checked nếu có vấn đề liên quan đến file PDF");
		
		final CheckBox chBoxSMS = new CheckBox("SMS");
		chBoxSMS.setDescription("Checked nếu có vấn đề liên quan đến nội dung SMS");
		
		btSubmit.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSubmit.setWidth(100.0f, Unit.PIXELS);
		btSubmit.addClickListener(event -> {
			String smsType = cbbSmsType.getValue().toString();
			String cardbrn = cbbCardbrn.getValue().toString();
			String smsMonth = cbbSmsMonth.getValue().toString();
			String blodFlag = chBoxBLOD.getValue().equals(true) ? "Y" : "N";
			String smsFlag = chBoxSMS.getValue().equals(true) ? "Y" : "N";
			
			if(txtareaComment.getValue().isEmpty() || tfLOC.getValue().isEmpty()) {
				Notification.show("Chưa nhập đủ thông tin", Type.ERROR_MESSAGE);
				return;
			}
			
			try {
//				SmsSysTask smsSysTaskTemp = sysTaskService.findOneByIdtask(gridIdtask);
//				if(smsSysTaskTemp != null) {
//					smsSysTaskTemp.setContenttask(txtareaComment.getValue());
//					smsSysTaskTemp.setBlod(chBoxBLOD.getValue().equals(true) ? "Y" : "N");
//					smsSysTaskTemp.setSms(chBoxSMS.getValue().equals(true) ? "Y" : "N");
//					sysTaskService.save(smsSysTaskTemp);
//				}
//				else 
				{
					SmsSysTask smsSysTask = new SmsSysTask();
					smsSysTask.setUserid(sUserId);
					smsSysTask.setCreatedate(new BigDecimal(timeConverter.getCurrentTime()));
					smsSysTask.setSmsType(smsType);
					smsSysTask.setCardbrn(cardbrn);
					smsSysTask.setSmsMonth(smsMonth);
					smsSysTask.setLoc(new BigDecimal(tfLOC.getValue()));
					smsSysTask.setTypetask(TYPE);
					smsSysTask.setContenttask(txtareaComment.getValue());
					smsSysTask.setBlod(blodFlag);
					smsSysTask.setSms(smsFlag);
					smsSysTask.setStatus(STATUSNEW);
					
					sysTaskService.save(smsSysTask);
					Notification.show("Ghi nhận yêu cầu SMS thành công.", Type.WARNING_MESSAGE);
				}
				
			} catch (Exception e) {
				Notification.show("Lỗi ứng dụng: "+ e.getMessage(), Type.ERROR_MESSAGE);
			}
			txtareaTasknote.setValue("");
//			txtareaTasknoteFull.setValue("");
			lbTaskNoteFull.setValue(htmlTaskNoteFull(""));
			refreshData();
			
		});
		
		
		gridContent = new Grid();
		gridContent.setSizeFull();
		gridContent.setHeightByRows(10);
		gridContent.setHeightMode(HeightMode.ROW);
//		gridContent.setEditorEnabled(true);
		
		containerContent = new IndexedContainer();
		containerContent.addContainerProperty("idtask", String.class, "");
		containerContent.addContainerProperty("userid", String.class, "");
		containerContent.addContainerProperty("createddate", String.class, "");
		containerContent.addContainerProperty("loc", String.class, "");
		containerContent.addContainerProperty("comment", String.class, "");
		containerContent.addContainerProperty("blod", String.class, "");
		containerContent.addContainerProperty("sms", String.class, "");
		containerContent.addContainerProperty("status", String.class, "");
		
		gridContent.setContainerDataSource(containerContent);
		gridContent.getColumn("idtask").setHeaderCaption("CASE NO");
		gridContent.getColumn("idtask").setWidth(90);
		gridContent.getColumn("userid").setHeaderCaption("USER");
		gridContent.getColumn("userid").setWidth(90);
		gridContent.getColumn("createddate").setHeaderCaption("CREATED DATE");
		gridContent.getColumn("createddate").setWidth(154);
		gridContent.getColumn("loc").setHeaderCaption("LOC");
		gridContent.getColumn("loc").setWidth(120);
		gridContent.getColumn("comment").setHeaderCaption("COMMENT");
		gridContent.getColumn("blod").setHeaderCaption("BLOD");
		gridContent.getColumn("blod").setWidth(70);
		gridContent.getColumn("sms").setHeaderCaption("SMS");
		gridContent.getColumn("sms").setWidth(65);
		gridContent.getColumn("status").setHeaderCaption("STATUS");
		gridContent.getColumn("status").setWidth(90);
		gridContent.addItemClickListener(itemEvent -> {
			
			gridIdtask = Long.valueOf(itemEvent.getItem().getItemProperty("idtask").toString());
			SmsSysTask smsSysTask = sysTaskService.findOneByIdtask(gridIdtask);
			tfLOC.setValue(String.valueOf(smsSysTask.getLoc()));
			txtareaComment.setValue(smsSysTask.getContenttask());
			chBoxBLOD.setValue(smsSysTask.getBlod().equals("Y") ? true : false);
			chBoxSMS.setValue(smsSysTask.getSms().equals("Y") ? true : false);
			txtareaTasknote.setValue("");
			if(smsSysTask.getStatus().equals("DONE")) {
				if(smsSysTask.getAsgNote().contains(timeConverter.convertStrToDateTime(smsSysTask.getAsgTms().toString()))) {
//					txtareaTasknoteFull.setValue(smsSysTask.getAsgNote());
					lbTaskNoteFull.setValue(htmlTaskNoteFull(smsSysTask.getAsgNote()));
				} else {
					String asgNote = smsSysTask.getAsgNote()==null ? "" : timeConverter.convertStrToDateTime(smsSysTask.getAsgTms().toString()) + " " + smsSysTask.getAsgUid() + ": " + smsSysTask.getAsgNote();
//					txtareaTasknoteFull.setValue(asgNote);
					lbTaskNoteFull.setValue(htmlTaskNoteFull(asgNote));
				}
			}
			else
//				txtareaTasknoteFull.setValue(smsSysTask.getAsgNote()==null ? "" : smsSysTask.getAsgNote());
				lbTaskNoteFull.setValue(htmlTaskNoteFull(smsSysTask.getAsgNote()==null ? "" : smsSysTask.getAsgNote()));
			
		});
		
		gridContent.setCellStyleGenerator(cell -> {
			String status;
			if (cell.getPropertyId().equals("status")) {
				status = cell.getItem().getItemProperty("status").getValue() == null ? "" : cell.getItem().getItemProperty("status").getValue().toString();
				if(status.equals("NEW")) {
					return "v-align-center-color";
				}
				return "v-align-center";
			}
			return "";
				
		});
		
		refreshData();
		
		cbbSmsMonth.addValueChangeListener(event -> {
			refreshData();
		});
		
		cbbSmsType.addValueChangeListener(event -> {
			refreshData();
		});
		
		cbbCardbrn.addValueChangeListener(event -> {
			refreshData();
		});
		
		txtareaTasknote = new TextArea(TASKNOTE);
		txtareaTasknote.setWidth(650f,Unit.PIXELS);
		txtareaTasknote.setHeight(44f,Unit.PIXELS);

		lbTaskNoteFull = new Label();
		lbTaskNoteFull.setContentMode(ContentMode.HTML);
		
		txtareaTasknoteFull = new TextArea();
		txtareaTasknoteFull.setWidth(880f,Unit.PIXELS);
		txtareaTasknoteFull.setHeight(120f,Unit.PIXELS);
		txtareaTasknoteFull.setEnabled(false);
		txtareaTasknoteFull.setStyleName(ValoTheme.TEXTAREA_BORDERLESS);
		txtareaTasknoteFull.setCaptionAsHtml(true);
		
		btDone.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btDone.setWidth(142f, Unit.PIXELS);
		btDone.addClickListener(event -> {
			if(gridIdtask != null) {
				SmsSysTask smsSysTask = sysTaskService.findOneByIdtask(gridIdtask);
				switch(smsSysTask.getStatus()) {
					case "UPDATE":
						if(!txtareaTasknote.getValue().isEmpty()) {
							smsSysTask.setAsgUid(sUserId);
							smsSysTask.setAsgTms(new BigDecimal(timeConverter.getCurrentTime()));
							String asgNote = smsSysTask.getAsgNote() == null ? timeConverter.convertStrToDateTime(timeConverter.getCurrentTime()) + " " + sUserId + ": " + txtareaTasknote.getValue() : timeConverter.convertStrToDateTime(timeConverter.getCurrentTime()) + " " + sUserId + ": " + txtareaTasknote.getValue() + "\n" + smsSysTask.getAsgNote();
							smsSysTask.setAsgNote(asgNote);
							smsSysTask.setStatus("DONE");
							sysTaskService.save(smsSysTask);
//							txtareaTasknoteFull.setValue(timeConverter.convertStrToDateTime(smsSysTask.getAsgTms().toString()) + " " + smsSysTask.getAsgUid() + ": " + smsSysTask.getAsgNote());
							lbTaskNoteFull.setValue(htmlTaskNoteFull(timeConverter.convertStrToDateTime(smsSysTask.getAsgTms().toString()) + " " + smsSysTask.getAsgUid() + ": " + smsSysTask.getAsgNote()));
							txtareaTasknote.setValue("");
							refreshData();
						} else {
							Notification.show("Không thể hoàn tất phản hồi do chưa thêm nội dung", Type.ERROR_MESSAGE);
						}
						break;
					case "NEW":
						if(!txtareaTasknote.getValue().isEmpty()) {
							smsSysTask.setAsgUid(sUserId);
							smsSysTask.setAsgTms(new BigDecimal(timeConverter.getCurrentTime()));
							smsSysTask.setAsgNote(txtareaTasknote.getValue());
							smsSysTask.setStatus("DONE");
							sysTaskService.save(smsSysTask);
//							txtareaTasknoteFull.setValue(timeConverter.convertStrToDateTime(smsSysTask.getAsgTms().toString()) + " " + smsSysTask.getAsgUid() + ": " + smsSysTask.getAsgNote());
							lbTaskNoteFull.setValue(htmlTaskNoteFull(timeConverter.convertStrToDateTime(smsSysTask.getAsgTms().toString()) + " " + smsSysTask.getAsgUid() + ": " + smsSysTask.getAsgNote()));
							txtareaTasknote.setValue("");
							refreshData();
						} else {
							Notification.show("Không thể hoàn tất phản hồi do chưa thêm nội dung", Type.ERROR_MESSAGE);
						}
						break;
					default:
						break;
				}
				
			}
			
		});
		
		btUpdate.setStyleName(ValoTheme.BUTTON_DANGER);
		btUpdate.setWidth(142f, Unit.PIXELS);
		btUpdate.addClickListener(event -> {
			if(gridIdtask != null) {
				SmsSysTask smsSysTask = sysTaskService.findOneByIdtask(gridIdtask);
				if(!smsSysTask.getStatus().equals("DONE")) {
					if(!txtareaTasknote.getValue().isEmpty()) {
						smsSysTask.setAsgUid(sUserId);
						smsSysTask.setAsgTms(new BigDecimal(timeConverter.getCurrentTime()));
						String asgNote = smsSysTask.getAsgNote() == null ? timeConverter.convertStrToDateTime(timeConverter.getCurrentTime()) + " " + sUserId + ": " + txtareaTasknote.getValue() : timeConverter.convertStrToDateTime(timeConverter.getCurrentTime()) + " " + sUserId + ": " + txtareaTasknote.getValue() + "\n" + smsSysTask.getAsgNote();
						smsSysTask.setAsgNote(asgNote);
						smsSysTask.setStatus("UPDATE");
						sysTaskService.save(smsSysTask);
//						txtareaTasknoteFull.setValue(smsSysTask.getAsgNote());
						lbTaskNoteFull.setValue(htmlTaskNoteFull(smsSysTask.getAsgNote()));
						txtareaTasknote.setValue("");
						refreshData();
					} else {
						Notification.show("Không thể cập nhật phản hồi do chưa thêm nội dung", Type.ERROR_MESSAGE);
					}
					
				}
				else {
					Notification.show("Không thể cập nhật phản hồi cho case đã hoàn thành", Type.ERROR_MESSAGE);
				}
			}
			
		});
		
		switch(CheckUserId) {
			case "1":
				txtareaTasknote.setVisible(false);
				btDone.setVisible(false);
				btUpdate.setVisible(false);
				break;
			case "2":
				txtareaTasknote.setVisible(true);
				btDone.setVisible(true);
				btUpdate.setVisible(true);
				break;
			case "4":
				txtareaTasknote.setVisible(true);
				btDone.setVisible(true);
				btUpdate.setVisible(true);
				break;
		}
		
		
		final FormLayout form = new FormLayout();
		form.setMargin(new MarginInfo(true, false, true, true));
		
		final HorizontalLayout hBodyLayout1 = new HorizontalLayout();
		hBodyLayout1.setSpacing(true);
		
		final HorizontalLayout hBodyLayout2 = new HorizontalLayout();
		hBodyLayout2.setSpacing(true);
		
		final FormLayout formStatus = new FormLayout();
		formStatus.setMargin(new MarginInfo(true, false, false, true));
		
		final VerticalLayout responseLayout = new VerticalLayout();
//		responseLayout.setSpacing(true);
		responseLayout.setMargin(new MarginInfo(false, true, true, true));
		
		final HorizontalLayout hBodyLayout3 = new HorizontalLayout();
		hBodyLayout3.setSpacing(true);
		
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		final VerticalLayout buttonStatusLayout = new VerticalLayout();
		buttonStatusLayout.setSizeFull();
		
		panelResponse.setHeight(300f, Unit.PIXELS);
		
		hBodyLayout1.addComponent(lbSmsType);
		hBodyLayout1.addComponent(cbbSmsType);
		hBodyLayout1.addComponent(lbCardbrn);
		hBodyLayout1.addComponent(cbbCardbrn);
		hBodyLayout1.addComponent(lbSmsMonth);
		hBodyLayout1.addComponent(cbbSmsMonth);
		hBodyLayout2.addComponent(lbLoc);
		hBodyLayout2.addComponent(tfLOC);
		hBodyLayout2.addComponent(lbComment);
		hBodyLayout2.addComponent(txtareaComment);
		hBodyLayout2.addComponent(chBoxBLOD);
		hBodyLayout2.addComponent(chBoxSMS);
		hBodyLayout2.addComponent(btSubmit);
		
//		hBodyLayout3.addComponent(lbTasknote);
		hBodyLayout3.addComponent(txtareaTasknote);
		buttonStatusLayout.addComponent(btDone);
		buttonStatusLayout.addComponent(btUpdate);
		hBodyLayout3.addComponent(buttonStatusLayout);
		responseLayout.addComponent(hBodyLayout3);
		responseLayout.addComponent(lbTaskNoteFull);
		
		hBodyLayout1.setComponentAlignment(lbSmsType, Alignment.MIDDLE_CENTER);
		hBodyLayout1.setComponentAlignment(lbCardbrn, Alignment.MIDDLE_CENTER);
		hBodyLayout1.setComponentAlignment(lbSmsMonth, Alignment.MIDDLE_CENTER);
		hBodyLayout2.setComponentAlignment(lbLoc, Alignment.TOP_CENTER);
		hBodyLayout2.setComponentAlignment(tfLOC, Alignment.TOP_CENTER);
		hBodyLayout2.setComponentAlignment(lbComment, Alignment.TOP_CENTER);
		hBodyLayout2.setComponentAlignment(chBoxBLOD, Alignment.TOP_CENTER);
		hBodyLayout2.setComponentAlignment(chBoxSMS, Alignment.TOP_CENTER);
		hBodyLayout2.setComponentAlignment(btSubmit, Alignment.TOP_CENTER);
//		hBodyLayout3.setComponentAlignment(lbTasknote, Alignment.TOP_CENTER);
		hBodyLayout3.setComponentAlignment(txtareaTasknote, Alignment.MIDDLE_CENTER);
		
		form.addComponent(hBodyLayout1);
		form.addComponent(hBodyLayout2);
		
		formStatus.addComponent(responseLayout);
		panelResponse.setContent(formStatus);
		
		mainLayout.addComponent(form);
		mainLayout.addComponent(gridContent);
		mainLayout.addComponent(panelResponse);
		setCompositionRoot(mainLayout);
		
		
	}
	
	@Override
	public void eventReload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventReloadAuto() {
		// TODO Auto-generated method stub
		
	}
	
	private void refreshData() {
		String smsType = cbbSmsType.getValue() == null ? "":cbbSmsType.getValue().toString();
		String cardbrn =  cbbCardbrn.getValue() == null ? "" : cbbCardbrn.getValue().toString();
		String smsMonth = cbbSmsMonth.getValue() == null ? "" : cbbSmsMonth.getValue().toString();
		
		Iterable<SmsSysTask> listSysTaskIter = null;
				
		switch(CheckUserId) {
			case "1":
				listSysTaskIter = sysTaskService.findAllBySmsTypeAndCardbrnAndSmsMonthAndUserId(smsType, cardbrn, smsMonth, sUserId);
				break;
			case "2":
				listSysTaskIter = sysTaskService.findAllBySmsTypeAndCardbrnAndSmsMonthByNotStatusOrAsgUid(smsType, cardbrn, smsMonth, "DONE", sUserId);
				break;
			case "4":
				listSysTaskIter = sysTaskService.findAllBySmsTypeAndCardbrnAndSmsMonth(smsType, cardbrn, smsMonth);
				break;
		}
		
		List<SmsSysTask> smsSysTaskList = StreamSupport.stream(listSysTaskIter.spliterator(), false).sorted(Comparator.comparing(SmsSysTask::getCreatedate).reversed()).collect(Collectors.toList());
		
		if (!smsSysTaskList.isEmpty()) {
			if (!containerContent.getItemIds().isEmpty()) {
				containerContent.removeAllItems();
			}
			for (int i = 0; i <= smsSysTaskList.size() - 1; i++) {
				Item item = containerContent.getItem(containerContent.addItem());
				item.getItemProperty("idtask").setValue(String.valueOf(smsSysTaskList.get(i).getIdtask()));
				item.getItemProperty("userid").setValue(smsSysTaskList.get(i).getUserid() != null ? smsSysTaskList.get(i).getUserid().toString() : "");
				item.getItemProperty("createddate").setValue(smsSysTaskList.get(i).getCreatedate() != null ? timeConverter.convertStrToDateTime(smsSysTaskList.get(i).getCreatedate().toString()) : "");
				item.getItemProperty("loc").setValue(smsSysTaskList.get(i).getLoc() != null ? smsSysTaskList.get(i).getLoc().toString() : "");
				item.getItemProperty("comment").setValue(smsSysTaskList.get(i).getContenttask() != null ? smsSysTaskList.get(i).getContenttask().toString() : "");
				item.getItemProperty("blod").setValue(smsSysTaskList.get(i).getBlod() != null ? smsSysTaskList.get(i).getBlod().toString() : "");
				item.getItemProperty("sms").setValue(smsSysTaskList.get(i).getSms() != null ? smsSysTaskList.get(i).getSms().toString() : "");
				item.getItemProperty("status").setValue(smsSysTaskList.get(i).getStatus() != null ? smsSysTaskList.get(i).getStatus().toString() : "");
			}
		}
		else
			containerContent.removeAllItems();
	}
	
	private String htmlTaskNoteFull(String content) {
		return "<p><font color='blue'>" + content.replace("\n", "</br>") + "</font></p>";
	}
	

}
