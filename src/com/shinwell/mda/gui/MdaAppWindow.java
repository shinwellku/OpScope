package com.shinwell.mda.gui;


import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.shinwell.mda.gui.action.AboutAction;
import com.shinwell.mda.gui.composite.SmisComposite;
import com.shinwell.mda.gui.composite.SnmpComposite;
import com.shinwell.mda.gui.composite.SshComposite;
import com.shinwell.mda.gui.composite.WmiComposite;
import com.shinwell.mda.gui.dialog.AboutDialog;
import com.shinwell.mda.gui.dialog.SnapshotShell;
import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.util.BeanContextUtil;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;

/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2017年7月28日下午2:10:42
 * @Version: 1.0
 * @Desc:模拟调试助手工具(MDA)
 */
public class MdaAppWindow extends ApplicationWindow {
    static Logger logger = Logger.getLogger(MdaAppWindow.class.getSimpleName());
	static MdaAppWindow appWindow;
	Composite mainContainer;
	StackLayout stackLayout;
	Action smisAction,wmiAction,snmpAction,sshAction,aboutAction,exitAction,helpAction;
	Composite smisComposite,wmiComposite,snmpComposite,sshComposite;
	TrayItem trayItem;
	private Process process;
	private Action providerMgrAction;
	private Action hostMgrAction;
	private Action snmpMgrAction;
	private Action copyAction;
	private Action snapshotAction;
	private Action openAction;
	private Action saveAction;
	private Action toolbarAction;
	private Action zhAction;
	private Action enAction;
	private Tray tray;
	private Image trayItemImage;
	private StatusLineManager statusLineManager;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			MdaAppWindow window = new MdaAppWindow();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the application window.
	 */
	public MdaAppWindow() {
		super(null);
		appWindow = this;
		initContext();
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	private void initContext() {
		BeanContextUtil.getInstance();
	}

	
	private void addSystemTray(Shell newShell) {
		tray = Display.getCurrent().getSystemTray();
		if (null==tray) {
			System.err.println("system is not supported systemtray.");
			return;
		}
		final Menu trayMenu = createTrayMenu(Display.getCurrent(),newShell);
		trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_APP_NAME));
		trayItemImage = UIBuilderFactory.createImage(ResourceConstant.IMAGE_SYS_TRAY);
		trayItem.setImage(trayItemImage);
		trayItem.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				trayMenu.setVisible(true);
			}
		});
	}

	//创建系统托盘上下文菜单
	private Menu createTrayMenu(Display display, Shell shell) {
		Menu trayMenu = new Menu(shell,SWT.POP_UP);
		//welcome
		MenuItem welcomItem = new MenuItem(trayMenu, SWT.PUSH);
		welcomItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_WELCOME));
		welcomItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_SYSTEM_WELCOM));
		welcomItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_WELCOME));
			}
		});
		new MenuItem(trayMenu, SWT.SEPARATOR);
		
		//update
		MenuItem updateItem = new MenuItem(trayMenu, SWT.PUSH);
		updateItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_ONLINE_UPDATE));
		updateItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_SYSTEM_WELCOM));
		updateItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_UNDO));
			}
			
		});
		new MenuItem(trayMenu, SWT.SEPARATOR);
		
		//show
		MenuItem showItem = new MenuItem(trayMenu, SWT.PUSH);
		showItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SHOW));
		showItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_SYSTEM_WELCOM));
		showItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				appWindow.getShell().setVisible(true);
				appWindow.getShell().forceActive();
			}
			
		});
		
		new MenuItem(trayMenu, SWT.SEPARATOR);
		//hidden
		MenuItem hideItem = new MenuItem(trayMenu, SWT.PUSH);
		hideItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_HIDE));
		hideItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_SYSTEM_WELCOM));
		hideItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleShellCloseEvent();
			}
			
		});
		new MenuItem(trayMenu, SWT.SEPARATOR);
		//about
		MenuItem aboutItem = new MenuItem(trayMenu, SWT.PUSH);
		aboutItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_ABOUT_MSG));
		aboutItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_SYSTEM_WELCOM));
		aboutItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AboutDialog aboutDialog =  new AboutDialog(getShell());
				aboutDialog.open();
			}
			
		});
		new MenuItem(trayMenu, SWT.SEPARATOR);
		//exit
		MenuItem exitItem = new MenuItem(trayMenu, SWT.PUSH);
		exitItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_EXIT));
		exitItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_EXIT_ACTION));
		exitItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_CONFIRM), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONFIRM_EXIT_MSG))) {
					disposeResource();
				}
			}
			
		});
		trayMenu.setDefaultItem(welcomItem);
		return trayMenu;
	}

	protected void disposeResource() {
		if (process!=null) {
			process.destroy();
		}
		trayItemImage.dispose();
		trayItem.dispose();
		tray.dispose();
		MdaAppWindow.getAppWindow().close();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		mainContainer = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		mainContainer.setLayout(stackLayout);
		
		smisComposite = new SmisComposite(mainContainer, SWT.BORDER);
		wmiComposite = new WmiComposite(mainContainer, SWT.BORDER);
		snmpComposite = new SnmpComposite(mainContainer, SWT.BORDER);
		sshComposite = new SshComposite(mainContainer, SWT.BORDER);
		
        stackLayout.topControl = smisComposite;
		return mainContainer;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// smisAction = new SmisAction();
		// wmiAction = new WmiAction();
		// snmpAction = new SnmpAction();
		// sshAction = new SshAction();
		aboutAction = new AboutAction(getShell());
		smisAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_SMIS), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_SMIS_ACTION)) {
			public void run() {
				//System.out.println(getText());
				MdaAppWindow.getAppWindow().getStatusLineManager().setMessage("");
				stackLayout.topControl = smisComposite;
				mainContainer.layout();
			}

		});
