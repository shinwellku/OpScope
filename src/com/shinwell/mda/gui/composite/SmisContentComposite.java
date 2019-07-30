package com.shinwell.mda.gui.composite;

import javax.cim.CIMInstance;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;

import swing2swt.layout.BorderLayout;

import com.shinwell.mda.domain.SmisProvider;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Menu;

/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2017年8月8日下午4:48:13
 * @Version: 1.0
 * @Desc:
 */
public class SmisContentComposite extends Composite {
	TreeItem selectedItem;
	CTabFolder tabFolder; 
	CIMInstance systemComputer;
	SmisProvider smisProvider;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SmisContentComposite(Composite parent, int style,TreeItem selectedItem, CTabFolder tabFolder, CIMInstance systemComputer, SmisProvider smisProvider) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		this.selectedItem = selectedItem;
		this.tabFolder = tabFolder;
		this.systemComputer = systemComputer;
		this.smisProvider = smisProvider;
		this.createContent();
	}

	
	private void createContent() {
		//设计模式打开，运行模式屏蔽
		tabFolder = new CTabFolder(this, SWT.CLOSE | SWT.FLAT|SWT.BORDER);
		tabFolder.setSimple(false);//平滑模式
		
		CTabItem cTabItem = new CTabItem(tabFolder, SWT.NONE);
		cTabItem.setText(selectedItem.getText());
		cTabItem.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
//				if (MessageDialog.openConfirm(getShell(), "确认", "确认要关闭吗?")) {
//					if (null!=selectedItem) {
//						System.out.println("cTabItem "+selectedItem.getText() +" will is dispose!");
//					}
//				}
			}
		});
		
		Composite tabItemComposite = new Composite(tabFolder, SWT.NONE);
		cTabItem.setControl(tabItemComposite);
		tabItemComposite.setLayout(new BorderLayout(0, 0));
		
		ToolBar smisOperToolBar = new ToolBar(tabItemComposite, SWT.FLAT | SWT.RIGHT | SWT.SHADOW_OUT);
		smisOperToolBar.setLayoutData(BorderLayout.NORTH);
		
		ToolItem poolItem = new ToolItem(smisOperToolBar, SWT.NONE);
		poolItem.setText("抽取池");
		poolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(getShell(), "提示", selectedItem.getText()+"\n"+smisProvider.toString());
			}
		});
		ToolItem diskItem = new ToolItem(smisOperToolBar, SWT.NONE);
		diskItem.setText("抽取磁盘");
		
		ToolItem volumeItem = new ToolItem(smisOperToolBar, SWT.NONE);
		volumeItem.setText("抽取卷");
		
		ToolItem portItem = new ToolItem(smisOperToolBar, SWT.NONE);
		portItem.setText("抽取端口");
		
		SashForm contentSashForm = new SashForm(tabItemComposite, SWT.SMOOTH);
		contentSashForm.setLayoutData(BorderLayout.CENTER);
		
		Table table = new Table(contentSashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn indexColumn = new TableColumn(table, SWT.NONE);
		indexColumn.setWidth(200);
		indexColumn.setText("索引");
		
		TableColumn cimInstanceColumn = new TableColumn(table, SWT.NONE);
		cimInstanceColumn.setWidth(200);
		cimInstanceColumn.setText("实例");
		
		Menu menu = new Menu(table);
		table.setMenu(menu);
		
		
		SashForm kpiSashForm = new SashForm(contentSashForm, SWT.VERTICAL);
		
		Table configTable = new Table(kpiSashForm, SWT.BORDER | SWT.FULL_SELECTION);
		configTable.setHeaderVisible(true);
		configTable.setLinesVisible(true);
		
		TableColumn configKeyColumn = new TableColumn(configTable, SWT.NONE);
		configKeyColumn.setWidth(200);
		configKeyColumn.setText("Config Key");
		
		TableColumn configValueColumn = new TableColumn(configTable, SWT.NONE);
		configValueColumn.setWidth(200);
		configValueColumn.setText("Value");
		
		Table perfTable = new Table(kpiSashForm, SWT.BORDER | SWT.FULL_SELECTION);
		perfTable.setHeaderVisible(true);
		perfTable.setLinesVisible(true);
		
		TableColumn perfKeyColumn = new TableColumn(perfTable, SWT.NONE);
		perfKeyColumn.setWidth(200);
		perfKeyColumn.setText("Perf Key");
		
		TableColumn perfValueColumn = new TableColumn(perfTable, SWT.NONE);
		perfValueColumn.setWidth(200);
		perfValueColumn.setText("Value");
		kpiSashForm.setWeights(new int[] {1, 1});
		contentSashForm.setWeights(new int[] {1, 1});
		tabFolder.setSelection(cTabItem);
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
