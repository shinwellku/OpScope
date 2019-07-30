package com.shinwell.mda.gui.composite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.cim.CIMInstance;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger16;
import javax.wbem.WBEMException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import swing2swt.layout.BorderLayout;

import com.shinwell.mda.api.ArrayCimAPI;
import com.shinwell.mda.api.CimAPI;
import com.shinwell.mda.api.FabricCimAPI;
import com.shinwell.mda.api.TapeCimAPI;
import com.shinwell.mda.domain.FabricModel;
import com.shinwell.mda.domain.SmisProvider;
import com.shinwell.mda.domain.SwitchModel;
import com.shinwell.mda.gui.MdaAppWindow;
import com.shinwell.mda.gui.dialog.ArrayPerfermanceChartDialog;
import com.shinwell.mda.gui.dialog.CustomQueryDialog;
import com.shinwell.mda.gui.dialog.SmisProviderDialog;
import com.shinwell.mda.gui.dialog.SwitchPerfermanceChartDialog;
import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.service.SmisService;
import com.shinwell.mda.util.BeanContextUtil;
import com.shinwell.mda.util.CommonUtil;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;

/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2017年7月28日下午2:43:24
 * @Version: 1.0
 * @Desc:
 */
public class SmisComposite extends Composite{
	static Logger logger = Logger.getLogger(SmisComposite.class.getSimpleName());
	SmisService smisService = BeanContextUtil.getInstance().getSmisService();
	Map<String, String> dedicatedMap = BeanContextUtil.getInstance().getDedicatedMap();
	//Button addSmisBtn, editSmisBtn, deleteSmisBtn,testSmisBtn,discoverSmisBtn;
	private ToolItem addSmisToolItem;
	private ToolItem editSmisToolItem;
	private ToolItem deleteSmisToolItem;
	private ToolItem testSmisToolItem;
	private ToolItem discovSmisToolItem;
	private Menu treeContextMenu;
	private MenuItem testMenuItem;
	private MenuItem discoverMenuItem;
	private MenuItem addMenuItem;
	private MenuItem editMenuItem;
	private MenuItem deleteMenuItem;
	private MenuItem customQueryMenuItem;
	private MenuItem expMenuItem;
	static Set<SmisProvider> smisProviderSet = new HashSet<SmisProvider>();
	static Tree tree;
	static TreeItem rooTreeItem;
	private Composite tabComposite;
	private CTabFolder tabFolder;
	public static String associatorClass = null;
	public static String resultClass = null;
	public static int returnCode = -1;

	public static Set<SmisProvider> getSmisProviderSet() {
		return smisProviderSet;
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SmisComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new BorderLayout(0, 0));
		
		SashForm sashForm = new SashForm(composite, SWT.SMOOTH);
		sashForm.setLayoutData(BorderLayout.CENTER);
		sashForm.setSashWidth(3);

		//left content
		createTreeComposite(sashForm);
		//right content
		createTabFolder(sashForm);
		