//		wmiAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_WMI), UIBuilderFactory
//				.createImageDescriptor(ResourceConstant.IMAGE_WMI_ACTION)) {
//			public void run() {
//				System.out.println(getText());
//				stackLayout.topControl = wmiComposite;
//				mainContainer.layout();
//			}
//
//		});
		snmpAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_SNMP), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_SNMP_ACTION)) {
			public void run() {
				//System.out.println(getText());
				stackLayout.topControl = snmpComposite;
				mainContainer.layout();
			}

		});
		sshAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_SSH), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_SSH_ACTION)) {
			public void run() {
				//System.out.println(getText());
				stackLayout.topControl = sshComposite;
				mainContainer.layout();
			}

		});
		exitAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_EXIT), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_EXIT_ACTION)) {
			public void run() {
				//System.out.println(getText());
				if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_CONFIRM),
						ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONFIRM_EXIT_MSG))) {
					// System.exit(0);
					MdaAppWindow.getAppWindow().close();
				}
			}
		});
		helpAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_HELP_DOC), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_HELP_ACTION)) {
			public void run() {
				//System.out.println(getText());
				try {
					if (process == null) {
						process = Runtime.getRuntime().exec("hh.exe conf/MDA-HELP.chm");
					} else {
						process.destroy();
						process = Runtime.getRuntime().exec("hh.exe conf/MDA-HELP.chm");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		providerMgrAction = copyAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_SMIS_MGR), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_MGR_ACTION)) {
			public void run() {
				//System.out.println(getText());
				MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_UNDO));
			}
		});
		hostMgrAction = copyAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_SSH_MGR), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_MGR_ACTION)) {
			public void run() {
				//System.out.println(getText());
				MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_UNDO));
			}
		});
		snmpMgrAction = copyAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_SNMP_MGR), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_MGR_ACTION)) {
			public void run() {
				//System.out.println(getText());
				MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_UNDO));
			}
		});
		copyAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_COPY), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_COPY_ACTION)) {
			public void run() {
				//System.out.println(getText());
				MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_UNDO));
			}
		});
		snapshotAction =  UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_CUT), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_CUT_ACTION)) {
			public void run() {
				//System.out.println(getText());
		        Display display= Display.getDefault(); 
		        Shell shell=new Shell(display,SWT.NO_TRIM|SWT.TOOL);  
		        Rectangle di = display.getBounds();  
		        Image temps = new Image(display, di.width, di.height);  
		        GC gc = new GC(display);  
		        gc.copyArea(temps, 0, 0);  
		        gc.dispose();  
		        shell.setSize(di.width,di.height);  
		        shell.setLocation(0,0);  
		        GridLayout grid=new GridLayout();  
		        grid.marginHeight=grid.marginLeft=grid.marginRight=grid.marginTop=0;  
		        grid.marginWidth=0;  
		        shell.setLayout(grid);  
		        SnapshotShell temp=new SnapshotShell(shell,temps);  
		        temp.setLayoutData(new GridData(GridData.FILL_BOTH));  
		        shell.setVisible(true);  
		        shell.open();  
			}
		});
		openAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_OPEN)) {
			public void run() {
				//System.out.println(getText());
				MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_UNDO));
			}
		});
		saveAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_SAVE), UIBuilderFactory
				.createImageDescriptor(ResourceConstant.IMAGE_SAVE_ACTION)) {
			public void run() {
				//System.out.println(getText());
				MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_UNDO));
			}
		});
		toolbarAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_TOOLBAR)) {
			public void run() {
				//System.out.println(getText());
				if (toolbarAction.isChecked()) {
					this.setChecked(false);
					getToolBarManager().getControl().setVisible(!this.isChecked());
					getToolBarManager().getControl().getParent().layout(true);
					getToolBarManager().getControl().dispose();
				}else {
					this.setChecked(true);
					addToolBar(SWT.FLAT | SWT.WRAP);
				}
		
				getToolBarManager().update(true);
				Display.getCurrent().update();
				getShell().layout();
				getShell().redraw();
				getShell().update();
			}
		});
		toolbarAction.setChecked(true);
		
		zhAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_LANGUAGE_ZH)) {
			public void run() {
				//System.out.println(getText());
				ResourceBundleUtil.changeLang(ResourceConstant.LANGUAGE_LOCAL_ZH_CN);
				this.setChecked(true);
				enAction.setChecked(false);
			}
		});
		zhAction.setChecked(true);
		enAction = UIBuilderFactory.createAction(new Action(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_LANGUAGE_EN)) {
			public void run() {
				//System.out.println(getText());
				ResourceBundleUtil.changeLang(ResourceConstant.LANGUAGE_LOCAL_EN_US);
				this.setChecked(true);
				zhAction.setChecked(false);
			}
		});
		enAction.setChecked(false);
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuBar = new MenuManager("");
        MenuManager fileMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_FILE),"1");
