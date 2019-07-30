package com.shinwell.mda.gui.composite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.cim.CIMInstance;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.snmp4j.PDU;

import swing2swt.layout.BorderLayout;

import com.shinwell.mda.domain.SnmpProvider;
import com.shinwell.mda.domain.SnmpTrap;
import com.shinwell.mda.gui.MdaAppWindow;
import com.shinwell.mda.gui.dialog.OidDialog;
import com.shinwell.mda.gui.dialog.SnmpProviderDialog;
import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.util.CommonUtil;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;
import com.shinwell.mda.util.SWTResourceManager;
import com.shinwell.mda.util.snmp.SnmpMTTrapReceiver;
import com.shinwell.mda.util.snmp.SnmpOperationUtil;
import com.shinwell.mda.util.snmp.SnmpTrapSendUtil;

/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月25日上午10:52:18
 * @Version: 1.0
 * @Desc:
 */
public class SnmpComposite extends Composite {
	static Logger logger = Logger.getLogger(SnmpComposite.class.getSimpleName());
	static Set<SnmpProvider> snmpProviderSet = new HashSet<SnmpProvider>();
	static Tree tree;
	static TreeItem rooTreeItem;
	private Composite tabComposite;
	private CTabFolder tabFolder;
	private ToolItem addSnmpToolItem;
	private ToolItem editSnmpToolItem;
	private ToolItem deleteSnmpToolItem;
	private ToolItem testSnmpToolItem;
	private Menu treeContextMenu;
	private MenuItem testMenuItem;
	private MenuItem addMenuItem;
	private MenuItem editMenuItem;
	private MenuItem deleteMenuItem;
	private MenuItem expMenuItem;
	private Group snmpGroupForm;
	private Label hostLabel;
	private Text hostText;
	private Label portLabel;
	private Combo portCombo;
	private Label versionLabel;
	private Combo versionCombo;
	private Label actionLabel;
	private Combo actionCombo;
	private Label readCommLabel;
	private Text readCommText;
	private Label writeCommLabel;
	private Text writeCommText;
	private Label oidLabel;
	private Text oidText;
	private Label setValueLabel;
	private Text setValueText;
	private Button doButton;
	private Button resetButton;
	private Composite btnComposite;
	private Button clearButton;
	private Text resultText;
	private Group resultGroupForm;
	private Label errorLabel;
	private ToolItem sendSnmpTrapToolItem;
	private ToolItem receiveSnmpTrapToolItem;

	public static Set<SnmpProvider> getSnmpProviderSet() {
		return snmpProviderSet;
	}

	public SnmpComposite(Composite parent, int style) {
		// super(parent, style);
		// setLayout(new FillLayout(SWT.HORIZONTAL));
		//
		// Composite composite = new Composite(this, SWT.NONE);
		// composite.setLayout(new BorderLayout(0, 0));
		// SashForm sashForm = new SashForm(composite, SWT.BORDER);
		// sashForm.setLayoutData(BorderLayout.CENTER);
		// sashForm.setSashWidth(1);
		//
		//
		// Tree tree = new Tree(sashForm, SWT.NONE);
		// TreeItem treeItem = new TreeItem(tree, SWT.NONE);
		// treeItem.setText("SNMP-ROOT");
		//
		// CTabFolder tabFolder = new CTabFolder(sashForm, SWT.NONE);
		// tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		//
		// CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.NONE);
		// tbtmNewItem.setText("SNMP");
		//
		// sashForm.setWeights(new int[] { 15, 85 });
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new BorderLayout(0, 0));

		SashForm sashForm = new SashForm(composite, SWT.SMOOTH);
		sashForm.setLayoutData(BorderLayout.CENTER);
		sashForm.setSashWidth(3);