		sashForm.setWeights(new int[] { 25, 75 });
	}

	private void createTabFolder(SashForm sashForm) {
		tabComposite = new Composite(sashForm, SWT.NONE);
		tabComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new CTabFolder(tabComposite, SWT.CLOSE | SWT.FLAT|SWT.BORDER);
		tabFolder.setTabPosition(SWT.BOTTOM);
		tabFolder.setSimple(false);//平滑模式
	}

	private void createTreeComposite(SashForm sashForm) {
		Composite treeComposite = new Composite(sashForm, SWT.BORDER);
		treeComposite.setLayout(new BorderLayout(5, 5));
		
		createToolBar(treeComposite);
		
		tree = new Tree(treeComposite, SWT.FULL_SELECTION);
		tree.setSortDirection(SWT.UP);
		tree.setLayoutData(BorderLayout.CENTER);
		

		rooTreeItem = new TreeItem(tree, SWT.NONE);
		rooTreeItem.setText("CIMOM-ROOT");
		rooTreeItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TREE_ROOT));
		rooTreeItem.setExpanded(true);
		
		treeContextMenu = new Menu(tree);
		tree.setMenu(treeContextMenu);
		
		
		testMenuItem = new MenuItem(treeContextMenu, SWT.NONE);
		testMenuItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_CONNECT));
		testMenuItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TEST_ACTION));
		
		new MenuItem(treeContextMenu, SWT.SEPARATOR);
		
		discoverMenuItem = new MenuItem(treeContextMenu, SWT.NONE);
		discoverMenuItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_DISCOVER));
		discoverMenuItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_DISCOVER_ACTION));
		
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
		
		customQueryMenuItem = new MenuItem(treeContextMenu, SWT.NONE);
		customQueryMenuItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_CUSTOM_QUERY));
		customQueryMenuItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_DISCOVER_ACTION));
		
		new MenuItem(treeContextMenu, SWT.SEPARATOR);
		
		expMenuItem = new MenuItem(treeContextMenu, SWT.NONE);
		expMenuItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_EXPORT));
		expMenuItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_EXPORT_ACTION));
		
		addTreeContextMenuListener();
		
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TreeItem item = (TreeItem) event.item;
				if (item.getText().equals("CIMOM-ROOT")) {
					addMenuItem.setEnabled(true);
					editMenuItem.setEnabled(false);
					deleteMenuItem.setEnabled(false);
					testMenuItem.setEnabled(false);
					discoverMenuItem.setEnabled(false);
					customQueryMenuItem.setEnabled(false);
					expMenuItem.setEnabled(false);
				}else if (item.getData() instanceof SmisProvider) {
					addMenuItem.setEnabled(false);
					editMenuItem.setEnabled(true);
					deleteMenuItem.setEnabled(true);
					testMenuItem.setEnabled(true);
					discoverMenuItem.setEnabled(true);
					customQueryMenuItem.setEnabled(false);
					expMenuItem.setEnabled(false);
				}else if (item.getData() instanceof CIMInstance) {
					addMenuItem.setEnabled(false);
					editMenuItem.setEnabled(false);
					deleteMenuItem.setEnabled(false);
					testMenuItem.setEnabled(false);
					discoverMenuItem.setEnabled(false);
					customQueryMenuItem.setEnabled(true);
					expMenuItem.setEnabled(true);
				}else {
					addMenuItem.setEnabled(false);
					editMenuItem.setEnabled(false);
					deleteMenuItem.setEnabled(false);
					testMenuItem.setEnabled(false);
					discoverMenuItem.setEnabled(false);
					customQueryMenuItem.setEnabled(false);
					expMenuItem.setEnabled(false);
				}
				
			}
		});
		
		//双击事件逻辑处理
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent event) {
				TreeItem item = tree.getSelection()[0];
				if (item.getData() instanceof CIMInstance) {
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
						SmisProvider provider = null;
						TreeItem treeItem = item.getParentItem().getParentItem();
						if (null!=treeItem&&!treeItem.getText().equalsIgnoreCase("CIMOM-ROOT")) {
							provider = (SmisProvider) treeItem.getData();
						}else{
							provider = (SmisProvider) item.getParentItem().getData();
						}
						openNewCTabItem(null,item,tabFolder,(CIMInstance)item.getData(),provider,null);
					}
					
				}
			}
		});
		initData();
	}
	private void createToolBar(Composite treeComposite) {
		ToolBar toolBar = new ToolBar(treeComposite, SWT.NONE);
		toolBar.setLayoutData(BorderLayout.NORTH);
		
		addSmisToolItem = new ToolItem(toolBar, SWT.PUSH);
		//addSmisToolItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_ADD));
		addSmisToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_ADD));
		addSmisToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_ADD_ACTION));
		
		editSmisToolItem = new ToolItem(toolBar, SWT.PUSH);
		//editSmisToolItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_EDIT));
		editSmisToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_EDIT));
		editSmisToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_EDIT_ACTION));
		
		deleteSmisToolItem = new ToolItem(toolBar, SWT.PUSH);
		//deleteSmisToolItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_DELETE));
		deleteSmisToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_DELETE));
		deleteSmisToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_REMOVE_ACTION));
		
		testSmisToolItem = new ToolItem(toolBar, SWT.PUSH);
		//testSmisToolItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_CONNECT));
		testSmisToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_CONNECT));
		testSmisToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TEST_ACTION));
		
		discovSmisToolItem = new ToolItem(toolBar, SWT.PUSH);
		//discovSmisToolItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_DISCOVER));
		discovSmisToolItem.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TOOLITEM_DISCOVER));
		discovSmisToolItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_DISCOVER_ACTION));

		toolBar.pack();
		//注册工具栏触发事件
		addToolItemListener();
	}
	private void createTapeLibraryToolItem(final TreeItem selectedItem, ToolBar smisOperToolBar, final Table instanceTable,final Table configTable,final Table perfTable,final SmisProvider smisProvider,final CIMInstance systemComputer,final TableColumn cimInstanceColumn) {
		ToolItem TapeLibraryItem = new ToolItem(smisOperToolBar, SWT.NONE);
		TapeLibraryItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TAPE_LIBRARY));
		
		ToolItem changerItem = new ToolItem(smisOperToolBar, SWT.NONE);
		changerItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TAPE_CHANGER));
		
		ToolItem dirverItem = new ToolItem(smisOperToolBar, SWT.NONE);
		dirverItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TAPE_DRIVER));
		
		ToolItem portItem = new ToolItem(smisOperToolBar, SWT.NONE);
		portItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TAPE_PORT));
		
		ToolItem meadiaItem = new ToolItem(smisOperToolBar, SWT.NONE);
		meadiaItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_TAPE_MEADIA));
		
		TapeLibraryItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_TAPE_LIBRARY,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		
		changerItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_TAPE_CHANGER,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		dirverItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_TAPE_DRIVER,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		portItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_TAPE_PORT,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		meadiaItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_TAPE_MEADIA,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		
		handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_TAPE_LIBRARY,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
	}
	private void createFabricToolItem(final TreeItem selectedItem, ToolBar smisOperToolBar, final Table instanceTable,final Table configTable,final Table perfTable,final SmisProvider smisProvider,final CIMInstance systemComputer,final TableColumn cimInstanceColumn) {
		ToolItem fabricItem = new ToolItem(smisOperToolBar, SWT.NONE);
		fabricItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_FABRIC));
		
		ToolItem zoneSetItem = new ToolItem(smisOperToolBar, SWT.NONE);
		zoneSetItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_ZONE_CONFIG));
		
		ToolItem zoneItem = new ToolItem(smisOperToolBar, SWT.NONE);
		zoneItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_ZONE));
		
		ToolItem zoneAliasItem = new ToolItem(smisOperToolBar, SWT.NONE);
		zoneAliasItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_ZONEALIAS));
		
		ToolItem remoteItem = new ToolItem(smisOperToolBar, SWT.NONE);
		remoteItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_REMOTE_DEVICE));
		
		fabricItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_FABRIC,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		
		zoneSetItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_ZONE_CONFIG,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		zoneItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_ZONE,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		zoneAliasItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_ZONEALIAS,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		remoteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_REMOTE_DEVICE,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		
		handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_FABRIC,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
	}
	private void createSwitchToolItem(final TreeItem selectedItem, ToolBar smisOperToolBar, final Table instanceTable,final Table configTable,final Table perfTable,final SmisProvider smisProvider,final CIMInstance systemComputer,final TableColumn cimInstanceColumn) {
		ToolItem switchItem = new ToolItem(smisOperToolBar, SWT.NONE);
		switchItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_SWITCH));
		
		ToolItem moudleItem = new ToolItem(smisOperToolBar, SWT.NONE);
		moudleItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_SWITCH_MODULE));
		
		ToolItem portItem = new ToolItem(smisOperToolBar, SWT.NONE);
		portItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_SWITCH_PORT));
		
		ToolItem powerItem = new ToolItem(smisOperToolBar, SWT.NONE);
		powerItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_SWITCH_POWERSUPPLY));
		
		ToolItem fanItem = new ToolItem(smisOperToolBar, SWT.NONE);
		fanItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_SWITCH_FAN));
		
		switchItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(false);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_SWITCH,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		moudleItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(false);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_SWITCH_MODULE,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		portItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(true);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_SWITCH_PORT,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		powerItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(false);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_SWITCH_POWERSUPPLY,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		fanItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(false);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_SWITCH_FAN,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		
		handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_SWITCH,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
	}
	private void createStorageToolItem(final TreeItem selectedItem, ToolBar smisOperToolBar, final Table instanceTable, final Table configTable,final Table perfTable,final SmisProvider smisProvider,final CIMInstance systemComputer,final TableColumn cimInstanceColumn) {
		ToolItem subsystemItem = new ToolItem(smisOperToolBar, SWT.NONE);
		subsystemItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_SUBSYSTEM));
		
		ToolItem controllerItem = new ToolItem(smisOperToolBar, SWT.NONE);
		controllerItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_CONTROLLER));
		
		ToolItem poolItem = new ToolItem(smisOperToolBar, SWT.NONE);
		poolItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_POOL));
		
		ToolItem diskItem = new ToolItem(smisOperToolBar, SWT.NONE);
		diskItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_DISK));
		
		ToolItem volumeItem = new ToolItem(smisOperToolBar, SWT.NONE);
		volumeItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_VOLUME));
		
		ToolItem portItem = new ToolItem(smisOperToolBar, SWT.NONE);
		portItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PORT));
		
		ToolItem powerItem = new ToolItem(smisOperToolBar, SWT.NONE);
		powerItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_POWERSUPPLY));
		
		ToolItem betteryItem = new ToolItem(smisOperToolBar, SWT.NONE);
		betteryItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_BETTERY));
		
		ToolItem fanItem = new ToolItem(smisOperToolBar, SWT.NONE);
		fanItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_FAN));
		
		subsystemItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(true);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_SUBSYSTEM,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		
		controllerItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(true);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_CONTROLLER,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		poolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(false);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_POOL,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		diskItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(true);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_DISK,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		volumeItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(true);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_VOLUME,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		portItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(true);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_PORT,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		powerItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(false);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_POWERSUPPLY,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		betteryItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(false);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_BETTERY,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		fanItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instanceTable.getMenu().getItem(1).setEnabled(false);
				handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_FAN,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
			}
		});
		handleToolBarItemEvent(selectedItem,ResourceConstant.COMPONET_TYPE_SUBSYSTEM,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn,null);
	}

	//根据选择的不同节点类型打开一个新的 tabitem
	protected void openNewCTabItem(CIMInstance selectCimInstance, final TreeItem selectedItem, CTabFolder tabFolder, final CIMInstance systemComputer, final SmisProvider smisProvider, List<CIMInstance> instances) {
		//new SmisContentComposite(this, SWT.NONE, selectedItem, tabFolder, systemComputer, smisProvider);
		CTabItem cTabItem = new CTabItem(tabFolder, SWT.NONE);
		cTabItem.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
			}
		});
		
		Composite tabItemComposite = new Composite(tabFolder, SWT.NONE);
		cTabItem.setControl(tabItemComposite);
		tabItemComposite.setLayout(new BorderLayout(0, 0));
		
		SashForm contentSashForm = new SashForm(tabItemComposite, SWT.SMOOTH);
		contentSashForm.setLayoutData(BorderLayout.CENTER);
		
		final Table instanceTable = new Table(contentSashForm, SWT.BORDER | SWT.FULL_SELECTION);
		instanceTable.setHeaderVisible(true);
		instanceTable.setLinesVisible(true);
		instanceTable.setToolTipText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_DOUBLE_SEE_DATAIL));
		
		
		TableColumn indexColumn = new TableColumn(instanceTable, SWT.NONE);
		//indexColumn.setWidth(200);
		indexColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_INDEX));
		indexColumn.setMoveable(true);
		indexColumn.pack();
		
		final TableColumn cimInstanceColumn = new TableColumn(instanceTable, SWT.NONE);
		cimInstanceColumn.setWidth(200);
		cimInstanceColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_INSTANCE_NAME));
		cimInstanceColumn.setMoveable(true);
		
		SashForm kpiSashForm = new SashForm(contentSashForm, SWT.VERTICAL);
		
		final Table configTable = new Table(kpiSashForm, SWT.BORDER | SWT.FULL_SELECTION);
		configTable.setHeaderVisible(true);
		configTable.setLinesVisible(true);
		
		TableColumn configKeyColumn = new TableColumn(configTable, SWT.NONE);
		configKeyColumn.setWidth(200);
		configKeyColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONFIG_KEY));
		configKeyColumn.setMoveable(true);
		
		TableColumn configValueColumn = new TableColumn(configTable, SWT.NONE);
		configValueColumn.setWidth(200);
		configValueColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONFIG_KEY_VALUE));
		configValueColumn.setMoveable(true);
		
		TableColumn configTypeColumn = new TableColumn(configTable, SWT.NONE);
		configTypeColumn.setWidth(200);
		configTypeColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONFIG_KEY_DATATYPE));
		configTypeColumn.setMoveable(true);
		
		final Table perfTable = new Table(kpiSashForm, SWT.BORDER | SWT.FULL_SELECTION);
		perfTable.setHeaderVisible(true);
		perfTable.setLinesVisible(true);
		
		TableColumn perfKeyColumn = new TableColumn(perfTable, SWT.NONE);
		perfKeyColumn.setWidth(200);
		perfKeyColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_PERF_KEY));
		perfKeyColumn.setMoveable(true);
		
		TableColumn perfValueColumn = new TableColumn(perfTable, SWT.NONE);
		perfValueColumn.setWidth(200);
		perfValueColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_PERF_KEY_VALUE));
		perfValueColumn.setMoveable(true);
		
		TableColumn perfTypeColumn = new TableColumn(perfTable, SWT.NONE);
		perfTypeColumn.setWidth(200);
		perfTypeColumn.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_PERF_KEY_DATATYPE));
		perfTypeColumn.setMoveable(true);
		
		kpiSashForm.setWeights(new int[] {1, 1});
		contentSashForm.setWeights(new int[] {35, 65});
		
		tabFolder.setSelection(cTabItem);
		//自定义查询
		if (null!=selectCimInstance) {
			instanceTable.setData(smisProvider);
			cTabItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_CUSTOM_QUERY)+":"+selectCimInstance.getClassName());
			cTabItem.setToolTipText(selectCimInstance.getObjectPath().toString());
			this.handleToolBarItemEvent(selectedItem, ResourceConstant.COMPONET_TYPE_CUSTOM_QUERY, instanceTable, configTable, perfTable, smisProvider, systemComputer, cimInstanceColumn,instances);
		}else {
			ToolBar smisOperToolBar = new ToolBar(tabItemComposite, SWT.FLAT | SWT.RIGHT|SWT.SHADOW_OUT);
			smisOperToolBar.setLayoutData(BorderLayout.NORTH);
			cTabItem.setText(selectedItem.getText());
			cTabItem.setToolTipText(((CIMInstance)selectedItem.getData()).getObjectPath().toString());
			if (selectedItem.getText().contains("Library")) {
				createTapeLibraryToolItem(selectedItem,smisOperToolBar,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn);
			}else if(selectedItem.getText().contains("Storage")) {
				createStorageToolItem(selectedItem,smisOperToolBar,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn);
			}else if (selectedItem.getText().contains("Switch")) {
				createSwitchToolItem(selectedItem,smisOperToolBar,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn);
			}else if (((CIMInstance)selectedItem.getData()).getClassName().contains("Fabric")) {
				createFabricToolItem(selectedItem,smisOperToolBar,instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn);
			} 
			smisOperToolBar.pack();
		}
		tabItemComposite.layout();
		instanceTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (instanceTable.getSelectionIndex()>=0) {
					TableItem item = instanceTable.getItem(instanceTable.getSelectionIndex());
					if (null!=item) {
						TextTransfer textTransfer = TextTransfer.getInstance();
						String tmp = "";
						for (int i = 0; i < instanceTable.getColumnCount(); i++) {
							tmp = tmp.concat(" "+item.getText(i));
						}
						logger.debug("Clipboard:"+tmp);
						Clipboard clipboard = new Clipboard(getDisplay());
						clipboard.clearContents();
						clipboard.setContents(new String[]{tmp}, new Transfer[] {textTransfer});
						clipboard.dispose();
					}
					handleInstanceTableEvent(instanceTable,configTable,perfTable,smisProvider,systemComputer,cimInstanceColumn);
				}
			}
			
		});
		configTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem item = configTable.getItem(configTable.getSelectionIndex());
				if (null!=item) {
					TextTransfer textTransfer = TextTransfer.getInstance();
					String tmp = "";
					for (int i = 0; i < configTable.getColumnCount(); i++) {
						tmp = tmp.concat(" "+item.getText(i));
					}
					logger.debug("Clipboard:"+tmp);
					Clipboard clipboard = new Clipboard(getDisplay());
					clipboard.clearContents();
					clipboard.setContents(new String[]{tmp}, new Transfer[] {textTransfer});
					clipboard.dispose();
				}
			}
			
		});
		perfTable.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem item = perfTable.getItem(perfTable.getSelectionIndex());
				if (null!=item) {
					TextTransfer textTransfer = TextTransfer.getInstance();
					String tmp = "";
					for (int i = 0; i < perfTable.getColumnCount(); i++) {
						tmp = tmp.concat(" "+item.getText(i));
					}
					logger.debug("Clipboard:"+tmp);
					Clipboard clipboard = new Clipboard(getDisplay());
					clipboard.clearContents();
					clipboard.setContents(new String[]{tmp}, new Transfer[] {textTransfer});
					clipboard.dispose();
				}
			}
		});

		//create table's contextmenu
		Menu tableContextMenu = createInstanceContextMenu(instanceTable,tabFolder,systemComputer,smisProvider);
		instanceTable.setMenu(tableContextMenu);
		
	}

	//处理表格实例双击事件
	protected void handleInstanceTableEvent(Table instanceTable, final Table configTable, final Table perfTable, final SmisProvider smisProvider, CIMInstance systemComputer, TableColumn cimInstanceColumn) {
		//实例表格选中的实例
		final TableItem item = instanceTable.getItem(instanceTable.getSelectionIndex());
		final String type = (String) instanceTable.getData();//用于判断当前保存的数据组件类型
		configTable.removeAll();
		perfTable.removeAll();
		if (null!=item&&item.getData()!=null&&item.getData() instanceof CIMInstance) {
			final CIMInstance instance = (CIMInstance) item.getData();
			try {
				ModalContext.run(new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
						progressMonitor.beginTask(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_UI_QUERY_TIP), progressMonitor.UNKNOWN);
						// final CIMInstance instance = (CIMInstance)
						// item.getData();
						
						CimAPI cimAPI = new CimAPI();
						try {
							cimAPI.getWbemClient(smisProvider);
							ArrayCimAPI arrayCimAPI = new ArrayCimAPI(cimAPI);
							FabricCimAPI fabricCimAPI = new FabricCimAPI(cimAPI);
							TapeCimAPI tapeCimAPI =  new TapeCimAPI(cimAPI);
							// config
							CIMProperty<?>[] configProperties = instance.getProperties();
							
							if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SUBSYSTEM)) {
								CIMInstance _instance1 = arrayCimAPI.queryPhysicalPackage(instance);
								CIMInstance _instance2 = arrayCimAPI.querySoftwareIdentity(instance);
								if (null != _instance1) {
									configProperties = (CIMProperty<?>[]) ArrayUtils.addAll(_instance1.getProperties(), configProperties);
								}
								if (null != _instance2) {
									configProperties = (CIMProperty<?>[]) ArrayUtils.addAll(_instance2.getProperties(), configProperties);
								}
							} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_DISK)) {
								CIMInstance _instance1 = arrayCimAPI.queryDiskPhysicalPackage(instance);
								if (null != _instance1) {
									configProperties = (CIMProperty<?>[]) ArrayUtils.addAll(_instance1.getProperties(), configProperties);
								}
							} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH)) {
								CIMInstance _instance1 = fabricCimAPI.querySwitchSoftware(instance);
								CIMInstance _instance2 = fabricCimAPI.querySwitchPhysicalPackages(instance);
								CIMInstance _instance3 = fabricCimAPI.querySwitchAccessPoint(instance);
								if (null != _instance1) {
									configProperties = (CIMProperty<?>[]) ArrayUtils.addAll(_instance1.getProperties(), configProperties);
								}
								if (null != _instance2) {
									configProperties = (CIMProperty<?>[]) ArrayUtils.addAll(_instance2.getProperties(), configProperties);
								}
								if (null != _instance3) {
									configProperties = (CIMProperty<?>[]) ArrayUtils.addAll(_instance3.getProperties(), configProperties);
								}
							} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_ZONEALIAS)) {
								List<CIMInstance> _instance1 = fabricCimAPI.queryZoneMembersInAlias(instance);
								for (CIMInstance cimInstance : _instance1) {
									configProperties = (CIMProperty<?>[]) ArrayUtils.addAll(cimInstance.getProperties(), configProperties);
								}
							} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_TAPE_LIBRARY)) {
								List<CIMInstance> _instance1 = tapeCimAPI.queryRemoteAccessPoint(instance);
								List<CIMInstance> _instance2 = tapeCimAPI.querySoftwareIdentity(instance);
								for (CIMInstance cimInstance : _instance1) {
									configProperties = (CIMProperty<?>[]) ArrayUtils.addAll(cimInstance.getProperties(), configProperties);
								}
								for (CIMInstance cimInstance : _instance2) {
									configProperties = (CIMProperty<?>[]) ArrayUtils.addAll(cimInstance.getProperties(), configProperties);
								}
							}
							final List<String[]> configList = new ArrayList<String[]>();
							for (int i = 0; i < configProperties.length; i++) {
								String propertyName = "";
								String propertyValue = "";
								String propertyDataType = "";
								CIMProperty<?> cimProperty = configProperties[i];
								propertyName = cimProperty.getName();
								propertyDataType = cimProperty.getDataType().toString();
								if (cimProperty.getValue() != null) {
									if (cimProperty.getDataType().isArray()) {
										propertyValue = Arrays.asList((Object[]) cimProperty.getValue()).toString();
									} else {
										propertyValue = String.valueOf(cimProperty.getValue());
									}
								}
								configList.add(new String[] { propertyName, propertyValue, propertyDataType });
							}
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									for (String[] strings : configList) {
										TableItem tableItem = new TableItem(configTable, SWT.NONE);
										tableItem.setText(strings);
										tableItem.setData(instance);
									}
									// 重新布局表格列，否则需要设置大小
									for (int j = 0; j < configTable.getColumnCount(); j++) {
										configTable.getColumn(j).pack();
									}
								}
							});

							// performance
							CIMInstance perfInstance = instance;
							final List<String[]> perfList = new ArrayList<String[]>();
							if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SUBSYSTEM) 
									|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_CONTROLLER)
									|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_DISK) 
									|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_VOLUME)
									|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_PORT)
									|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH_PORT)
									|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_CUSTOM_QUERY)) {

								if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_DISK)) {
									CIMInstance _instance = arrayCimAPI.queryStatisticalData(instance);
									if (null == _instance) {
										_instance = arrayCimAPI.queryStorageExtent(instance);
										if (_instance != null) {
											perfInstance = arrayCimAPI.queryStatisticalData(_instance);
										} else {
											perfInstance = null;
										}
									}else {
										perfInstance = _instance;
									}
								} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH_PORT)) {
									perfInstance = fabricCimAPI.queryStatisticalData(instance);
								} else {
									perfInstance = arrayCimAPI.queryStatisticalData(instance);
								}

								if (null != perfInstance) {
									CIMProperty<?>[] perfProperties = perfInstance.getProperties();
									for (int i = 0; i < perfProperties.length; i++) {
										String propertyName = "";
										String propertyValue = "";
										String propertyDataType = "";
										CIMProperty<?> cimProperty = perfProperties[i];
										propertyName = cimProperty.getName();
										propertyDataType = cimProperty.getDataType().toString();
										if (cimProperty.getDataType().isArray()) {
											propertyValue = Arrays.asList((Object[]) cimProperty.getValue()).toString();
										} else {
											propertyValue = String.valueOf(cimProperty.getValue());
										}
										perfList.add(new String[] { propertyName, propertyValue, propertyDataType });
										
									}
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											for (String[] strings : perfList) {
												TableItem tableItem = new TableItem(perfTable, SWT.NONE);
												tableItem.setText(strings);
												//tableItem.setData(perfInstance);
											}
											// 重新布局表格列，否则需要设置大小
											for (int j = 0; j < perfTable.getColumnCount(); j++) {
												perfTable.getColumn(j).pack();
											}
										}
									});
									
								}

							}
						} catch (final WBEMException e1) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_CONNECT_FAILURE, e1);
									ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
									dislDialog.open();
								}
							});
							e1.printStackTrace();
						} finally {
							cimAPI.close();
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
				e.printStackTrace();
			} catch (final InterruptedException e) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_MSG_ERROR, e);
						ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
						dislDialog.open();
					}
				});
				e.printStackTrace();
			}
		}
	
	}

	//创建实例表格的上下文菜单
	private Menu createInstanceContextMenu(final Table instanceTable, final CTabFolder tabFolder, final CIMInstance systemComputer, final SmisProvider smisProvider) {
		Menu instanceTableMenu = new Menu(instanceTable);
		MenuItem customQueryItem = new MenuItem(instanceTableMenu, SWT.None);
		customQueryItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_CUSTOM_QUERY));
		customQueryItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e1) {
				if (instanceTable.getSelectionIndex()==-1) {
					MessageDialog.openWarning(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_WARNING), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_SELECT_DATA));
					return;
				}
				TableItem item = instanceTable.getItem(instanceTable.getSelectionIndex());
				if (null != item && item.getData() != null && item.getData() instanceof CIMInstance) {
					final CIMInstance instance = (CIMInstance) item.getData();
					executeCustomQuery(instance,systemComputer,smisProvider);
				}

			}

		});
		final String type = (String) instanceTable.getData();
		if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SUBSYSTEM) 
				|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_CONTROLLER)
				|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_DISK) 
				|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_VOLUME)
				|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_PORT)
				|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH)
				|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH_PORT)){
			MenuItem prfItem = new MenuItem(instanceTableMenu, SWT.None);
			prfItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS));
			if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH)) {
				prfItem.setEnabled(false);
			}
			prfItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e1) {
					if (instanceTable.getSelectionIndex()==-1) {
						MessageDialog.openWarning(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_WARNING), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_SELECT_DATA));
						return;
					}
					TableItem item = instanceTable.getItem(instanceTable.getSelectionIndex());
					if (null != item && item.getData() != null && item.getData() instanceof CIMInstance) {
						final CIMInstance instance = (CIMInstance) item.getData();
						if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH)
							|| type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH_PORT)){
							openSwitchPerfermenceLineChart(instance,systemComputer,smisProvider);
						}else {
							openArrayPerfermenceLineChart(instance,systemComputer,smisProvider);
						}
						
					}

				}

			});
			
		}
		
		return instanceTableMenu;
	}

	protected void openSwitchPerfermenceLineChart(CIMInstance instance, CIMInstance systemComputer, SmisProvider smisProvider) {
		SwitchPerfermanceChartDialog dialog = new SwitchPerfermanceChartDialog(getShell(), instance, systemComputer,smisProvider);
		returnCode = dialog.open();
	}

	//性能曲线
	protected void openArrayPerfermenceLineChart(CIMInstance instance, CIMInstance systemComputer, SmisProvider smisProvider) {
		ArrayPerfermanceChartDialog dialog = new ArrayPerfermanceChartDialog(getShell(), instance, systemComputer,smisProvider);
		returnCode = dialog.open();
	}

	protected void executeCustomQuery(final CIMInstance instance, final CIMInstance systemComputer, final SmisProvider smisProvider) {
		try {
			ModalContext.run(new IRunnableWithProgress() {
				public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
					final CimAPI cimAPI = new CimAPI();
					progressMonitor.beginTask(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_UI_QUERY_TIP), progressMonitor.UNKNOWN);
					try {
						cimAPI.getWbemClient(smisProvider);
						final String[] items = cimAPI.queryClassNames(null);
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								try {
									CustomQueryDialog dialog = new CustomQueryDialog(getShell(), instance, items);
									returnCode = dialog.open();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});

					} catch (WBEMException e1) {
						e1.printStackTrace();
					} finally {
						cimAPI.close();
					}
					progressMonitor.done();
				}
			}, true, MdaAppWindow.getAppWindow().getStatusLineManager().getProgressMonitor(), getShell().getDisplay());
			
			if (IDialogConstants.OK_ID == returnCode) {
				ModalContext.run(new IRunnableWithProgress() {
					public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
						final CimAPI cimAPI = new CimAPI();
						progressMonitor.beginTask(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_UI_QUERY_TIP), progressMonitor.UNKNOWN);
						try {
							cimAPI.getWbemClient(smisProvider);
							resultClass = (resultClass.isEmpty()||resultClass==null)?null:resultClass;
							final List<CIMInstance> instances = cimAPI.associatorInstances(instance.getObjectPath(), associatorClass, resultClass);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									try {
										logger.info("associatorClass=" + associatorClass + "  resultClass=" + resultClass);
										if (null==instances||instances.isEmpty() || instances.size() == 0) {
											MessageDialog.openWarning(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_WARNING),
													ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_NO_DATA));
											return;
										}
										openNewCTabItem(instance, null, tabFolder, systemComputer, smisProvider, instances);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});

						} catch (WBEMException e1) {
							e1.printStackTrace();
						} finally {
							cimAPI.close();
						}
						progressMonitor.done();
					}
				}, true, MdaAppWindow.getAppWindow().getStatusLineManager().getProgressMonitor(), getShell().getDisplay());
			}
			
		} catch (final InvocationTargetException e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_MSG_ERROR, e);
					ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
					dislDialog.open();
				}
			});
			e.printStackTrace();
		} catch (final InterruptedException e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_MSG_ERROR, e);
					ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
					dislDialog.open();
				}
			});
			e.printStackTrace();
		}

	}

	//处理不同按钮的操作逻辑
	private void handleToolBarItemEvent(final TreeItem selectedItem, final String type, final Table instanceTable, Table configTable, Table perfTable, final SmisProvider smisProvider, final CIMInstance topInstance, final TableColumn cimInstanceColumn, final List<CIMInstance> resultInstances) {
		instanceTable.removeAll();
		configTable.removeAll();
		perfTable.removeAll();
		final String clomnName = ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_INSTANCE_NAME);
		final Object _topInstance = null==selectedItem?null:selectedItem.getParentItem().getData();
		final CimAPI cimAPI = new CimAPI();
		try {
			ModalContext.run(new IRunnableWithProgress() {
				List<CIMInstance> instances = null;
				String key = "Name";
				@Override
				public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
					progressMonitor.beginTask(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_UI_QUERY_TIP), progressMonitor.UNKNOWN);
					try {
						cimAPI.getWbemClient(smisProvider);
						final ArrayCimAPI arrayCimAPI = new ArrayCimAPI(cimAPI);
						final FabricCimAPI fabricCimAPI = new FabricCimAPI(cimAPI);
						final TapeCimAPI tapeCimAPI = new TapeCimAPI(cimAPI);
						if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SUBSYSTEM)) {
							instances = new ArrayList<>();
							instances.add(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_CONTROLLER)) {
							instances = arrayCimAPI.queryController(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_POOL)) {
							instances = arrayCimAPI.queryPool(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_DISK)) {
							instances = arrayCimAPI.queryDisk(topInstance);
							key = "Name";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_VOLUME)) {
							instances = arrayCimAPI.queryVolume(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_PORT)) {
							List<CIMInstance> controllerInstances = arrayCimAPI.queryController(topInstance);
							instances = new ArrayList<CIMInstance>();
							key = "ElementName";
							for (CIMInstance controllerCimInstance : controllerInstances) {
								instances.addAll(arrayCimAPI.queryPort(controllerCimInstance));
							}
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_POWERSUPPLY)) {
							instances = arrayCimAPI.queryPower(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_BETTERY)) {
							instances = arrayCimAPI.queryBattery(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_FAN)) {
							instances = arrayCimAPI.queryFan(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_FABRIC)) {
							instances = new ArrayList<>();
							instances.add(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_ZONE_CONFIG)) {
							instances = fabricCimAPI.queryZoneSet(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_ZONE)) {
							instances = fabricCimAPI.queryZone(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_ZONEALIAS)) {
							instances = fabricCimAPI.queryAllZoneAlias(topInstance);
							key = "CollectionAlias";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_REMOTE_DEVICE)) {
							instances = fabricCimAPI.queryBrocadePlatform(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH)) {
							instances = fabricCimAPI.querySwitch((CIMInstance)_topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH_MODULE)) {
							instances = new ArrayList<CIMInstance>();
							List<CIMInstance> moduleInstances = fabricCimAPI.queryModule(topInstance);
							for (CIMInstance moduleInstance : moduleInstances) {
								instances.addAll(fabricCimAPI.queryBlade(moduleInstance));
							}
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH_PORT)) {
							instances = new ArrayList<CIMInstance>();
							List<CIMInstance> moduleInstances = fabricCimAPI.queryModule(topInstance);
							if (moduleInstances.size()>0) {
								for (CIMInstance moduleInstance : moduleInstances) {
									instances.addAll(fabricCimAPI.queryPort(moduleInstance));
								}
							}else{
								List<CIMInstance> ports = fabricCimAPI.queryPortWithoutModule(topInstance);
								if (ports.size()>0) {
									instances.addAll(ports);
								}else {
									instances.addAll(fabricCimAPI.queryPortWithExtendPort(topInstance));
								}
								
							}
							
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH_POWERSUPPLY)) {
							instances = fabricCimAPI.queryPowerSupply(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH_FAN)) {
							instances = fabricCimAPI.queryFan(topInstance);
							key = "ElementName";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_TAPE_LIBRARY)) {
							instances = new ArrayList<>();
							instances.add(topInstance);
							key = "Name";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_TAPE_DRIVER)) {
							instances = tapeCimAPI.queryTapeDrive(topInstance);
							key = "DeviceID";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_TAPE_CHANGER)) {
							instances = tapeCimAPI.queryChangerDevice(topInstance);
							key = "DeviceID";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_TAPE_PORT)) {
							instances = tapeCimAPI.queryFCPorts(topInstance);
							key = "PermanentAddress";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_TAPE_MEADIA)) {
							instances = tapeCimAPI.getPhysicalTape(topInstance);
							key = "Caption";
						} else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_CUSTOM_QUERY)) {
							key = "Name";
							instances = resultInstances;
						}
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								cimInstanceColumn.setText(clomnName + "(" + ResourceBundleUtil.getValueByKey(type) + ")");
								if (instances.size() > 0) {
									instanceTable.setData(type);// 用于判断当前表格数据是什么类型的数据
									for (int i=0;i<instances.size();i++) {
										CIMInstance cimInstance = instances.get(i);
										TableItem tableItem = new TableItem(instanceTable, SWT.NONE);
										if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_BETTERY)) {
											Object object = cimInstance.getPropertyValue(key);
											if (null == object) {
												object = cimInstance.getPropertyValue("DeviceID");
											}
											tableItem.setText(new String[] { String.valueOf(i+1), object.toString() });
										}else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_SWITCH_PORT)||type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_REMOTE_DEVICE)) {
											Object object = cimInstance.getPropertyValue(key);
											if (null == object||"".equalsIgnoreCase(object.toString())) {
												object = cimInstance.getPropertyValue("DeviceID");
											}
											tableItem.setText(new String[] { String.valueOf(i+1), object.toString() });
										}else if (type.equalsIgnoreCase(ResourceConstant.COMPONET_TYPE_CUSTOM_QUERY)) {
											Object object = cimInstance.getPropertyValue(key);
											if (null == object||"".equalsIgnoreCase(object.toString())) {
												object = cimInstance.getPropertyValue("ElementName");
											}
											if (null == object||"".equalsIgnoreCase(object.toString())) {
												object = cimInstance.getPropertyValue("DeviceID");
											}
											if (null == object||"".equalsIgnoreCase(object.toString())) {
												object = cimInstance.getPropertyValue("InstanceID");
											}
											if (null == object||"".equalsIgnoreCase(object.toString())) {
												object = cimInstance.getClassName();
											}
											if (null != object && !"".equalsIgnoreCase(object.toString())) {
												tableItem.setText(new String[] { String.valueOf(i+1), object.toString() });
											}
											
										}else {
											tableItem.setText(new String[] { String.valueOf(i+1), cimInstance.getPropertyValue(key).toString() });
										}
										tableItem.setData(cimInstance);
									}
									// 重新布局表格列，否则需要设置大小
									for (int j = 0; j < instanceTable.getColumnCount(); j++) {
										instanceTable.getColumn(j).pack();
									}
								} else {
									MessageDialog.openWarning(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_WARNING),
											ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_NO_DATA));
								}
							}
						});
					} catch (final Exception e) {
						logger.error("handleToolBarItemEvent", e);
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
									Status status = new Status(IStatus.ERROR,"1", 1,ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR),e);
									ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR),ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), status,IStatus.ERROR);
									dislDialog.open();
							}
						});
					}finally{
						cimAPI.close();
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
			e.printStackTrace();
		} catch (final InterruptedException e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_MSG_ERROR, e);
					ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
					dislDialog.open();
				}
			});
			e.printStackTrace();
		}
	}
	private void addTreeContextMenuListener() {
		addMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addSmisAction();
			}
		});
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				editSmisAction();
			}
		});
		deleteMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteSmisAction();
			}
			
		});
		testMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				testSmisAction();
			}

		});
		discoverMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				discoverSmisAction();
			}
		});
		customQueryMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				customQuerySmisAction();
			}
		});
		expMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportSmisAction();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		Object object = CommonUtil.readObjectFromFile(ResourceConstant.DATA_STORAGE_PATH_SMIS);
		if (null==object) {
			return;
		}
		smisProviderSet = (Set<SmisProvider>) object;
		for (SmisProvider provider : smisProviderSet) {
			TreeItem treeItem = new TreeItem(rooTreeItem, SWT.NONE);
			treeItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TREE_NODE));
			treeItem.setText(provider.getDisplayName());
			treeItem.setExpanded(true);
			treeItem.setData(provider);
			TreeItem subTreeItem = new TreeItem(treeItem, SWT.NONE);
			subTreeItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_NAMESPACE) + provider.getNamespace());
			subTreeItem.setExpanded(true);
			tree.update();
		}
		rooTreeItem.setExpanded(true);

	}

	private void addSmisAction() {
		SmisProviderDialog smisProviderDialog = new SmisProviderDialog(getShell(), null,0);
		smisProviderDialog.open();
	}
	private void editSmisAction() {
		TreeItem[] items = tree.getSelection();
		if (items.length>0) {
			TreeItem item = tree.getSelection()[0];
			Object object = item.getData();
			if (object instanceof SmisProvider) {
				SmisProviderDialog smisProviderDialog = new SmisProviderDialog(getShell(), (SmisProvider) object,1);
				smisProviderDialog.open();
			}
		}
	}
	private void deleteSmisAction() {
		TreeItem[] items = tree.getSelection();
		if (items.length>0) {
			TreeItem item = tree.getSelection()[0];
			Object object = item.getData();
			if (object instanceof SmisProvider) {
				if (MessageDialog.openConfirm(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_CONFIRM), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONFIRM_DELETE_MSG))) {
					getSmisProviderSet().remove(object);
					synchronizedFile(getSmisProviderSet());
					item.dispose();
				}
			}
		}
	}
	
	private void testSmisAction() {
		TreeItem item = tree.getSelection()[0];
		Object object = item.getData();
		if (object instanceof SmisProvider) {
			final SmisProvider provider = (SmisProvider) object;
			final CimAPI api = new CimAPI();
			try {
				ModalContext.run(new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
						progressMonitor.beginTask(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_UI_CONNECT_TIP), IProgressMonitor.UNKNOWN);
						try {
							api.getWbemClient(provider.getProtocol(), provider.getHost(), String.valueOf(provider.getPort()), provider.getNamespace(), provider.getUserName(), provider.getPassword());
							if (api.testConnection()) {
								//非UI现成访问UI线程
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), provider.getDisplayName()+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONNECT_SUCESSED));
									}
								});
								
							}
						} catch (final Exception e) {
							logger.error("error", e);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
										Status status = new Status(IStatus.ERROR,"1", 1,ResourceConstant.I18N_KEY_CONNECT_FAILURE,e);
										ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), provider.getDisplayName()+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_TEST_FAIL_DETAIL), status,IStatus.ERROR);
										dislDialog.open();
								}
							});
						}finally{
							api.close();
						}
						progressMonitor.done();
					}
				}, true, MdaAppWindow.getAppWindow().getStatusLineManager().getProgressMonitor(), getShell().getDisplay());
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
//			//对于耗时的操作放到非UI线程中处理
//				new Thread(){
//					@Override
//					public void run() {
//						try {
//							api.getWbemClient(provider.getProtocol(), provider.getHost(), String.valueOf(provider.getPort()), provider.getNamespace(), provider.getUserName(), provider.getPassword());
//							if (api.testConnection()) {
//								//非UI现成访问UI线程
//								Display.getDefault().asyncExec(new Runnable() {
//									public void run() {
//										MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), provider.getDisplayName()+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CONNECT_SUCESSED));
//									}
//								});
//								
//							}
//						} catch (final Exception e) {
//							logger.error("error", e);
//							Display.getDefault().asyncExec(new Runnable() {
//								public void run() {
//										Status status = new Status(IStatus.ERROR,"1", 1,ResourceConstant.I18N_KEY_CONNECT_FAILURE,e);
//										ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), provider.getDisplayName()+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_TEST_FAIL_DETAIL), status,IStatus.ERROR);
//										dislDialog.open();
//								}
//							});
//						}finally{
//							api.close();
//						}
//						
//					}
//					
//				}.start();
		}
		
	}
	
	private void discoverSmisAction() {
		final TreeItem item = tree.getSelection()[0];
		Object object = item.getData();
		TreeItem[] items = item.getItems();
		for (TreeItem treeItem : items) {
			if (treeItem.getData() instanceof CIMInstance) {
				treeItem.dispose();
			}
		}
		if (object instanceof SmisProvider) {
			final SmisProvider provider = (SmisProvider) object;
			final CimAPI api = new CimAPI();
			try {
				ModalContext.run(new IRunnableWithProgress() {
					public void run(final IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
						progressMonitor.beginTask(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_UI_DISCOVER_TIP), IProgressMonitor.UNKNOWN);
						try {
							api.getWbemClient(provider.getProtocol(), provider.getHost(), String.valueOf(provider.getPort()), provider.getNamespace(), provider.getUserName(), provider.getPassword());
							ArrayCimAPI arrayCimAPI = new ArrayCimAPI(api);
							final FabricCimAPI fabricCimAPI = new FabricCimAPI(api);
							List<CIMInstance> fabriCimInstances = null;
							List<CIMInstance> storageInstances = null;
							if (api.getPath().getNamespace().toLowerCase().contains("brocade")) {
								fabriCimInstances = fabricCimAPI.queryFabric();
							} else {
								storageInstances = arrayCimAPI.queryComputerSystem();
							}
							final List<FabricModel> fabricList = new ArrayList<FabricModel>();
							if (null != fabriCimInstances && fabriCimInstances.size() > 0) {
								for (final CIMInstance fabricInstance : fabriCimInstances) {
									if (!fabricInstance.getClassName().contains("Fabric"))
										continue;
									final String fabricName = fabricInstance.getPropertyValue("Name").toString();
									FabricModel fabricModel = new FabricModel();
									fabricModel.setFabricName(fabricName);
									fabricModel.setFabricData(fabricInstance);
									fabricList.add(fabricModel);
									ArrayList<SwitchModel> switchModels = new ArrayList<SwitchModel>();

									List<CIMInstance> switchCimInstances = fabricCimAPI.querySwitch(fabricInstance);
									for (final CIMInstance switchInstance : switchCimInstances) {
										String name = switchInstance.getPropertyValue("ElementName").toString();
										String tmp = "[";
										UnsignedInteger16[] Dedicated = (UnsignedInteger16[]) switchInstance.getPropertyValue("Dedicated");
										for (int i = 0; i < Dedicated.length; i++) {
											UnsignedInteger16 unsignedInteger16 = Dedicated[i];
											if (i != (Dedicated.length - 1)) {
												tmp = tmp.concat(dedicatedMap.get(unsignedInteger16.toString().trim())).concat(",");
											} else {
												tmp = tmp.concat(dedicatedMap.get(unsignedInteger16.toString().trim()));
											}
										}
										tmp = tmp.concat("]");
										SwitchModel switchModel = new SwitchModel();
										switchModel.setSwitchName(name + tmp);
										switchModel.setSwithData(switchInstance);
										switchModels.add(switchModel);
										fabricModel.setSwitchList(switchModels);
									}

								}

								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										for (FabricModel fabricModel : fabricList) {
											// fabric node
											TreeItem fabricTreeItem = new TreeItem(item, SWT.NONE);
											fabricTreeItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TREE_FABRIC));
											fabricTreeItem.setText(fabricModel.getFabricName());
											fabricTreeItem.setData(fabricModel.getFabricData());
											List<SwitchModel> switchModels = fabricModel.getSwitchList();
											// switch node
											for (SwitchModel switchModel : switchModels) {
												TreeItem subTreeItem = new TreeItem(fabricTreeItem, SWT.NONE);
												subTreeItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TREE_INSTANCE));
												subTreeItem.setText(switchModel.getSwitchName());
												subTreeItem.setData(switchModel.getSwithData());
												subTreeItem.setExpanded(true);
											}
											//fabricTreeItem.setExpanded(true);
										}
										item.setExpanded(true);
									}
								});
							} else if (null != storageInstances && storageInstances.size() > 0) {
								for (final CIMInstance cimInstance : storageInstances) {
									final String name = cimInstance.getPropertyValue("ElementName").toString();
									final String _name = cimInstance.getPropertyValue("Name").toString();
									if (cimInstance.getClassName().contains("Processor") 
											|| cimInstance.getClassName().contains("PhysicalComputerSystem")
											|| cimInstance.getClassName().contains("Processor")
											||cimInstance.getClassName().contains("Management")
											||cimInstance.getClassName().contains("ServerSystem")
											||name.contains("FCB"))//带库router
										continue;
									
									String tmp = "[";
									UnsignedInteger16[] Dedicated = (UnsignedInteger16[]) cimInstance.getPropertyValue("Dedicated");
									for (int i = 0; i < Dedicated.length; i++) {
										UnsignedInteger16 unsignedInteger16 = Dedicated[i];
										if (i != (Dedicated.length - 1)) {
											tmp = tmp.concat(dedicatedMap.get(unsignedInteger16.toString().trim())).concat(",");
										} else {
											tmp = tmp.concat(dedicatedMap.get(unsignedInteger16.toString().trim()));
										}
									}
									tmp = tmp.concat("]");
									final String ss = tmp;
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											TreeItem subTreeItem = new TreeItem(item, SWT.NONE);
											subTreeItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TREE_INSTANCE));
											subTreeItem.setText(ss.contains("Library")?(_name + ss):(name + ss));
											subTreeItem.setData(cimInstance);
											subTreeItem.setExpanded(true);
											item.setExpanded(true);
										}
									});
								}
							}
						} catch (final WBEMException e) {
							logger.error("error", e);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_CONNECT_FAILURE, e);
									ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
									dislDialog.open();
								}
							});
						} finally {
							api.close();
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
				e.printStackTrace();
			} catch (final InterruptedException e) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_MSG_ERROR, e);
						ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
						dislDialog.open();
					}
				});
				e.printStackTrace();
			}
		}
	}
	protected void customQuerySmisAction() {
		TreeItem item = tree.getSelection()[0];
		if (item.getData() instanceof CIMInstance) {
				SmisProvider provider = null;
				TreeItem treeItem = item.getParentItem().getParentItem();
				if (null!=treeItem&&!treeItem.getText().equalsIgnoreCase("CIMOM-ROOT")) {
					provider = (SmisProvider) treeItem.getData();
				}else{
					provider = (SmisProvider) item.getParentItem().getData();
				}
				//openNewCTabItem(null,item,tabFolder,(CIMInstance)item.getData(),provider,null);
				executeCustomQuery((CIMInstance)item.getData(),(CIMInstance)item.getData(),provider);
		}
		
	}
	//导出原数据
	protected void exportSmisAction() {
		final TreeItem selectedItem = tree.getSelection()[0];
		if (selectedItem.getData() instanceof CIMInstance) {
			final SmisProvider smisProvider;
			final CIMInstance instance = (CIMInstance) selectedItem.getData();
			final String selectedName = selectedItem.getText();
			final String selectedClassName = instance.getClassName();
			TreeItem treeItem = selectedItem.getParentItem().getParentItem();
			if (null != treeItem && !treeItem.getText().equalsIgnoreCase("CIMOM-ROOT")) {
				smisProvider = (SmisProvider) treeItem.getData();
			} else {
				smisProvider = (SmisProvider) selectedItem.getParentItem().getData();
			}

			//MessageDialog.openInformation(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_INFO), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_UNDO));

			DirectoryDialog dd = new DirectoryDialog(getShell());
			dd.setMessage(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_UI_EXPORT_DIALOG_TITLE));
			dd.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_UI_EXPORT_DIALOG_TITLE));
			final String saveDir = dd.open();
			if (null != saveDir) {
				System.out.println(saveDir);
				try {
					ModalContext.run(new IRunnableWithProgress() {
						public void run(final IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
							progressMonitor.beginTask(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_EXPORT), IProgressMonitor.UNKNOWN);
							if (selectedName.contains("Storage")) {
								exportStorageData(instance, smisProvider, saveDir, selectedName);
							} else if (selectedName.contains("Switch")) {
								exportSwitchData(instance, smisProvider, saveDir, selectedName);
							} else if (selectedClassName.contains("Fabric")) {
								exportFabricData(instance, smisProvider, saveDir, selectedName);
							} else if (selectedName.contains("Library")) {
								exportTapeLibraryData(instance, smisProvider, saveDir, selectedName);
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
					e.printStackTrace();
				} catch (final InterruptedException e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							Status status = new Status(IStatus.ERROR, "1", 1, ResourceConstant.I18N_KEY_MSG_ERROR, e);
							ErrorDialog dislDialog = new ErrorDialog(getShell(), ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_ERROR), "", status, IStatus.ERROR);
							dislDialog.open();
						}
					});
					e.printStackTrace();
				}
			}
		}
	}
	
	//带库数据导出
	private void exportTapeLibraryData(CIMInstance instance, SmisProvider smisProvider, String saveDir, String selectedName) {
		CimAPI api = new CimAPI();
		ArrayList<CIMInstance> instances = new ArrayList<>();
		try {
			api.getWbemClient(smisProvider);
			TapeCimAPI tapeCimAPI = new TapeCimAPI(api);
			instances.add(instance);// tapeLibrary

			List<CIMInstance> tapeDrives = tapeCimAPI.queryTapeDrive(instance);// TapeDrive
			instances.addAll(tapeDrives);

			List<CIMInstance> changers = tapeCimAPI.queryChangerDevice(instance);// Changer
			instances.addAll(changers);

			List<CIMInstance> ports = tapeCimAPI.queryFCPorts(instance);// ports
			instances.addAll(ports);

			List<CIMInstance> tapes = tapeCimAPI.getPhysicalTape(instance);// tapes
			instances.addAll(tapes);

			//selectedName = selectedName.substring(0,selectedName.indexOf("["));
			selectedName = selectedName.replace(":", "_");
			CommonUtil.zipFile(saveDir, selectedName, instances);

		} catch (WBEMException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			api.close();
		}

	}
	//fabric数据导出
	private void exportFabricData(CIMInstance instance, SmisProvider smisProvider, String saveDir, String selectedName) {
		CimAPI api = new CimAPI();
		ArrayList<CIMInstance> instances = new ArrayList<>();
		try {
			api.getWbemClient(smisProvider);
			FabricCimAPI fabricCimAPI = new FabricCimAPI(api);
			instances.add(instance);//Fabric
			
			List<CIMInstance> zoneSetInstances = fabricCimAPI.queryZoneSet(instance);//zoneSet
			instances.addAll(zoneSetInstances);
			
			List<CIMInstance> zones = fabricCimAPI.queryZone(instance);//zone
			instances.addAll(zones);
			
			List<CIMInstance> zoneAlias = fabricCimAPI.queryAllZoneAlias(instance);//ZoneAlias
			instances.addAll(zoneAlias);
			
			List<CIMInstance> endNodes = fabricCimAPI.queryBrocadePlatform(instance);//endNodes 对端节点
			instances.addAll(endNodes);
			
			CommonUtil.zipFile(saveDir, selectedName, instances);
			
		} catch (WBEMException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			api.close();
		}
	
	}
	//交换机数据导出
	private void exportSwitchData(CIMInstance instance, SmisProvider smisProvider, String saveDir, String selectedName) {
		CimAPI api = new CimAPI();
		ArrayList<CIMInstance> instances = new ArrayList<>();
		try {
			api.getWbemClient(smisProvider);
			FabricCimAPI fabricCimAPI = new FabricCimAPI(api);
			instances.add(instance);//switch
			
			List<CIMInstance> moduleInstances = fabricCimAPI.queryModule(instance);//module
			for (CIMInstance moduleInstance : moduleInstances) {
				instances.add(moduleInstance);
				instances.addAll(fabricCimAPI.queryBlade(moduleInstance));//blade
			}
			
//			for (CIMInstance moduleInstance : moduleInstances) {
//				instances.addAll(fabricCimAPI.queryPort(moduleInstance));//port
//			}
			if (moduleInstances.size()>0) {
				for (CIMInstance moduleInstance : moduleInstances) {
					instances.addAll(fabricCimAPI.queryPort(moduleInstance));
				}
			}else{
				List<CIMInstance> ports = fabricCimAPI.queryPortWithoutModule(instance);
				if (ports.size()>0) {
					instances.addAll(ports);
				}else {
					instances.addAll(fabricCimAPI.queryPortWithExtendPort(instance));
				}
				
			}
			List<CIMInstance> powerSupplys = fabricCimAPI.queryPowerSupply(instance);//PowerSupply
			instances.addAll(powerSupplys);
			
			List<CIMInstance> fans = fabricCimAPI.queryFan(instance);//fan
			instances.addAll(fans);
			
			//selectedName = selectedName.substring(0,selectedName.indexOf("["));
			selectedName = selectedName.replace(":", "_");
			CommonUtil.zipFile(saveDir, selectedName, instances);
			
		} catch (WBEMException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			api.close();
		}
	
	}

	//存储数据导出
	private void exportStorageData(CIMInstance instance, SmisProvider smisProvider, String saveDir, String selectedName) {
		CimAPI api = new CimAPI();
		ArrayList<CIMInstance> instances = new ArrayList<>();
		try {
			api.getWbemClient(smisProvider);
			ArrayCimAPI arrayCimAPI = new ArrayCimAPI(api);
			instances.add(instance);//subsustem
			CIMInstance physicalPackage = arrayCimAPI.queryPhysicalPackage(instance);
			CIMInstance softwareIdentity = arrayCimAPI.querySoftwareIdentity(instance);
			
			instances.add(physicalPackage);
			instances.add(softwareIdentity);
			
			List<CIMInstance> controllers = arrayCimAPI.queryController(instance);//controller
			instances.addAll(controllers);
			for (CIMInstance controllerCimInstance : controllers) {
				List portsList = arrayCimAPI.queryPort(controllerCimInstance);
				if (portsList.size()>0) {
					instances.addAll(portsList);
				}
			}
			List<CIMInstance> pools = arrayCimAPI.queryPool(instance);//pools
			instances.addAll(pools);
			
			List<CIMInstance> disks = arrayCimAPI.queryDisk(instance);
			for (Iterator iterator = disks.iterator(); iterator.hasNext();) {
				CIMInstance disk = (CIMInstance) iterator.next();
				CIMInstance _disk = arrayCimAPI.queryDiskPhysicalPackage(disk);
				instances.add(disk);
				instances.add(_disk);
			}
			List<CIMInstance> volumes = arrayCimAPI.queryVolume(instance);
			instances.addAll(volumes);
			
			List<CIMInstance> powerSupplys = arrayCimAPI.queryPower(instance);
			if (powerSupplys.size()>0) {
				instances.addAll(powerSupplys);
			}
			
			List<CIMInstance> batterys = arrayCimAPI.queryBattery(instance);
			if (batterys.size()>0) {
				instances.addAll(batterys);
			}
			//selectedName = selectedName.substring(0,selectedName.indexOf("["));
			selectedName = selectedName.replace(":", "_");
			CommonUtil.zipFile(saveDir, selectedName, instances);
			
		} catch (WBEMException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			api.close();
		}
	}

	private void addToolItemListener() {
		testSmisToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(testSmisToolItem.getToolTipText());
				testSmisAction();
			}
		});
		discovSmisToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(discovSmisToolItem.getToolTipText());
				discoverSmisAction();
			}
		});
		addSmisToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(addSmisToolItem.getToolTipText());
				addSmisAction();
			}
		});
		editSmisToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(editSmisToolItem.getToolTipText());
				editSmisAction();
			}
		});
		deleteSmisToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				logger.info(deleteSmisToolItem.getToolTipText());
				deleteSmisAction();
			}
		});

	}


	public static void scanSmisArray(SmisProvider provider, int status) {
		provider.setDisplayName(provider.getDisplayName().length()==0?provider.getHost():provider.getDisplayName());
		getSmisProviderSet().add(provider);
		synchronizedFile(getSmisProviderSet());
		logger.info(provider);
		refreshTreeData(provider,status);
	}

	private static void refreshTreeData(SmisProvider provider, int status) {
		if (status==1) {//edit
			TreeItem[] items = tree.getSelection();
			if (items.length>0) {
				TreeItem item = tree.getSelection()[0];
				Object object = item.getData();
				if (object instanceof SmisProvider) {
					getSmisProviderSet().remove(object);
					synchronizedFile(getSmisProviderSet());
					item.dispose();
				}
			}
		}
		//add
		TreeItem treeItem = new TreeItem(rooTreeItem, SWT.NONE);
		treeItem.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_TREE_NODE));
		treeItem.setText(provider.getDisplayName().length()==0?provider.getHost():provider.getDisplayName());
		treeItem.setExpanded(true);
		treeItem.setData(provider);
		TreeItem subTreeItem = new TreeItem(treeItem, SWT.NONE);
		subTreeItem.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_NAMESPACE) + provider.getNamespace());
		subTreeItem.setExpanded(true);
		tree.update();
	}

	private static void synchronizedFile(Set<SmisProvider> smisProviderSet) {
		CommonUtil.writeObjectToFile(ResourceConstant.DATA_STORAGE_PATH_SMIS, smisProviderSet);
	}


}