//        MenuManager fileMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_FILE),UIBuilderFactory.createImageDescriptor(ResourceConstant.IMAGE_FILE_ACTION),"1");
		MenuManager editMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_EDIT),"2");
//		MenuManager editMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_EDIT),UIBuilderFactory.createImageDescriptor(ResourceConstant.IMAGE_EDIT_ACTION),"2");
        MenuManager viewMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_VIEW),"3");
//        MenuManager viewMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_VIEW),UIBuilderFactory.createImageDescriptor(ResourceConstant.IMAGE_VIEW_ACTION),"3");
        MenuManager configMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_CONFIG),"4");
//        MenuManager configMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_CONFIG),UIBuilderFactory.createImageDescriptor(ResourceConstant.IMAGE_CONFIG_ACTION),"4");
       // MenuManager operateMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_OPERATOR),UIBuilderFactory.createImageDescriptor(ResourceConstant.IMAGE_OPERATOR_ACTION),"5");
        MenuManager helpMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_HELP),"6");
//        MenuManager helpMenu = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_HELP),UIBuilderFactory.createImageDescriptor(ResourceConstant.IMAGE_HELP_ACTION),"6");

        menuBar.add(fileMenu);
        fileMenu.add(openAction);
        fileMenu.add(new Separator());
        fileMenu.add(saveAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);
        
        menuBar.add(editMenu);  
        editMenu.add(copyAction);
        editMenu.add(new Separator());
        editMenu.add(snapshotAction);
        
        menuBar.add(viewMenu);  
        viewMenu.add(toolbarAction);
        viewMenu.add(new Separator());
        MenuManager languageMenuManager = new MenuManager(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_LANGUAGE),UIBuilderFactory.createImageDescriptor(ResourceConstant.IMAGE_LANGUAGE_ACTION),"7");
        viewMenu.add(languageMenuManager);
        languageMenuManager.add(zhAction);
        languageMenuManager.add(enAction);
        viewMenu.add(new Separator());
        viewMenu.add(smisAction);
        viewMenu.add(new Separator());
        viewMenu.add(snmpAction);
        viewMenu.add(new Separator());
        viewMenu.add(sshAction);
        
        menuBar.add(configMenu);
        configMenu.add(providerMgrAction);
        configMenu.add(new Separator());
        configMenu.add(snmpMgrAction);
        configMenu.add(new Separator());
        configMenu.add(hostMgrAction);
     
        