		// left content
		createTreeComposite(sashForm);
		// right content
		createTabFolder(sashForm);
		sashForm.setWeights(new int[] { 184, 819 });
	}

	private void createTreeComposite(SashForm sashForm) {
		Composite treeComposite = new Composite(sashForm, SWT.BORDER);
		treeComposite.setLayout(new BorderLayout(5, 5));

		createToolBar(treeComposite);

		tree = new Tree(treeComposite, SWT.FULL_SELECTION);
		tree.setSortDirection(SWT.UP);
		tree.setLayoutData(BorderLayout.CENTER);

		rooTreeItem = new TreeItem(tree, SWT.NONE);
		rooTreeItem.setText("SNMP-ROOT");
		rooTreeItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TREE_ROOT));
		rooTreeItem.setExpanded(true);

		treeContextMenu = new Menu(tree);
		tree.setMenu(treeContextMenu);

		testMenuItem = new MenuItem(treeContextMenu, SWT.NONE);
		testMenuItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_CONNECT));
		testMenuItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TEST_ACTION));

		new MenuItem(treeContextMenu, SWT.SEPARATOR);

		addMenuItem = new MenuItem(treeContextMenu, SWT.NONE);
		addMenuItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_ADD));
		addMenuItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_ADD_ACTION));

		editMenuItem = new MenuItem(treeContextMenu, SWT.NONE);
		editMenuItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_EDIT));
		editMenuItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_EDIT_ACTION));

		deleteMenuItem = new MenuItem(treeContextMenu, SWT.NONE);
		deleteMenuItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_DELETE));
		deleteMenuItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_REMOVE_ACTION));

		new MenuItem(treeContextMenu, SWT.SEPARATOR);

		expMenuItem = new MenuItem(treeContextMenu, SWT.NONE);
		expMenuItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_EXPORT));
		expMenuItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_EXPORT_ACTION));

		addTreeContextMenuListener();

		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TreeItem item = (TreeItem) event.item;
				if (item.getText().equals("SNMP-ROOT")) {
					addMenuItem.setEnabled(true);
					editMenuItem.setEnabled(false);
					deleteMenuItem.setEnabled(false);
					testMenuItem.setEnabled(false);
					expMenuItem.setEnabled(false);
				} else if (item.getData() instanceof SnmpProvider) {
					addMenuItem.setEnabled(false);
					editMenuItem.setEnabled(true);
					deleteMenuItem.setEnabled(true);
					testMenuItem.setEnabled(true);
					expMenuItem.setEnabled(false);
				} else if (item.getData() instanceof CIMInstance) {
					addMenuItem.setEnabled(false);
					editMenuItem.setEnabled(false);
					deleteMenuItem.setEnabled(false);
					testMenuItem.setEnabled(false);
					expMenuItem.setEnabled(true);
				} else {
					addMenuItem.setEnabled(false);
					editMenuItem.setEnabled(false);
					deleteMenuItem.setEnabled(false);
					testMenuItem.setEnabled(false);
					expMenuItem.setEnabled(false);
				}

			}
		});

		// 双击事件逻辑处理
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent event) {
				TreeItem item = tree.getSelection()[0];
				if (item.getData() instanceof SnmpProvider) {
					boolean isExsit = false;
					CTabItem[] items = tabFolder.getItems();
					for (CTabItem cTabItem : items) {
						if (cTabItem.getText().equalsIgnoreCase(item.getText())) {
							tabFolder.setSelection(cTabItem);
							isExsit = true;
							break;
						}
					}
					if (!isExsit) {
						SnmpProvider provider = (SnmpProvider) item.getData();
						openSNMPQueryCTabItem(item, tabFolder, provider);
					}

				}
			}
		});
		initData();
	}
	// 打开一个新页面
		protected void openTrapSendCTabItem(CTabFolder tabFolder, String name) {
			CTabItem cTabItem = new CTabItem(tabFolder, SWT.NONE);
			cTabItem.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent event) {
				}
			});
			cTabItem.setText(name);
			cTabItem.setToolTipText(name);

			Composite container = new Composite(tabFolder, SWT.NONE);
			container.setLayout(new BorderLayout(0, 0));
			//container.setLayoutData(new GridData(GridData.FILL_BOTH));
			cTabItem.setControl(container);
			
			Group grpTrap = new Group(container, SWT.NONE);
			grpTrap.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_CONFIG));
			grpTrap.setLayoutData(BorderLayout.NORTH);
			grpTrap.setLayout(new GridLayout(4, false));
			
			final Label errorLabel = new Label(grpTrap, SWT.NONE);
			errorLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
			errorLabel.setAlignment(SWT.CENTER);
			errorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
			
			final Label serverIpLabel = new Label(grpTrap, SWT.NONE);
			serverIpLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			serverIpLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_SERVER_IP));
			
			final Text serverText = new Text(grpTrap, SWT.BORDER);
			serverText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			final Label portLabel = new Label(grpTrap, SWT.NONE);
			portLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			portLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_SERVER_PORT));
			
			portCombo = new Combo(grpTrap, SWT.BORDER);
			portCombo.setItems(new String[] {"162","1622"});
			portCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			portCombo.select(0);
			
			final Label trapTypeLabel = new Label(grpTrap, SWT.NONE);
			trapTypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			trapTypeLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_SERVER_TYPE));
			
			final Combo trapTypeCombo = new Combo(grpTrap, SWT.READ_ONLY);
			trapTypeCombo.setItems(new String[] {"SNMP V1Trap", "SNMP Trap"});
			trapTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			//trapTypeCombo.select(0);
			
			final Label versionLabel = new Label(grpTrap, SWT.NONE);
			versionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			versionLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_SERVER_VERSION));
			
			versionCombo = new Combo(grpTrap, SWT.READ_ONLY);
			versionCombo.setItems(new String[] {"v1", "v2c", "v3"});
			versionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			versionCombo.select(0);
			
			Label retriesLabel = new Label(grpTrap, SWT.NONE);
			retriesLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			retriesLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_SERVER_RETRY_TIMES));
			
			final Combo retriesCombo = new Combo(grpTrap, SWT.READ_ONLY);
			retriesCombo.setItems(new String[] {"1", "3", "5"});
			retriesCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			retriesCombo.select(1);
			
			Label timeoutLabel = new Label(grpTrap, SWT.NONE);
			timeoutLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			timeoutLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_TIMEOUT));
			
			final Combo timeoutCombo = new Combo(grpTrap, SWT.READ_ONLY);
			timeoutCombo.setItems(new String[] {"3", "5", "7"});
			timeoutCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			timeoutCombo.select(0);
			
			Label communityLabel = new Label(grpTrap, SWT.NONE);
			communityLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			communityLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_COMMUNITY));
			
			final Text commText = new Text(grpTrap, SWT.BORDER | SWT.PASSWORD);
			commText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			Composite btnCom = new Composite(grpTrap, SWT.NONE);
			btnCom.setLayout(new GridLayout(5, false));
			btnCom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			
			Button sendButton = new Button(btnCom, SWT.NONE);
			sendButton.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 9, SWT.BOLD));
			sendButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_BUTTON));
			
			final Button btnoid = new Button(btnCom, SWT.CHECK);
			btnoid.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_CUSTOM_OID));
			
			final Button addItemButton = new Button(btnCom, SWT.NONE);
			addItemButton.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_ADD_TABLE_OID));
			addItemButton.setEnabled(false);
			addItemButton.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_ADD_ACTION));
			
			final Button editButton = new Button(btnCom, SWT.NONE);
			editButton.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_EDIT_TABLE_OID));
			editButton.setEnabled(false);
			editButton.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_EDIT_ACTION));
			
			final Button deltemButton = new Button(btnCom, SWT.NONE);
			deltemButton.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_DELETE_TABLE_OID));
			deltemButton.setEnabled(false);
			deltemButton.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_DELETE_ACTION));
			
			final Text trapcontentText = new Text(grpTrap, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
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
						trapTypeCombo.update();
					}
				}
			});
			trapcontentText.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			trapcontentText.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
			GridData gd_trapcontentText = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
			gd_trapcontentText.heightHint = 220;
			trapcontentText.setLayoutData(gd_trapcontentText);
			trapcontentText.setText("V1TRAP[reqestID=0,timestamp=0:00:00.00,enterprise=1.3.6.1.4.1.1588.2.1.1.34,genericTrap=6,specificTrap=4, VBS[1.3.6.1.4.1.1588.2.1.1.1.8.5.1.5.41112 = SNMP-1005 SNMP configuration attribute, Trap Severity Level 4 , has changed from 3 to 4; 1.3.6.1.4.1.1588.2.1.1.1.1.10.0 = USB644XB3R; 1.3.6.1.4.1.1588.2.1.1.1.8.5.1.3.41112 = 4; 1.3.6.1.4.1.1588.2.1.1.1.8.5.1.1.41112 = 41112; 1.3.6.1.4.1.1588.2.1.1.1.8.5.1.4.41112 = 1; 1.3.6.1.4.1.1588.2.1.1.1.8.5.1.2.41112 = 2015/11/06-09:25:02]]");
			
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayoutData(BorderLayout.CENTER);
			TableColumnLayout tcl_composite = new TableColumnLayout();
			composite.setLayout(tcl_composite);
			
			TableViewer tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
			final Table oidTable = tableViewer.getTable();
			oidTable.setHeaderVisible(true);
			oidTable.setLinesVisible(true);
			
			TableColumn oidKeyColumn = new TableColumn(oidTable, SWT.CENTER);
			tcl_composite.setColumnData(oidKeyColumn, new ColumnPixelData(400, true, true));
			oidKeyColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_TABLE_OID));
			
			TableColumn oidValueColumn = new TableColumn(oidTable, SWT.CENTER);
			tcl_composite.setColumnData(oidValueColumn, new ColumnPixelData(150, true, true));
			oidValueColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_TABLE_VALUE));
			
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
						errorLabel.setText("\""+serverIpLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
						return;
					}
					if (portCombo.getText().trim().equals("")) {
						errorLabel.setText("\""+portLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
						return;
					}
					if (trapTypeCombo.getText().trim().equals("")) {
						errorLabel.setText("\""+trapTypeLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
						return;
					}
					if (versionCombo.getText().trim().equals("")) {
						errorLabel.setText("\""+versionLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
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
							errorLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_TABLE_NO_OID_ITEM));
						}else {
							TableItem[] tableItems = oidTable.getItems();
							for (TableItem tableItem : tableItems) {
								vbs.put(tableItem.getText(0), tableItem.getText(1));
							}
							trap.setEnterprise("1.3.6.1.4.1.36500");
							trap.setSpecialTrap("4");
							trap.setGeneralTrap("6");
							trap.setVbs(vbs);
						}
					}else {
						if (trapcontentText.getText().trim().length()==0) {
							errorLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_TRAP_CONTENT_NOT_EMPTY));
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
						MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_SENT_SUCCESFUL));
					} catch (Exception e1) {
						MessageDialog.openError(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_SENT_FAIL)+e1.getMessage());
						logger.error("SendTrap error:",e1);
						
					} 
				}
			});
			tabFolder.setSelection(cTabItem);
			
		}
		//open trap receive tabItem
		protected void openTrapReceiveCTabItem(CTabFolder tabFolder, String name) {
			CTabItem cTabItem = new CTabItem(tabFolder, SWT.NONE);
			cTabItem.setText(name);
			cTabItem.setToolTipText(name);
			
			Composite container = new Composite(tabFolder, SWT.NONE);
			container.setLayout(new BorderLayout(5, 5));
			//container.setLayoutData(new GridData(GridData.FILL_BOTH));
			cTabItem.setControl(container);
			
			Group grpTrap = new Group(container, SWT.NONE);
			grpTrap.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_CONFIG));
			grpTrap.setLayoutData(BorderLayout.NORTH);
			grpTrap.setLayout(new GridLayout(7, false));
			
			Label portLabel = new Label(grpTrap, SWT.NONE);
			portLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			portLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_LISTENER_PORT));
			
			final Combo portCombo = new Combo(grpTrap, SWT.NONE);
			portCombo.setItems(new String[] {"162","1622"});
			portCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			portCombo.select(0);
			
			Label communityLabel = new Label(grpTrap, SWT.NONE);
			communityLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			communityLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_COMMUNITY));
			
			final Text commText = new Text(grpTrap, SWT.BORDER);
			commText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			final Button startButton = new Button(grpTrap, SWT.NONE);
			startButton.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 9, SWT.BOLD));
			startButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_START));
			
			final Button stopButton = new Button(grpTrap, SWT.NONE);
			stopButton.setEnabled(false);
			stopButton.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 9, SWT.BOLD));
			stopButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_STOP));
			
			Button delButton = new Button(grpTrap, SWT.NONE);
			delButton.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 9, SWT.BOLD));
			delButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_DELETE));
			
			SashForm sashForm = new SashForm(container, SWT.NONE);
			sashForm.setLayoutData(BorderLayout.CENTER);
			
			SashForm leftSashForm = new SashForm(sashForm, SWT.VERTICAL);
			
			TableViewer trapTableViewer = new TableViewer(leftSashForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
			final Table trapTable = trapTableViewer.getTable();
			trapTable.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_DETAIL));
			trapTable.setLinesVisible(true);
			trapTable.setHeaderVisible(true);
			
