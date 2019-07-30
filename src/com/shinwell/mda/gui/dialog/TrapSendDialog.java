package com.shinwell.mda.gui.dialog;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.snmp4j.PDU;

import swing2swt.layout.BorderLayout;

import com.shinwell.mda.domain.SnmpTrap;
import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.util.CommonUtil;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;
import com.shinwell.mda.util.SWTResourceManager;
import com.shinwell.mda.util.snmp.SnmpTrapSendUtil;
/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2018年4月29日上午1:54:11
 * @Version: 1.0
 * @Desc:
 */
public class TrapSendDialog extends TitleAreaDialog {
	private Text trapcontentText;
	private Text serverText;
	private Combo portCombo;
	private Combo versionCombo;
	private Text commText;
	private Table oidTable;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TrapSendDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MAX);
		setHelpAvailable(false);
	}
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		UIBuilderFactory.setCenter(newShell);
		newShell.setText("Trap模拟发送");
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		//setTitleImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_SNMP_SENDTRAP));
		setTitle("Trap模拟发送");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new BorderLayout(0, 0));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group grpTrap = new Group(container, SWT.NONE);
		grpTrap.setText("Trap模拟发送配置");
		grpTrap.setLayoutData(BorderLayout.NORTH);
		grpTrap.setLayout(new GridLayout(4, false));
		
		final Label errorLabel = new Label(grpTrap, SWT.NONE);
		errorLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		errorLabel.setAlignment(SWT.CENTER);
		errorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		final Label serverIpLabel = new Label(grpTrap, SWT.NONE);
		serverIpLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		serverIpLabel.setText("服务器IP*:");
		
		serverText = new Text(grpTrap, SWT.BORDER);
		serverText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		final Label portLabel = new Label(grpTrap, SWT.NONE);
		portLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		portLabel.setText("端口*:");
		
		portCombo = new Combo(grpTrap, SWT.BORDER);
		portCombo.setItems(new String[] {"162"});
		portCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		portCombo.select(0);
		
		final Label trapTypeLabel = new Label(grpTrap, SWT.NONE);
		trapTypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		trapTypeLabel.setText("Trap类型*:");
		
		final Combo trapTypeCombo = new Combo(grpTrap, SWT.READ_ONLY);
		trapTypeCombo.setItems(new String[] {"SNMP V1Trap", "SNMP Trap"});
		trapTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		//trapTypeCombo.select(0);
		
		final Label versionLabel = new Label(grpTrap, SWT.NONE);
		versionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		versionLabel.setText("版本*:");
		
		versionCombo = new Combo(grpTrap, SWT.READ_ONLY);
		versionCombo.setItems(new String[] {"v1", "v2c", "v3"});
		versionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		versionCombo.select(0);
		
		Label retriesLabel = new Label(grpTrap, SWT.NONE);
		retriesLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		retriesLabel.setText("重试次数:");
		
		final Combo retriesCombo = new Combo(grpTrap, SWT.READ_ONLY);
		retriesCombo.setItems(new String[] {"1", "3", "5"});
		retriesCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		retriesCombo.select(1);
		
		Label timeoutLabel = new Label(grpTrap, SWT.NONE);
		timeoutLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		timeoutLabel.setText("超时(秒):");
		
		final Combo timeoutCombo = new Combo(grpTrap, SWT.READ_ONLY);
		timeoutCombo.setItems(new String[] {"3", "5", "7"});
		timeoutCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		timeoutCombo.select(0);
		
		Label communityLabel = new Label(grpTrap, SWT.NONE);
		communityLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		communityLabel.setText("Community：");
		
		commText = new Text(grpTrap, SWT.BORDER | SWT.PASSWORD);
		commText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite btnCom = new Composite(grpTrap, SWT.NONE);
		btnCom.setLayout(new GridLayout(5, false));
		btnCom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Button sendButton = new Button(btnCom, SWT.NONE);
		sendButton.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		sendButton.setText("  发  送  ");
		
		final Button btnoid = new Button(btnCom, SWT.CHECK);
		btnoid.setText("自定义OID");
		
		final Button addItemButton = new Button(btnCom, SWT.NONE);
		addItemButton.setToolTipText("增加表格OID");
		addItemButton.setEnabled(false);
		addItemButton.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_ADD_ACTION));
		
		final Button editButton = new Button(btnCom, SWT.NONE);
		editButton.setToolTipText("修改表格OID");
		editButton.setEnabled(false);
		editButton.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_EDIT_ACTION));
		
		final Button deltemButton = new Button(btnCom, SWT.NONE);
		deltemButton.setToolTipText("刪除表格OID");
		deltemButton.setEnabled(false);
		deltemButton.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_DELETE_ACTION));
		
		trapcontentText = new Text(grpTrap, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		trapcontentText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!trapcontentText.getText().trim().equals("")) {
					if (trapcontentText.getText().startsWith("V1TRAP")) {
						trapTypeCombo.select(0);
					}else if (trapcontentText.getText().startsWith("TRAP")) {
						trapTypeCombo.select(1);
					}
				}else {
					trapTypeCombo.setText("");
					trapTypeCombo.setItem(-1, "");
					trapTypeCombo.update();
				}
			}
		});
		trapcontentText.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		trapcontentText.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		GridData gd_trapcontentText = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd_trapcontentText.heightHint = 110;
		trapcontentText.setLayoutData(gd_trapcontentText);
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		TableColumnLayout tcl_composite = new TableColumnLayout();
		composite.setLayout(tcl_composite);
		
		TableViewer tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		oidTable = tableViewer.getTable();
		oidTable.setHeaderVisible(true);
		oidTable.setLinesVisible(true);
		
		TableColumn oidKeyColumn = new TableColumn(oidTable, SWT.CENTER);
		tcl_composite.setColumnData(oidKeyColumn, new ColumnPixelData(400, true, true));
		oidKeyColumn.setText("OID");
		
		TableColumn oidValueColumn = new TableColumn(oidTable, SWT.CENTER);
		tcl_composite.setColumnData(oidValueColumn, new ColumnPixelData(150, true, true));
		oidValueColumn.setText("值");
		
		btnoid.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnoid.getSelection()) {
					addItemButton.setEnabled(true);
					editButton.setEnabled(true);
					deltemButton.setEnabled(true);
				}else {
					addItemButton.setEnabled(false);
					editButton.setEnabled(false);
					deltemButton.setEnabled(false);
				}
				
			}
		});
		addItemButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				OidDialog oidDialog = new OidDialog(getShell(), null,0,oidTable);
				oidDialog.open();
				oidTable.layout();
			}
		});
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (oidTable.getSelectionIndex()>-1) {
					TableItem tableItem = oidTable.getItem(oidTable.getSelectionIndex());
					if (null!=tableItem) {
						OidDialog oidDialog = new OidDialog(getShell(), tableItem,1,oidTable);
						oidDialog.open();
					}
				}
			}
		});
		deltemButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (oidTable.getSelectionIndex()>-1) {
					TableItem tableItem = oidTable.getItem(oidTable.getSelectionIndex());
					if (null!=tableItem) {
						oidTable.remove(oidTable.getSelectionIndex());
						tableItem.dispose();
					}
				}
			}
		});
		sendButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				errorLabel.setText("");
			
				if (serverText.getText().trim().equals("")) {
					errorLabel.setText("\""+serverIpLabel.getText()+"\""+"不能为空");
					return;
				}
				if (portCombo.getText().trim().equals("")) {
					errorLabel.setText("\""+portLabel.getText()+"\""+"不能为空");
					return;
				}
				if (trapTypeCombo.getText().trim().equals("")) {
					errorLabel.setText("\""+trapTypeLabel.getText()+"\""+"不能为空");
					return;
				}
				if (versionCombo.getText().trim().equals("")) {
					errorLabel.setText("\""+versionLabel.getText()+"\""+"不能为空");
					return;
				}
				String trapType = trapTypeCombo.getText().trim();
				String version =  versionCombo.getText().trim();
				String retries =  retriesCombo.getText().trim();
				String timeout = timeoutCombo.getText().trim();
				int _version =0;
				if (version.equalsIgnoreCase("v2c")) {
					_version =1;
				}else if (version.equalsIgnoreCase("v2c")) {
					_version =3;
				}
				Map vbs = new HashMap<Object, Object>();
				SnmpTrap trap = new SnmpTrap();
				if (btnoid.getSelection()) {
					if (oidTable.getItemCount()==0) {
						errorLabel.setText("OID 表格无定义项，请补充数据。");
					}else {
						TableItem[] tableItems = oidTable.getItems();
						for (TableItem tableItem : tableItems) {
							vbs.put(tableItem.getText(0), tableItem.getText(1));
						}
						trap.setVbs(vbs);
					}
				}else {
					if (trapcontentText.getText().trim().length()==0) {
						errorLabel.setText("Trap 内容不能为空!");
						return;
					}
					if (trapcontentText.getText().contains("V1TRAP")) {
						trapTypeCombo.select(0);
					}else {
						trapTypeCombo.select(1);
					}
					
					String trapContent = trapcontentText.getText();
					trap = CommonUtil.parseTrapText(trapContent);
				}
				SnmpTrapSendUtil trapSend = new SnmpTrapSendUtil(serverText.getText().trim(), Integer.valueOf(portCombo.getText().trim()));
				try {
					trapSend.initComm();
					trapSend.sendTrap(trap,trapType.equalsIgnoreCase("SNMP V1Trap")?PDU.V1TRAP:PDU.TRAP ,_version, Integer.valueOf(retries), Integer.valueOf(timeout),  commText.getText().trim());
					MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), "发送成功！");
				} catch (Exception e1) {
					MessageDialog.openError(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), "发送失败！"+e1.getMessage());
					e1.printStackTrace();
					
				} 
			}
		});
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		//createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(918, 730);
	}

}