//        menuBar.add(operateMenu); 
//        operateMenu.add(smisAction);
//        operateMenu.add(new Separator());
//        operateMenu.add(snmpAction);
////        operateMenu.add(new Separator());
////        operateMenu.add(wmiAction);
//        operateMenu.add(new Separator());
//        operateMenu.add(sshAction);
        
        menuBar.add(helpMenu);
        helpMenu.add(helpAction);
        helpMenu.add(new Separator());
        helpMenu.add(aboutAction);
        
		return menuBar;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		toolBarManager.add(exitAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(smisAction);
		toolBarManager.add(new Separator());
//		toolBarManager.add(wmiAction);
//		toolBarManager.add(new Separator());
		toolBarManager.add(snmpAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(sshAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(snapshotAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(helpAction);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		statusLineManager = new StatusLineManager();
		statusLineManager.add(new Separator());
		
		statusLineManager.add(new Separator());
		StatusLineContributionItem authorItem = new StatusLineContributionItem("1");
		authorItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_AUTHOR_NAME)+"Shinwell Ku");
		statusLineManager.add(authorItem);
		
		statusLineManager.add(new Separator());
		StatusLineContributionItem item = new StatusLineContributionItem("2");
		item.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_VERSION_LABEL)+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_APP_VERSION));
		statusLineManager.add(item);
		
		StatusLineContributionItem copyrightItem = new StatusLineContributionItem("3");
		copyrightItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CLIENT_COPY_RIGHT_LABEL)+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_COPY_RIGHT));
		statusLineManager.add(copyrightItem);
		
//		statusLineManager.add(new Separator());
//		StatusLineContributionItem blankItem = new StatusLineContributionItem("4");
//		blankItem.setText(" ");
//		statusLineManager.add(blankItem);
		
		statusLineManager.add(new Separator());
		statusLineManager.add(snapshotAction);
		statusLineManager.add(new Separator());		
		return statusLineManager;
	}

	
	public StatusLineManager getStatusLineManager() {
		return statusLineManager;
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_APP_NAME));
		//newShell.setMinimumSize(1366, 768);
		newShell.setMinimized(false);
		newShell.setMaximized(true);//设置最大化
		newShell.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_APP_LOGO));
		UIBuilderFactory.setCenter(newShell);
		//newShell.setBounds(Display.getCurrent().getPrimaryMonitor().getBounds());
		//create system tray
		addSystemTray(newShell);
		newShell.forceActive();
	}

	public static MdaAppWindow getAppWindow() {
		return appWindow;
	}


	@Override
	protected void handleShellCloseEvent() {
		getShell().setVisible(false);
		ToolTip toolTip = new ToolTip(getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);
		toolTip.setAutoHide(true);  
		toolTip.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_WELCOME));
		toolTip.setMessage(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SYSTEM_TRAY_MSG));
		trayItem.setToolTip(toolTip);
		toolTip.setVisible(true);
	}

	
	
	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(UIBuilderFactory.getScreenWidth(), UIBuilderFactory.getScreenHeight());
	}
}
