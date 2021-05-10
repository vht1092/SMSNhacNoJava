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
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional.TxType;

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
import com.sms.entities.SmsStatement;
import com.sms.services.DescriptionService;
import com.sms.services.SmsStatementService;
import com.sms.services.SysUserroleService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * tanvh1 Aug 20, 2019
 */
@SpringComponent
@Scope("prototype")
public class SendSmsDueDate extends CustomComponent implements ReloadAutoComponent, ReloadComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(SendSmsDueDate.class);

	private SpringConfigurationValueHelper configurationHelper;
	public static final String CAPTION = "SMS NHẮC NỢ ĐẾN HẠN";
	private static final String STATEMENTMONTH = "STATEMENT MONTH";
	private static final String SMSTYPE = "SMS TYPE";
	private static final String DUEDATE = "DUE DATE";
	private static final String SENDSMS = "SEND SMS";
	private static final String INSERT = "INSERT";
	private static final String EXCEL = "XLSX";
	private static final String SMSTYPE_DUEDATE = "DEBT02";
	public static final String IDALERT = "MASTER_CARD_ALERT";

	final Button btInsert = new Button(INSERT);
	final Button btSendSms = new Button(SENDSMS);
	final Button btXLSXExport = new Button(EXCEL);

	public final transient FormLayout formLayout = new FormLayout();

	private final SysUserroleService sysUserroleService;
	private SmsStatementService smsStatementService;

	public transient TextField tfDueDate;
	public transient ComboBox cbbStatementMonth;
	public final transient TextField tfSmsType;

	private transient String sUserId;
	private String CheckUserId = "";
	final TimeConverter timeConverter = new TimeConverter();
	List<SmsStatement> smsStatementList = new ArrayList<SmsStatement>();
	private int rowNumExport = 0;
	private String fileNameOutput = "";
	private Path pathExport = null;

	public SendSmsDueDate() {
		// TAN 20190815
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		configurationHelper = (SpringConfigurationValueHelper) helper.getBean("springConfigurationValueHelper");
		final DescriptionService descService = (DescriptionService) helper.getBean("descriptionService");
		sysUserroleService = (SysUserroleService) helper.getBean("sysUserroleService");
		smsStatementService = (SmsStatementService) helper.getBean("smsStatementService");

		this.sUserId = SecurityUtils.getUserId();
		CheckUserId = sysUserroleService.findByRoleId(sUserId);

		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);

		cbbStatementMonth = new ComboBox(STATEMENTMONTH);
		cbbStatementMonth.setNullSelectionAllowed(false);
		cbbStatementMonth.setPageLength(12);
		cbbStatementMonth.setDescription("Format : YYYYMM (= Now - 1 month)");
		descService.findAllByTypeByOrderBySequencenoDesc("KYSAOKE").forEach(item -> {
			cbbStatementMonth.addItem(item.getId());
			cbbStatementMonth.setItemCaption(item.getId(), item.getDescription());
		});
		SimpleDateFormat formatterStatementMonth = new SimpleDateFormat("yyyyMM");
		String cbbStatementMonthdefault = formatterStatementMonth.format(cal.getTime());
		cbbStatementMonth.setValue(cbbStatementMonthdefault);
		cbbStatementMonth.addValueChangeListener(event -> {
			tfDueDate.setValue(defaultDuedate(cbbStatementMonth.getValue().toString()));
		});

		tfDueDate = new TextField(DUEDATE);
		tfDueDate.setDescription("Format : YYYYMMDD");
		tfDueDate.setValue(defaultDuedate(cbbStatementMonth.getValue().toString()));

		tfSmsType = new TextField(SMSTYPE);
		tfSmsType.setValue(SMSTYPE_DUEDATE);
		tfSmsType.setEnabled(false);

		final FormLayout form = new FormLayout();
		form.setMargin(new MarginInfo(true, false, true, true));

		btSendSms.setStyleName(ValoTheme.BUTTON_DANGER);
		btSendSms.setWidth(120.0f, Unit.PIXELS);
		btSendSms.setIcon(FontAwesome.SEND);
		btSendSms.setDescription("Lấy thông tin từ bảng SMS_NHAC_NO gửi qua bên DB EB");

		btInsert.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btInsert.setWidth(120.0f, Unit.PIXELS);
		btInsert.setDescription("Call Procedure GET_SMS_NHAC_NO_DUE_DATE để Insert dữ liệu xuống bảng SMS_NHAC_NO");

		btXLSXExport.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		btXLSXExport.setWidth(120.0f, Unit.PIXELS);
		btXLSXExport.setIcon(FontAwesome.DOWNLOAD);
		btXLSXExport.setDescription("Xuất thông tin từ Procedure hoặc table SMS_NHAC_NO");

		btInsert.addClickListener(event -> {
			form.setEnabled(false);
			String statementMonth = cbbStatementMonth.getValue().toString();
			String duedate = tfDueDate.getValue();
			try {
				smsStatementList = smsStatementService.getDataSMSNhacNoDueDate(statementMonth, duedate);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
							String message = CreateSMSMessage(item.getCardBrn(), item.getCardNo(), item.getSmsMonth(), item.getClosingBalance(),
									item.getMinimumPayment(), item.getDueDate(), item.getVip(), item.getCifVip());
							if (!StringUtils.isEmpty(message)) {
								item.setSmsDetail(message);
								if (item.getDesMobile().equals("khong co")) // ko co so dien thoai
								{
									item.setActionType("D"); // D = ko gui qua ebanking
									smsStatementService.save(item);
								} else {
									item.setActionType("N"); // N = se gui qua ebanking
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
			String smsMonth = cbbStatementMonth.getValue().toString();
			String duedate = tfDueDate.getValue().toString();
			smsStatementList = smsStatementService.findAllByActionTypeAndSmsMonthAndDueDateAndSmsType("N", smsMonth, duedate, SMSTYPE_DUEDATE);

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
							int result = 0;
							try {
								result = smsStatementService.InsertSMSMessateToEBankGW(IDALERT, item.getDesMobile(), item.getSmsDetail(), "N",
										SMSTYPE_DUEDATE);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								LOGGER.error(e.getMessage());
							}
							// call function update table SMS_NHAC_NO
							if (result == 1) {
								item.setActionType("Y");
								item.setInsertTransDateTime(timeConverter.getCurrentTime());
								smsStatementService.save(item);
								LOGGER.info("id: " + item.getId() + ", due Date: " + item.getDueDate() + ", Month: " + item.getSmsMonth() + ", Loc: "
										+ item.getLoc() + " is successful");
							} else {
								item.setActionType("F");
								item.setInsertTransDateTime(timeConverter.getCurrentTime());
								smsStatementService.save(item);
								LOGGER.error("id: " + item.getId() + ", due Date: " + item.getDueDate() + ", Month: " + item.getSmsMonth() + ", Loc: "
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
			bProcedure.setDescription("Export excel từ procedure GET_SMS_NHAC_NO_DUE_DATE");

			final Button bTable = new Button("From SMS_NHAC_NO");
			bTable.setStyleName(ValoTheme.BUTTON_SMALL);

			confirmDialog.setCaption("Export SMS nhắc nợ");
			confirmDialog.setWidth(350.0f, Unit.PIXELS);

			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					String statementMonth = cbbStatementMonth.getValue().toString();
					String duedate = tfDueDate.getValue();
					bProcedure.addClickListener(event -> {

						try {
							smsStatementList = smsStatementService.getDataSMSNhacNoDueDate(statementMonth, duedate);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						for (SmsStatement item : smsStatementList) {
							String message = CreateSMSMessage(item.getCardBrn(), item.getCardNo(), item.getSmsMonth(), item.getClosingBalance(),
									item.getMinimumPayment(), item.getDueDate(), item.getVip(), item.getCifVip());
							if (!StringUtils.isEmpty(message)) {
								item.setSmsDetail(message);
								if (item.getDesMobile().equals("khong co")) // ko co so dien thoai
								{
									item.setActionType("D"); // D = ko gui qua ebanking
								} else {
									item.setActionType("N"); // N = se gui qua ebanking
								}
							} else {
								LOGGER.error("LOC " + item.getLoc() + " can not create message. Please check again");
							}
						}

						// EXPORT LIST TO EXCEL FILE
						XSSFWorkbook workbookExport = new XSSFWorkbook();
						XSSFSheet sheetExport = workbookExport.createSheet("SMS_DUE_DATE");

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

							fileNameOutput = "SMS_DUE_DATE_" + tfDueDate.getValue() + ".xlsx";
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
						smsStatementList = smsStatementService.findAllBySmsMonthAndDueDateAndSmsType(statementMonth, duedate, SMSTYPE_DUEDATE);
						// EXPORT LIST TO EXCEL FILE
						XSSFWorkbook workbookExport = new XSSFWorkbook();
						XSSFSheet sheetExport = workbookExport.createSheet("SMS_DUE_DATE");

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

							fileNameOutput = "SMS_DUE_DATE_" + tfDueDate.getValue() + ".xlsx";
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

		form.addComponent(tfDueDate);
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

	private String CreateSMSMessage(String brand, String pan, String settleDay, String totalClosingBal, String totalMiniPay, String dueDay,
			String vip_card, String vip_cif) {
		try {
			String SCBPhone = "";
			pan = pan.substring(12, 16);
			String settleDay_f = "";
			if (brand.equals("VS"))
				settleDay_f = "15/" + settleDay.substring(4, 6) + "/" + settleDay.substring(2, 4);
			else
				settleDay_f = "25/" + settleDay.substring(4, 6) + "/" + settleDay.substring(2, 4);

			dueDay = dueDay.substring(6, 8) + "/" + dueDay.substring(4, 6);
			if (vip_card.equals("Y") || vip_cif.equals("Y"))
				SCBPhone = "1800545438";
			else
				SCBPhone = "19006538";

			double d_totalClsBal = Double.parseDouble(totalClosingBal);
			double d_totalMin = Double.parseDouble(totalMiniPay);

			Locale locale = new Locale("vi", "VN");
			NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(locale);
			String s_totalCloBal = moneyFormat.format(d_totalClsBal).replace(" đ", "");
			String s_totalMiniPay = moneyFormat.format(d_totalMin).replace(" đ", "");

			String smsMessage = "Cam on Quy khach su dung the " + pan + "\nDu no den " + settleDay_f + ": " + s_totalCloBal + "VND\nTT toi thieu: "
					+ s_totalMiniPay + "VND" + "\nNgay den han " + dueDay + "\nVui long bo qua neu da TT\nLH " + SCBPhone;

			return smsMessage;
		} catch (Exception ex) {
			LOGGER.error("Error CreateSMSMessage(), " + ex.getMessage());
			return "";
		}
	}

	private String defaultDuedate(String statementMonth) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dateInString = cbbStatementMonth.getValue().toString() + "25";
		String output = "";
		try {
			Date date = formatter.parse(dateInString);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, 15); // Adding 5 days
			output = formatter.format(c.getTime());
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
		}
		return output;

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