//			TableColumn classColumn = new TableColumn(trapTable, SWT.LEFT);
//			classColumn.setWidth(100);
//			classColumn.setText("Class");
			
			TableColumn typeColumn = new TableColumn(trapTable, SWT.LEFT);
			typeColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_COLUMN_TYPE));
			typeColumn.setWidth(100);
			
			TableColumn sourceColumn = new TableColumn(trapTable, SWT.LEFT);
			sourceColumn.setWidth(200);
			sourceColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_COLUMN_SOURCE));
			
			TableColumn dateColumn = new TableColumn(trapTable, SWT.LEFT);
			dateColumn.setWidth(200);
			dateColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_COLUMN_DATE));
			
			TableColumn msgColumn = new TableColumn(trapTable, SWT.LEFT);
			msgColumn.setWidth(500);
			msgColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_COLUMN_MSG));
			
			final Text trapText = new Text(leftSashForm, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
			trapText.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_RECEIVER_ORIGINAL_TRAP));
			leftSashForm.setWeights(new int[] {80, 20});
			
			TableViewer trapDetailTableViewer = new TableViewer(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
			final Table detailTrapTable;
			detailTrapTable = trapDetailTableViewer.getTable();
			detailTrapTable.setLinesVisible(true);
			detailTrapTable.setHeaderVisible(true);
			
			TableColumn propertyColumn = new TableColumn(detailTrapTable, SWT.LEFT);
			propertyColumn.setWidth(300);
			propertyColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_TABLE_OID));
			
			TableColumn valueColumn = new TableColumn(detailTrapTable, SWT.LEFT);
			valueColumn.setWidth(100);
			valueColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_TRAP_SENDER_TABLE_VALUE));
			sashForm.setWeights(new int[] {70, 30});
			startButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (portCombo.getText().trim().length()==0) {
						MessageDialog.openError(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "端口不能为空！");
						return;
					}
					portCombo.setEnabled(false);
					commText.setEnabled(false);
					startButton.setEnabled(false);
					stopButton.setEnabled(true);
					SnmpMTTrapReceiver receiver;
					try {
						receiver = SnmpMTTrapReceiver.getInstance("127.0.0.1", Integer.valueOf(portCombo.getText().trim()),commText.getText(),trapTable);
						receiver.run();
					}catch (Exception e1) {
						MessageDialog.openError(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), e1.getMessage());
						logger.error("UnknownHostException error:",e1);
						//e1.printStackTrace();
					}
				}
			});
			stopButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					portCombo.setEnabled(true);
					commText.setEnabled(true);
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
					try {
						SnmpMTTrapReceiver receiver = SnmpMTTrapReceiver.getInstance("127.0.0.1", Integer.valueOf(portCombo.getText().trim()),commText.getText(),trapTable);
						receiver.stop();
					}catch (Exception e1) {
						MessageDialog.openError(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), e1.getMessage());
						logger.error("UnknownHostException error:",e1);
					}
				
				}
			});
			delButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableItem[] items = trapTable.getSelection();
					for (TableItem tableItem : items) {
						trapTable.remove(trapTable.indexOf(tableItem));
					}
				}
			});
			trapTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					TableItem[] items = trapTable.getSelection();
					if (items.length>0) {
						detailTrapTable.removeAll();
						SnmpTrap trap = (SnmpTrap)items[0].getData();
						Map<Object, Object> vbs = trap.getVbs();
						for (Entry<Object, Object> entry :vbs.entrySet()) {
							TableItem item = new TableItem(detailTrapTable, SWT.NONE);
							item.setText(new String[]{entry.getKey().toString(),entry.getValue().toString()});
						}
						trapText.setText(trap.getPud().toString());
					}
				}
			});
			cTabItem.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent event) {
					logger.info("close snmp listen!");
					try {
						SnmpMTTrapReceiver receiver = SnmpMTTrapReceiver.getInstance("127.0.0.1", Integer.valueOf(portCombo.getText().trim()),commText.getText(),trapTable);
						receiver.close();
					}catch (Exception e1) {
						MessageDialog.openError(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), e1.getMessage());
						logger.error("UnknownHostException error:",e1);
					}
				}
			});
			tabFolder.setSelection(cTabItem);
		}
	// 打开一个snmp query新页面
	protected void openSNMPQueryCTabItem(TreeItem selectedItem, CTabFolder tabFolder, SnmpProvider provider) {
		CTabItem cTabItem = new CTabItem(tabFolder, SWT.NONE);
		cTabItem.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
			}
		});
		cTabItem.setText(selectedItem.getText());
		cTabItem.setToolTipText(provider.getDisplayName() + "(" + provider.getHost() + ")");

		Composite tabItemComposite = new Composite(tabFolder, SWT.NONE);
		tabItemComposite.setLayout(new BorderLayout(0, 0));
		cTabItem.setControl(tabItemComposite);

		snmpGroupForm = new Group(tabItemComposite, SWT.NONE);
		snmpGroupForm.setLayoutData(BorderLayout.NORTH);
		snmpGroupForm.setText("SNMP");
		GridLayout gl_snmpGroupForm = new GridLayout(4, false);
		gl_snmpGroupForm.marginBottom = 5;
		gl_snmpGroupForm.marginLeft = 5;
		gl_snmpGroupForm.marginTop = 5;
		gl_snmpGroupForm.marginRight = 5;
		snmpGroupForm.setLayout(gl_snmpGroupForm);
		
		errorLabel = new Label(snmpGroupForm, SWT.HORIZONTAL | SWT.CENTER);
		errorLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		errorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));

		hostLabel = new Label(snmpGroupForm, SWT.NONE);
		hostLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hostLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_HOST_LABEL));

		hostText = new Text(snmpGroupForm, SWT.BORDER | SWT.READ_ONLY);
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		hostText.setText(provider.getHost());

		portLabel = new Label(snmpGroupForm, SWT.NONE);
		portLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		portLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_PORT_LABEL));

		portCombo = new Combo(snmpGroupForm, SWT.BORDER);
		portCombo.setItems(new String[] { "161" });
		portCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		portCombo.setText(String.valueOf(provider.getPort()));

		versionLabel = new Label(snmpGroupForm, SWT.NONE);
		versionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		versionLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_SNMPVERSION_LABEL));

		versionCombo = new Combo(snmpGroupForm, SWT.READ_ONLY);
		versionCombo.setItems(new String[] { "v1", "v2c" });
		versionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		versionCombo.select(0);

		actionLabel = new Label(snmpGroupForm, SWT.NONE);
		actionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		actionLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_OPERATOR_LABEL));

		actionCombo = new Combo(snmpGroupForm, SWT.READ_ONLY);
		//actionCombo.setItems(new String[] { "Get", "GetNext", "GetBulk", "SnmpWalk", "Set" });
		actionCombo.setItems(new String[] { "Get", "SnmpWalk", "Set" });
		actionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		actionCombo.select(0);

		readCommLabel = new Label(snmpGroupForm, SWT.NONE);
		readCommLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		readCommLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_R_COMMUNITY_LABEL));

		readCommText = new Text(snmpGroupForm, SWT.BORDER | SWT.PASSWORD);
		readCommText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		readCommText.setText(provider.getReadCommunity());

		writeCommLabel = new Label(snmpGroupForm, SWT.NONE);
		writeCommLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		writeCommLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_W_COMMUNITY_LABEL));

		writeCommText = new Text(snmpGroupForm, SWT.BORDER | SWT.PASSWORD);
		writeCommText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		writeCommText.setText(provider.getWriteCommunity());

		oidLabel = new Label(snmpGroupForm, SWT.NONE);
		oidLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		oidLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_OID_LABEL));

		oidText = new Text(snmpGroupForm, SWT.BORDER);
		oidText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		oidText.setText(provider.getEnterpriseOID());

		setValueLabel = new Label(snmpGroupForm, SWT.NONE);
		setValueLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		setValueLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_VALUE_LABEL));

		setValueText = new Text(snmpGroupForm, SWT.BORDER);
		setValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		btnComposite = new Composite(snmpGroupForm, SWT.NONE);
		btnComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		btnComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 4, 1));

		doButton = new Button(btnComposite, SWT.NONE);
		doButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				errorLabel.setText("");
				List<String> resultList = new ArrayList<String>();
				if (portCombo.getText().trim().equals("")) {
					errorLabel.setText("\""+portLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
					return;
				}
				if (oidText.getText().trim().equals("")) {
					errorLabel.setText("\""+oidLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
					return;
				}
				if (actionCombo.getText().equalsIgnoreCase("Get")) {
					resultList = SnmpOperationUtil.snmpGet(hostText.getText(), readCommText.getText().trim(), oidText.getText().trim(), 0, 0, 0);
				} else if (actionCombo.getText().equalsIgnoreCase("GetNext")) {
					// SnmpOperationUtil.snmpGetList(hostText.getText(),
					// readCommText.getText().trim(), oidText.getText().trim());
				} else if (actionCombo.getText().equalsIgnoreCase("GetBulk")) {

				} else if (actionCombo.getText().equalsIgnoreCase("SnmpWalk")) {
					resultList = SnmpOperationUtil.snmpWalk(hostText.getText(), readCommText.getText().trim(), oidText.getText().trim(), 0, 0, 0);
				} else if (actionCombo.getText().equalsIgnoreCase("Set")) {
					if (writeCommText.getText().trim().equals("")) {
						errorLabel.setText("\""+writeCommLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
						return;
					}
					if (setValueText.getText().trim().equals("")) {
						errorLabel.setText("\""+setValueLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
						return;
					}
					try {
						resultList = SnmpOperationUtil.setPDU(hostText.getText(), readCommText.getText().trim(), oidText.getText().trim(), setValueText.getText().trim(), 0, 0, 0);
					} catch (IOException e1) {
						logger.error("SetPDU error:",e1);
					}
				}
				
				for (String obj : resultList) {
					resultText.append(obj + "\n");
				}
			}
		});
		doButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_EXECUTE_BUTTON));

		resetButton = new Button(btnComposite, SWT.NONE);
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oidText.setText("");
				setValueText.setText("");
				errorLabel.setText("");
			}
		});
		resetButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_RESET_BUTTON));

		clearButton = new Button(btnComposite, SWT.NONE);
		clearButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_CLEAR_BUTTON));
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resultText.setText("");
				errorLabel.setText("");
			}
		});

		resultGroupForm = new Group(tabItemComposite, SWT.NONE);
		resultGroupForm.setLayoutData(BorderLayout.CENTER);
		resultGroupForm.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_QUERY_EXECUTE_RESULT));
		resultGroupForm.setLayout(new FillLayout(SWT.VERTICAL));

		resultText = new Text(resultGroupForm, SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		resultText.setForeground(SWTResourceManager.getColor(0, 255, 0));
		resultText.setEditable(true);
		resultText.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));

		tabFolder.setSelection(cTabItem);

	}

	private void createToolBar(Composite treeComposite) {
		ToolBar toolBar = new ToolBar(treeComposite, SWT.FLAT);
		toolBar.setLayoutData(BorderLayout.NORTH);

		addSnmpToolItem = new ToolItem(toolBar, SWT.PUSH);
		addSnmpToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_ADD));
		addSnmpToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_ADD_ACTION));

		editSnmpToolItem = new ToolItem(toolBar, SWT.PUSH);
		editSnmpToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_EDIT));
		editSnmpToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_EDIT_ACTION));

		deleteSnmpToolItem = new ToolItem(toolBar, SWT.PUSH);
		deleteSnmpToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_DELETE));
		deleteSnmpToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_REMOVE_ACTION));

		testSnmpToolItem = new ToolItem(toolBar, SWT.PUSH);
		testSnmpToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_CONNECT));
		testSnmpToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TEST_ACTION));
		
		sendSnmpTrapToolItem = new ToolItem(toolBar, SWT.PUSH);
		sendSnmpTrapToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_TRAP_TEST));
		sendSnmpTrapToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_SNMP_SENDTRAP));
		
		receiveSnmpTrapToolItem = new ToolItem(toolBar, SWT.PUSH);
		receiveSnmpTrapToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_TRAP_RECEIVE));
		receiveSnmpTrapToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_SNMP_TRAPVIEW));

		toolBar.pack();
		// 注册工具栏触发事件
		addToolItemListener();
	}

	private void addToolItemListener() {
		testSnmpToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(testSnmpToolItem.getToolTipText());
				testSnmpAction();
			}
		});
		addSnmpToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(addSnmpToolItem.getToolTipText());
				addSnmpAction();
			}
		});
		editSnmpToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(editSnmpToolItem.getToolTipText());
				editSnmpAction();
			}
		});
		deleteSnmpToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(deleteSnmpToolItem.getToolTipText());
				deleteSnmpAction();
			}
		});
		
		sendSnmpTrapToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(sendSnmpTrapToolItem.getToolTipText());
				senpSnmpTrapAction(sendSnmpTrapToolItem.getToolTipText());
			}
		});
		receiveSnmpTrapToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(receiveSnmpTrapToolItem.getToolTipText());
				receiveSnmpTrapViewAction(receiveSnmpTrapToolItem.getToolTipText());
			}
		});
	}

	protected void receiveSnmpTrapViewAction(String name) {
//		TrapViewerDialog tvd = new TrapViewerDialog(getShell());
//		tvd.open();
		boolean isExsit = false;
		CTabItem[] items = tabFolder.getItems();
		for (CTabItem cTabItem : items) {
			if (cTabItem.getText().equalsIgnoreCase(name)) {
				tabFolder.setSelection(cTabItem);
				isExsit = true;
				break;
			}
		}
		if (!isExsit) {
			openTrapReceiveCTabItem(tabFolder,name);
		}
	
	}

	protected void senpSnmpTrapAction(String name) {
		// TrapSendDialog tsd = new TrapSendDialog(getShell());
		// tsd.open();
		boolean isExsit = false;
		CTabItem[] items = tabFolder.getItems();
		for (CTabItem cTabItem : items) {
			if (cTabItem.getText().equalsIgnoreCase(name)) {
				tabFolder.setSelection(cTabItem);
				isExsit = true;
				break;
			}
		}
		if (!isExsit) {
			openTrapSendCTabItem(tabFolder,name);
		}
	}

	protected void deleteSnmpAction() {
		TreeItem[] items = tree.getSelection();
		if (items.length > 0) {
			TreeItem item = tree.getSelection()[0];
			Object object = item.getData();
			if (object instanceof SnmpProvider) {
				if (MessageDialog.openConfirm(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_CONFIRM),
						ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONFIRM_DELETE_MSG))) {
					getSnmpProviderSet().remove(object);
					synchronizedFile(getSnmpProviderSet());
					item.dispose();
				}
			}
		}
	}

	protected void editSnmpAction() {
		TreeItem[] items = tree.getSelection();
		if (items.length > 0) {
			TreeItem item = tree.getSelection()[0];
			Object object = item.getData();
			if (object instanceof SnmpProvider) {
				SnmpProviderDialog snmpProviderDialog = new SnmpProviderDialog(getShell(), (SnmpProvider) object, 1);
				snmpProviderDialog.open();
			}
		}
	}

	protected void addSnmpAction() {
		SnmpProviderDialog snmpProviderDialog = new SnmpProviderDialog(getShell(), null, 0);
		snmpProviderDialog.open();
	}

	protected void testSnmpAction() {
		TreeItem[] items = tree.getSelection();
		if (items.length > 0) {
			TreeItem item = tree.getSelection()[0];
			Object object = item.getData();
			if (object instanceof SnmpProvider) {
				final SnmpProvider provider = (SnmpProvider)object;
				try {
					ModalContext.run(new IRunnableWithProgress() {
						public void run(final IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
							progressMonitor.beginTask(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_EXPORT), IProgressMonitor.UNKNOWN);
							boolean ok = CommonUtil.isHostConnectable(provider.getHost(), provider.getPort());
							if (ok) {
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), provider.getDisplayName()+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONNECT_SUCESSED));
									}
								});
							}else {
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), provider.getDisplayName()+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONNECT_FAILURE));
									}
								});
							}
							progressMonitor.done();
						}
					}, true, MdaAppWindow.getAppWindow().getStatusLineManager().getProgressMonitor(), getShell().getDisplay());
				} catch (final InvocationTargetException e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_MSG_ERROR, e);
							ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
							dislDialog.open();
						}
					});
					logger.error("InvocationTargetException :",e);
					//e.printStackTrace();
				} catch (final InterruptedException e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_MSG_ERROR, e);
							ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
							dislDialog.open();
						}
					});
					logger.error("InterruptedException :",e);
					//e.printStackTrace();
				}
			}
		}
		
	}

	protected void exportSnmpAction() {
		
	}

	private void addTreeContextMenuListener() {
		addMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addSnmpAction();
			}
		});
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				editSnmpAction();
			}
		});
		deleteMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteSnmpAction();
			}

		});
		testMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				testSnmpAction();
			}

		});
		expMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportSnmpAction();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		Object object = CommonUtil.readObjectFromFile(ResourceConstant.DATA_STORAGE_PATH_SNMP);
		if (null == object) {
			return;
		}
		snmpProviderSet = (Set<SnmpProvider>) object;
		for (SnmpProvider provider : snmpProviderSet) {
			TreeItem treeItem = new TreeItem(rooTreeItem, SWT.NONE);
			treeItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TREE_NODE));
			// treeItem.setText(provider.getDisplayName());
			treeItem.setText(provider.getDisplayName() + "(" + provider.getHost() + ")");
			treeItem.setExpanded(true);
			treeItem.setData(provider);
			tree.update();
		}
		rooTreeItem.setExpanded(true);

	}

	private void createTabFolder(SashForm sashForm) {
		tabComposite = new Composite(sashForm, SWT.NONE);
		tabComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new CTabFolder(tabComposite, SWT.BORDER | SWT.CLOSE | SWT.FLAT);
		tabFolder.setTabPosition(SWT.BOTTOM);
		tabFolder.setSimple(false);// 平滑模式

		SnmpProvider provider = new SnmpProvider();
		provider.setHost("127.0.0.1");
		provider.setPort(162);
		provider.setEnterpriseOID("1.3.6.4.12.36500");
		provider.setReadCommunity("public");
		provider.setWriteCommunity("public");
		provider.setDisplayName("Huawei");

//		 TreeItem item = new TreeItem(tree, SWT.None);
//		 item.setData(provider);
//		 item.setText(provider.getDisplayName());
//		
//		 openNewCTabItem(item,tabFolder,provider);
		//openTrapSendCTabItem(tabFolder,"TrapSend");
		//openTrapReceiveCTabItem(tabFolder,"TrapReceive");
	}

	public static void scanSnmpArray(SnmpProvider provider, int status) {
		provider.setDisplayName(provider.getDisplayName().length() == 0 ? provider.getHost() : provider.getDisplayName());
		getSnmpProviderSet().add(provider);
		synchronizedFile(getSnmpProviderSet());
		logger.info(provider);
		refreshTreeData(provider, status);
	}

	private static void refreshTreeData(SnmpProvider provider, int status) {
		if (status == 1) {// edit
			TreeItem[] items = tree.getSelection();
			if (items.length > 0) {
				TreeItem item = tree.getSelection()[0];
				Object object = item.getData();
				if (object instanceof SnmpProvider) {
					getSnmpProviderSet().remove(object);
					synchronizedFile(getSnmpProviderSet());
					item.dispose();
				}
			}
		}
		// add
		TreeItem treeItem = new TreeItem(rooTreeItem, SWT.NONE);
		treeItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TREE_NODE));
		// treeItem.setText(provider.getDisplayName().length()==0?provider.getHost():provider.getDisplayName()+"("+provider.getHost()+")");
		treeItem.setText(provider.getDisplayName() + "(" + provider.getHost() + ")");
		treeItem.setExpanded(true);
		treeItem.setData(provider);
		// TreeItem subTreeItem = new TreeItem(treeItem, SWT.NONE);
		// subTreeItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_NAMESPACE)
		// + provider.getNamespace());
		// subTreeItem.setExpanded(true);
		
		tree.update();
	}

	private static void synchronizedFile(Set<SnmpProvider> snmpProviderSet) {
		CommonUtil.writeObjectToFile(ResourceConstant.DATA_STORAGE_PATH_SNMP, snmpProviderSet);
	}

}
