package com.sms.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;

import com.sms.ReloadAutoComponent;
import com.sms.ReloadComponent;
import com.sms.SecurityUtils;
import com.sms.SpringConfigurationValueHelper;
import com.sms.SpringContextHelper;
import com.sms.TimeConverter;
import com.sms.services.DescriptionService;
import com.sms.services.SmsStatementService;
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
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
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
import com.sms.entities.*;

/**
 * tanvh1 Aug 20, 2019
 */
@SpringComponent
@Scope("prototype")
public class SendSmsStatement extends CustomComponent implements ReloadAutoComponent, ReloadComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(SendSmsStatement.class);

	private SpringConfigurationValueHelper configurationHelper;
	public static final String CAPTION = "SMS THÔNG BÁO SAO KÊ";
	private static final String STATEMENTMONTH = "STATEMENT MONTH";
	private static final String SMSTYPE = "SMS TYPE";
	private static final String CARDBRN = "CARD BRN";
	private static final String SENDSMS = "SEND SMS";
	private static final String INSERT = "INSERT";
	private static final String EXCEL = "XLSX";
	public static final String IDALERT = "MASTER_CARD_ALERT";
	private static final String SMSTYPE_STATEMENT = "DEBT01";

	public final transient FormLayout formLayout = new FormLayout();

	private final SysUserroleService sysUserroleService;
	private SmsStatementService smsStatementService;

	public final transient ComboBox cbbCardbrn;
	public final transient ComboBox cbbStatementMonth;
	public final transient TextField tfSmsType;
	final Button btSendSms = new Button(SENDSMS);
	final Button btInsert = new Button(INSERT);
	final Button btXLSXExport = new Button(EXCEL);
	private transient String sUserId;
	private String CheckUserId = "";
	final TimeConverter timeConverter = new TimeConverter();
	List<SmsStatement> smsStatementList = new ArrayList<SmsStatement>();
	private int rowNumExport = 0;
	private String fileNameOutput = "";
	private Path pathExport = null;

	public SendSmsStatement() {
		// TAN 20190815
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		configurationHelper = (SpringConfigurationValueHelper) helper.getBean("springConfigurationValueHelper");
		final DescriptionService descService = (DescriptionService) helper.getBean("descriptionService");
		sysUserroleService = (SysUserroleService) helper.getBean("sysUserroleService");
		smsStatementService = (SmsStatementService) helper.getBean("smsStatementService");

		this.sUserId = SecurityUtils.getUserId();
		CheckUserId = sysUserroleService.findByRoleId(sUserId);

		cbbCardbrn = new ComboBox(CARDBRN);
		cbbCardbrn.setNullSelectionAllowed(false);
		cbbCardbrn.addItems("MC", "VS");
		cbbCardbrn.setValue("MC");

		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		cbbStatementMonth = new ComboBox(STATEMENTMONTH);
		cbbStatementMonth.setNullSelectionAllowed(false);
		cbbStatementMonth.setPageLength(12);
		cbbStatementMonth.setDescription("Format: YYYYMM");
		descService.findAllByTypeByOrderBySequencenoDesc("KYSAOKE").forEach(item -> {
			cbbStatementMonth.addItem(item.getId());
			cbbStatementMonth.setItemCaption(item.getId(), item.getDescription());
		});
		SimpleDateFormat formatterStatementMonth = new SimpleDateFormat("yyyyMM");
		String cbbStatementMonthdefault = formatterStatementMonth.format(cal.getTime());
		cbbStatementMonth.setValue(cbbStatementMonthdefault);

		tfSmsType = new TextField(SMSTYPE);
		tfSmsType.setValue(SMSTYPE_STATEMENT);
		tfSmsType.setEnabled(false);

		final FormLayout form = new FormLayout();
		form.setMargin(new MarginInfo(true, false, true, true));

		btSendSms.setStyleName(ValoTheme.BUTTON_DANGER);
		btSendSms.setWidth(120.0f, Unit.PIXELS);
		btSendSms.setIcon(FontAwesome.SEND);
		btSendSms.setDescription("Lấy thông tin từ bảng SMS_NHAC_NO gửi qua bên DB EB");

		btInsert.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btInsert.setWidth(120.0f, Unit.PIXELS);
		btInsert.setDescription("Call Procedure GET_SMS_NHAC_NO_SAO_KE để Insert dữ liệu xuống bảng SMS_NHAC_NO");

		btXLSXExport.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		btXLSXExport.setWidth(120.0f, Unit.PIXELS);
		btXLSXExport.setIcon(FontAwesome.DOWNLOAD);
		btXLSXExport.setDescription("Xuất thông tin từ Procedure hoặc table SMS_NHAC_NO");

		btInsert.addClickListener(event -> {
			form.setEnabled(false);
			String statementMonth = cbbStatementMonth.getValue().toString();
			String crdbrn = cbbCardbrn.getValue().toString();

			try {
				smsStatementList = smsStatementService.getDataSMSThongBaoSaoKe(statementMonth, crdbrn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.error(e.getMessage());
			}

			Window confirmDialog = new Window();
			final FormLayout content = new FormLayout();
			content.setMargin(true);

			final Button bOK = new Button("OK");
			bOK.setStyleName(ValoTheme.BUTTON_SMALL);
			final Button bCancel = new Button("Cancel");
			bCancel.setStyleName(ValoTheme.BUTTON_SMALL);

			confirmDialog.setCaption("Số lượng SMS import là: " + smsStatementList.size());
			confirmDialog.setWidth(250.0f, Unit.PIXELS);

			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					bOK.addClickListener(event -> {
						for (SmsStatement item : smsStatementList) {
							if (item.getTotalBalIpp().equals(null) || item.getTotalBalIpp().equals("")) {
								item.setTotalBalIpp("0");
							}

							String message = CreateSMSMessage(item.getCardNo(), item.getSmsMonth(), item.getClosingBalance(),
									item.getMinimumPayment(), item.getDueDate(), item.getTotalBalIpp(), item.getCardBrn(), item.getVip(),
									item.getCifVip(), item.getIdStatement());
							if (!StringUtils.isEmpty(message)) {
								item.setSmsDetail(message);
								double ipp = 0;
								if (item.getTotalBalIpp() != "")
									ipp = Double.parseDouble(item.getTotalBalIpp());

								// closing balance >= 0 hoac closing balance <= -100,000 (du co hon 100k) hoac ipp > 0
								Boolean isIdStatement = item.getIdStatement() != null && !item.getIdStatement().equals("") ? true : false;
								if (Double.parseDouble(item.getClosingBalance()) >= 0
										|| (Double.parseDouble(item.getClosingBalance()) <= -100000 || isIdStatement) || ipp > 0) {
									if (item.getDesMobile().equals("khong co")) // ko co so dien thoai
									{
										item.setActionType("D"); // D = ko gui qua ebanking
										smsStatementService.save(item);
									} else {
										item.setActionType("N"); // N = se gui qua ebanking
										smsStatementService.save(item);
									}
								} else // 0 > closing > -100000 and don't have IPP, don't send
								{
									item.setActionType("D"); // D = ko gui qua ebanking
									smsStatementService.save(item);
								}
							} else {
								LOGGER.error("LOC " + item.getLoc() + " can not create message. Please check again");
							}

						}
						confirmDialog.close();
						form.setEnabled(true);
					});

					bCancel.addClickListener(event -> {
						confirmDialog.close();
						form.setEnabled(true);
					});
				}
			});

			VerticalLayout layoutConfirmBtn = new VerticalLayout();
			HorizontalLayout layoutBtn = new HorizontalLayout();
			layoutBtn.setSpacing(true);
			layoutBtn.addComponents(bOK);
			layoutBtn.addComponents(bCancel);
			layoutBtn.setComponentAlignment(bOK, Alignment.BOTTOM_CENTER);
			layoutBtn.setComponentAlignment(bCancel, Alignment.BOTTOM_CENTER);
			layoutConfirmBtn.addComponent(layoutBtn);
			layoutConfirmBtn.setComponentAlignment(layoutBtn, Alignment.BOTTOM_CENTER);
			content.addComponent(layoutConfirmBtn);

			confirmDialog.setContent(content);

			getUI().addWindow(confirmDialog);

			// Center it in the browser window
			confirmDialog.center();
			confirmDialog.setResizable(false);

		});

		btSendSms.addClickListener(event -> {
			form.setEnabled(false);
			String smsMonth = cbbStatementMonth.getValue().toString();
			String cardBrn = cbbCardbrn.getValue().toString();
			List<SmsStatement> smsStatementList = smsStatementService.findAllByActionTypeAndSmsMonthAndCardBrnAndSmsType("N", smsMonth, cardBrn,
					SMSTYPE_STATEMENT);

			Window confirmDialog = new Window();
			final FormLayout content = new FormLayout();
			content.setMargin(true);

			Button bOK = new Button("OK");
			bOK.setStyleName(ValoTheme.BUTTON_SMALL);
			final Button bCancel = new Button("Cancel");
			bCancel.setStyleName(ValoTheme.BUTTON_SMALL);

			confirmDialog.setCaption("Số lượng SMS import là: " + smsStatementList.size());
			confirmDialog.setWidth(250.0f, Unit.PIXELS);

			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					bOK.addClickListener(event -> {
						for (SmsStatement item : smsStatementList) {
							int result = 0;
							try {
								result = smsStatementService.InsertSMSMessateToEBankGW(IDALERT, item.getDesMobile(), item.getSmsDetail(), "N",
										SMSTYPE_STATEMENT);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								LOGGER.error(e.getMessage());
							}

							if (result == 1) {
								item.setActionType("Y");
								item.setInsertTransDateTime(timeConverter.getCurrentTime());
								smsStatementService.save(item);
								LOGGER.info("id: " + item.getId() + ", card brn: " + item.getCardBrn() + ", Month: " + item.getSmsMonth() + ", Loc: "
										+ item.getLoc() + " is successful");
							} else {
								item.setActionType("F");
								item.setInsertTransDateTime(timeConverter.getCurrentTime());
								smsStatementService.save(item);
								LOGGER.error("id: " + item.getId() + ", card brn: " + item.getCardBrn() + ", Month: " + item.getSmsMonth() + ", Loc: "
										+ item.getLoc() + " is failed");
							}
						}
						confirmDialog.close();
						form.setEnabled(true);
					});

					bCancel.addClickListener(event -> {
						confirmDialog.close();
						form.setEnabled(true);
					});
				}
			});
			VerticalLayout layoutConfirmBtn = new VerticalLayout();
			HorizontalLayout layoutBtn = new HorizontalLayout();
			layoutBtn.setSpacing(true);
			layoutBtn.addComponents(bOK);
			layoutBtn.addComponents(bCancel);
			layoutBtn.setComponentAlignment(bOK, Alignment.BOTTOM_CENTER);
			layoutBtn.setComponentAlignment(bCancel, Alignment.BOTTOM_CENTER);
			layoutConfirmBtn.addComponent(layoutBtn);
			layoutConfirmBtn.setComponentAlignment(layoutBtn, Alignment.BOTTOM_CENTER);
			content.addComponent(layoutConfirmBtn);

			confirmDialog.setContent(content);

			getUI().addWindow(confirmDialog);

			// Center it in the browser window
			confirmDialog.center();
			confirmDialog.setResizable(false);

		});

		btXLSXExport.addClickListener(event -> {
			form.setEnabled(false);
			Window confirmDialog = new Window();
			final FormLayout content = new FormLayout();
			content.setMargin(true);

			final Button bProcedure = new Button("From Procedure");
			bProcedure.setStyleName(ValoTheme.BUTTON_SMALL);
			bProcedure.setDescription("Export excel từ procedure GET_SMS_NHAC_NO_SAO_KE");

			final Button bTable = new Button("From SMS_NHAC_NO");
			bTable.setStyleName(ValoTheme.BUTTON_SMALL);

			confirmDialog.setCaption("Export SMS sao kê");
			confirmDialog.setWidth(350.0f, Unit.PIXELS);

			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					String statementMonth = cbbStatementMonth.getValue().toString();
					String crdbrn = cbbCardbrn.getValue().toString();

					bProcedure.addClickListener(event -> {
						try {
							smsStatementList = smsStatementService.getDataSMSThongBaoSaoKe(statementMonth, crdbrn);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						for (SmsStatement item : smsStatementList) {
							if (item.getTotalBalIpp().equals(null) || item.getTotalBalIpp().equals("")) {
								item.setTotalBalIpp("0");
							}

							String message = CreateSMSMessage(item.getCardNo(), item.getSmsMonth(), item.getClosingBalance(),
									item.getMinimumPayment(), item.getDueDate(), item.getTotalBalIpp(), item.getCardBrn(), item.getVip(),
									item.getCifVip(), item.getIdStatement());
							if (!StringUtils.isEmpty(message)) {
								item.setSmsDetail(message);
								double ipp = 0;
								if (item.getTotalBalIpp() != "")
									ipp = Double.parseDouble(item.getTotalBalIpp());

								// closing balance >= 0 hoac closing balance <= -100,000 (du co hon 100k) hoac ipp > 0
								Boolean isIdStatement = item.getIdStatement() != null && !item.getIdStatement().equals("") ? true : false;
								if (Double.parseDouble(item.getClosingBalance()) >= 0
										|| (Double.parseDouble(item.getClosingBalance()) <= -100000 || isIdStatement) || ipp > 0) {
									if (item.getDesMobile().equals("khong co")) // ko co so dien thoai
									{
										item.setActionType("D"); // D = ko gui qua ebanking
									} else {
										item.setActionType("N"); // N = se gui qua ebanking
									}
								} else // 0 > closing > -100000 and don't have IPP, don't send
								{
									item.setActionType("D"); // D = ko gui qua ebanking
								}
							} else {
								LOGGER.error("LOC " + item.getLoc() + " can not create message. Please check again");
							}

						}

						// EXPORT LIST TO EXCEL FILE
						XSSFWorkbook workbookExport = new XSSFWorkbook();
						XSSFSheet sheetExport = workbookExport.createSheet("SMS_STATEMENT");

						DataFormat format = workbookExport.createDataFormat();
						CellStyle styleNumber;
						styleNumber = workbookExport.createCellStyle();
						styleNumber.setDataFormat(format.getFormat("0.0"));

						rowNumExport = 0;
						LOGGER.info("Creating excel");

						if (rowNumExport == 0) {
							Object[] rowHeader = { "ID", "SMS_TYPE", "SMS_DETAIL", "DEST_MOBILE", "GET_TRANS_DATETIME", "INSERT_TRANS_DATETIME",
									"PAN", "CARD_BRN", "CARD_TYPE", "SMS_MONTH", "CLOSING_BALANCE", "DUE_DATE", "MINIMUM_PAYMENT", "ACTION_TYPE",
									"TOL_BAL_IPP", "VIP", "CIF_VIP", "CARD_NO", "LOC", "ID_STATEMENT" };
							int colNum = 0;
							XSSFRow row = sheetExport.createRow(rowNumExport++);
							for (Object field : rowHeader) {
								Cell cell = row.createCell(colNum++, CellType.STRING);
								cell.setCellValue((String) field);
							}
							LOGGER.info("Created row " + rowNumExport + " for header sheet in excel.");
						}

						for (SmsStatement item : smsStatementList) {
							XSSFRow row = sheetExport.createRow(rowNumExport++);

							row.createCell(0).setCellValue(item.getId());
							row.createCell(1).setCellValue(item.getSmsType());
							row.createCell(2).setCellValue(item.getSmsDetail());
							row.createCell(3).setCellValue(item.getDesMobile());
							row.createCell(4).setCellValue(item.getDateTime());
							row.createCell(5).setCellValue(item.getInsertTransDateTime());
							row.createCell(6).setCellValue(item.getPan());
							row.createCell(7).setCellValue(item.getCardBrn());
							row.createCell(8).setCellValue(item.getCardType());
							row.createCell(9).setCellValue(item.getSmsMonth());
							row.createCell(10, CellType.NUMERIC).setCellValue(Long.parseLong(item.getClosingBalance()));
							row.createCell(11).setCellValue(item.getDueDate());
							row.createCell(12, CellType.NUMERIC).setCellValue(Long.parseLong(item.getMinimumPayment()));
							row.createCell(13).setCellValue(item.getActionType());
							row.createCell(14, CellType.NUMERIC).setCellValue(Long.parseLong(item.getTotalBalIpp()));
							row.createCell(15).setCellValue(item.getVip());
							row.createCell(16).setCellValue(item.getCifVip());
							row.createCell(17).setCellValue(item.getCardNo());
							row.createCell(18).setCellValue(item.getLoc());
							row.createCell(19).setCellValue(item.getIdStatement());
						}

						try {

							fileNameOutput = "SMS_STATEMENT_" + cbbCardbrn.getValue() + "_" + cbbStatementMonth.getValue() + ".xlsx";
							pathExport = Paths.get(configurationHelper.getPathFileRoot() + "\\Export");
							if (Files.notExists(pathExport)) {
								Files.createDirectories(pathExport);
							}
							FileOutputStream outputStream = new FileOutputStream(pathExport + "\\" + fileNameOutput);
							LOGGER.info("Created file excel output " + fileNameOutput);
							workbookExport.write(outputStream);
							LOGGER.info("Write data to " + fileNameOutput + " completed");
							workbookExport.close();
							outputStream.close();
							LOGGER.info("Done");
							LOGGER.info("Export excel file " + fileNameOutput);
							messageExportXLSX("Info", "Export compeleted.");
							form.setEnabled(true);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							LOGGER.error(e.toString());
						} catch (IOException e) {
							e.printStackTrace();
							LOGGER.error(e.toString());
						}

						confirmDialog.close();
						form.setEnabled(true);
					});

					bTable.addClickListener(event -> {
						smsStatementList = smsStatementService.findAllBySmsMonthAndCardBrnAndSmsType(statementMonth, crdbrn, SMSTYPE_STATEMENT);
						// EXPORT LIST TO EXCEL FILE
						XSSFWorkbook workbookExport = new XSSFWorkbook();
						XSSFSheet sheetExport = workbookExport.createSheet("SMS_STATEMENT");

						DataFormat format = workbookExport.createDataFormat();
						CellStyle styleNumber;
						styleNumber = workbookExport.createCellStyle();
						styleNumber.setDataFormat(format.getFormat("0.0"));

						rowNumExport = 0;
						LOGGER.info("Creating excel");

						if (rowNumExport == 0) {
							Object[] rowHeader = { "ID", "SMS_TYPE", "SMS_DETAIL", "DEST_MOBILE", "GET_TRANS_DATETIME", "INSERT_TRANS_DATETIME",
									"PAN", "CARD_BRN", "CARD_TYPE", "SMS_MONTH", "CLOSING_BALANCE", "DUE_DATE", "MINIMUM_PAYMENT", "ACTION_TYPE",
									"TOL_BAL_IPP", "VIP", "CIF_VIP", "CARD_NO", "LOC", "ID_STATEMENT" };
							int colNum = 0;
							XSSFRow row = sheetExport.createRow(rowNumExport++);
							for (Object field : rowHeader) {
								Cell cell = row.createCell(colNum++, CellType.STRING);
								cell.setCellValue((String) field);
							}
							LOGGER.info("Created row " + rowNumExport + " for header sheet in excel.");
						}

						for (SmsStatement item : smsStatementList) {
							XSSFRow row = sheetExport.createRow(rowNumExport++);

							row.createCell(0).setCellValue(item.getId());
							row.createCell(1).setCellValue(item.getSmsType());
							row.createCell(2).setCellValue(item.getSmsDetail());
							row.createCell(3).setCellValue(item.getDesMobile());
							row.createCell(4).setCellValue(item.getDateTime());
							row.createCell(5).setCellValue(item.getInsertTransDateTime());
							row.createCell(6).setCellValue(item.getPan());
							row.createCell(7).setCellValue(item.getCardBrn());
							row.createCell(8).setCellValue(item.getCardType());
							row.createCell(9).setCellValue(item.getSmsMonth());
							row.createCell(10, CellType.NUMERIC).setCellValue(Long.parseLong(item.getClosingBalance()));
							row.createCell(11).setCellValue(item.getDueDate());
							row.createCell(12, CellType.NUMERIC).setCellValue(Long.parseLong(item.getMinimumPayment()));
							row.createCell(13).setCellValue(item.getActionType());
							row.createCell(14, CellType.NUMERIC).setCellValue(Long.parseLong(item.getTotalBalIpp()));
							row.createCell(15).setCellValue(item.getVip());
							row.createCell(16).setCellValue(item.getCifVip());
							row.createCell(17).setCellValue(item.getCardNo());
							row.createCell(18).setCellValue(item.getLoc());
							row.createCell(19).setCellValue(item.getIdStatement());
						}

						try {

							fileNameOutput = "SMS_STATEMENT_" + cbbCardbrn.getValue() + "_" + cbbStatementMonth.getValue() + ".xlsx";
							pathExport = Paths.get(configurationHelper.getPathFileRoot() + "\\Export");
							if (Files.notExists(pathExport)) {
								Files.createDirectories(pathExport);
							}
							FileOutputStream outputStream = new FileOutputStream(pathExport + "\\" + fileNameOutput);
							LOGGER.info("Created file excel output " + fileNameOutput);
							workbookExport.write(outputStream);
							LOGGER.info("Write data to " + fileNameOutput + " completed");
							workbookExport.close();
							outputStream.close();
							LOGGER.info("Done");
							LOGGER.info("Export excel file " + fileNameOutput);
							messageExportXLSX("Info", "Export compeleted.");
							form.setEnabled(true);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							LOGGER.error(e.toString());
						} catch (IOException e) {
							e.printStackTrace();
							LOGGER.error(e.toString());
						}

						confirmDialog.close();
						form.setEnabled(true);
					});

					confirmDialog.addCloseListener(event -> {
						form.setEnabled(true);
					});

				}
			});

			VerticalLayout layoutConfirmBtn = new VerticalLayout();
			HorizontalLayout layoutBtn = new HorizontalLayout();
			layoutBtn.setSpacing(true);
			layoutBtn.addComponents(bProcedure);
			layoutBtn.addComponents(bTable);
			layoutBtn.setComponentAlignment(bProcedure, Alignment.BOTTOM_CENTER);
			layoutBtn.setComponentAlignment(bTable, Alignment.BOTTOM_CENTER);
			layoutConfirmBtn.addComponent(layoutBtn);
			layoutConfirmBtn.setComponentAlignment(layoutBtn, Alignment.BOTTOM_CENTER);
			content.addComponent(layoutConfirmBtn);

			confirmDialog.setContent(content);

			getUI().addWindow(confirmDialog);

			// Center it in the browser window
			confirmDialog.center();
			confirmDialog.setResizable(false);

		});

		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();

		form.addComponent(cbbCardbrn);
		form.addComponent(cbbStatementMonth);
		form.addComponent(tfSmsType);
		form.addComponent(btInsert);
		form.addComponent(btSendSms);
		form.addComponent(btXLSXExport);

		mainLayout.addComponent(form);
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
	}

	private String CreateSMSMessage(String p_pan, String p_settleDay, String p_closingBal, String p_miniPay, String p_dueDay, String p_tol_bal_ipp,
			String p_crd_brn, String p_vip_card, String p_vip_cif, String p_ID_statement) {
		try {
			String SCBPhone = "";
			String last4Digits = p_pan.substring(12, 16); // cat 4 so duoi the
			String settleDay = p_settleDay.substring(4, 6) + "/" + p_settleDay.substring(2, 4);
			String dueDay = p_dueDay.substring(6, 8) + "/" + p_dueDay.substring(4, 6);

			double clsBal = Double.parseDouble(p_closingBal);
			double mini = Double.parseDouble(p_miniPay);

			Locale locale = new Locale("vi", "VN");
			NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(locale);

			String sCloBal = moneyFormat.format(clsBal).replace(" đ", "");
			String sMiniPayment = moneyFormat.format(mini).replace(" đ", "");

			String smsMessage = "";

			if (p_vip_card.equals("Y") || p_vip_cif.equals("Y"))
				SCBPhone = "1800545438";
			else
				SCBPhone = "19006538";

			if (clsBal < 0) {
				double clsBal_1 = -1 * clsBal;
				String cloBal_2 = moneyFormat.format(clsBal_1).replace(" đ", "");
				if (p_crd_brn.equals("VS"))
					smsMessage = "Cam on Quy khach da su dung the SCB " + last4Digits + "\nSo DU CO trong the den 15/" + settleDay + ":" + cloBal_2
							+ "VND";
				else // MC
					smsMessage = "Cam on Quy khach da su dung the SCB " + last4Digits + "\nSo DU CO trong the den 25/" + settleDay + ":" + cloBal_2
							+ "VND";

			} else {
				if (clsBal == 0) {
					if (p_crd_brn.equals("VS"))
						smsMessage = "Cam on Quy khach da su dung va thanh toan the SCB " + last4Digits + "\nDu no den 15/" + settleDay + ": 0VND.";
					else // MC
						smsMessage = "Cam on Quy khach da su dung va thanh toan the SCB " + last4Digits + "\nDu no den 25/" + settleDay + ": 0VND.";
				} else // clsBal > 0
				{
					if (p_crd_brn.equals("VS"))
						smsMessage = "Sao ke the " + last4Digits + "\nDu no den 15/" + settleDay + ": " + sCloBal + "VND\nTT toi thieu "
								+ sMiniPayment + "VND\nHan TT " + dueDay;
					else // MC
						smsMessage = "Sao ke the " + last4Digits + "\nDu no den 25/" + settleDay + ": " + sCloBal + "VND\nTT toi thieu "
								+ sMiniPayment + "VND\nHan TT " + dueDay;
				}
			}

			if (p_ID_statement != null && !p_ID_statement.trim().equals(""))
				smsMessage = smsMessage + "\nChi tiet: https://card.scb.com.vn/skt/skt.html?id=" + p_ID_statement;

			return smsMessage;

		} catch (Exception ex) {
			LOGGER.error("Error CreateSMSMessage(), " + ex.getMessage());
			return "";
		}
	}

	private void messageExportXLSX(String caption, String text) {
		Window confirmDialog = new Window();
		FormLayout content = new FormLayout();
		content.setMargin(true);
		Button bOK = new Button("OK");
		Label lbText = new Label(text);
		confirmDialog.setCaption(caption);
		confirmDialog.setWidth(300.0f, Unit.PIXELS);

		bOK.addClickListener(event -> {
			SimpleFileDownloader downloader = new SimpleFileDownloader();
			addExtension(downloader);
			StreamResource resource = getStream(new File(pathExport + "\\" + fileNameOutput));
			downloader.setFileDownloadResource(resource);
			downloader.download();
			confirmDialog.close();
		});

		VerticalLayout layoutBtn = new VerticalLayout();
		layoutBtn.setSpacing(true);
		layoutBtn.addComponent(lbText);
		layoutBtn.addComponents(bOK);
		layoutBtn.setComponentAlignment(lbText, Alignment.MIDDLE_CENTER);
		layoutBtn.setComponentAlignment(bOK, Alignment.BOTTOM_CENTER);
		content.addComponent(layoutBtn);

		confirmDialog.setContent(content);

		getUI().addWindow(confirmDialog);
		// Center it in the browser window
		confirmDialog.center();
		confirmDialog.setResizable(false);
	}

	private StreamResource getStream(File inputfile) {

		StreamResource.StreamSource source = new StreamResource.StreamSource() {

			public InputStream getStream() {

				InputStream input = null;
				try {
					input = new FileInputStream(inputfile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return input;

			}
		};
		StreamResource resource = new StreamResource(source, inputfile.getName());
		return resource;
	}

}
