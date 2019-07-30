package com.shinwell.mda.gui.dialog;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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

import swing2swt.layout.BorderLayout;

import com.shinwell.mda.domain.SnmpTrap;
import com.shinwell.mda.util.SWTResourceManager;
import com.shinwell.mda.util.snmp.SnmpMTTrapReceiver;

/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2018年4月29日上午1:56:22
 * @Version: 1.0
 * @Desc:
 */
public class TrapViewerDialog extends TitleAreaDialog {
	private Table trapTable;
	private Table detailTrapTable;
	private Text trapText;
	private Text commText;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TrapViewerDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MAX);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("实时监听指定的端口，接收Trap，列表展示，点击表格某一条，右边表格展示明细信息，下边文本框展示原始报文");
		setTitle("Trap接收");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new BorderLayout(5, 5));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group grpTrap = new Group(container, SWT.NONE);
		grpTrap.setText("Trap接收配置");
		grpTrap.setLayoutData(BorderLayout.NORTH);
		grpTrap.setLayout(new GridLayout(7, false));
		
		Label portLabel = new Label(grpTrap, SWT.NONE);
		portLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		portLabel.setText("端口:");
		
		final Combo portCombo = new Combo(grpTrap, SWT.NONE);
		portCombo.setItems(new String[] {"162"});
		portCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		portCombo.select(0);
		
		Label communityLabel = new Label(grpTrap, SWT.NONE);
		communityLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		communityLabel.setText("Community:");
		
		commText = new Text(grpTrap, SWT.BORDER);
		commText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		final Button startButton = new Button(grpTrap, SWT.NONE);
		startButton.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 9, SWT.BOLD));
		startButton.setText(" 开  始 ");
		
		final Button stopButton = new Button(grpTrap, SWT.NONE);
		stopButton.setEnabled(false);
		stopButton.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 9, SWT.BOLD));
		stopButton.setText(" 停  止 ");
		
		Button delButton = new Button(grpTrap, SWT.NONE);
		delButton.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 9, SWT.BOLD));
		delButton.setText(" 删  除 ");
		
		SashForm sashForm = new SashForm(container, SWT.NONE);
		sashForm.setLayoutData(BorderLayout.CENTER);
		
		SashForm leftSashForm = new SashForm(sashForm, SWT.VERTICAL);
		
		TableViewer trapTableViewer = new TableViewer(leftSashForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		trapTable = trapTableViewer.getTable();
		trapTable.setToolTipText("双击查看明细");
		trapTable.setLinesVisible(true);
		trapTable.setHeaderVisible(true);
		
		TableColumn classColumn = new TableColumn(trapTable, SWT.LEFT);
		classColumn.setWidth(100);
		classColumn.setText("Class");
		
		TableColumn typeColumn = new TableColumn(trapTable, SWT.LEFT);
		typeColumn.setText("类型");
		typeColumn.setWidth(100);
		
		TableColumn sourceColumn = new TableColumn(trapTable, SWT.LEFT);
		sourceColumn.setWidth(122);
		sourceColumn.setText("源");
		
		TableColumn dateColumn = new TableColumn(trapTable, SWT.LEFT);
		dateColumn.setWidth(129);
		dateColumn.setText("日期");
		
		TableColumn msgColumn = new TableColumn(trapTable, SWT.LEFT);
		msgColumn.setWidth(300);
		msgColumn.setText("消息");
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
		
		trapText = new Text(leftSashForm, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		trapText.setToolTipText("原始报文");
		leftSashForm.setWeights(new int[] {80, 20});
		
		TableViewer trapDetailTableViewer = new TableViewer(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		detailTrapTable = trapDetailTableViewer.getTable();
		detailTrapTable.setLinesVisible(true);
		detailTrapTable.setHeaderVisible(true);
		
		TableColumn propertyColumn = new TableColumn(detailTrapTable, SWT.LEFT);
		propertyColumn.setWidth(300);
		propertyColumn.setText("OID");
		
		TableColumn valueColumn = new TableColumn(detailTrapTable, SWT.LEFT);
		valueColumn.setWidth(100);
		valueColumn.setText("值");
		sashForm.setWeights(new int[] {70, 30});
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (portCombo.getText().trim().length()==0) {
					MessageDialog.openError(getShell(), "错误", "端口不能为空！");
					return;
				}
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				try {
					SnmpMTTrapReceiver receiver = SnmpMTTrapReceiver.getInstance("0.0.0.0", Integer.valueOf(portCombo.getText().trim()),null,trapTable);
					receiver.run();
				}catch (Exception e1) {
					MessageDialog.openError(getShell(), "错误", e1.getMessage());
					e1.printStackTrace();
				}
				
			}
		});
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				try {
					SnmpMTTrapReceiver receiver = SnmpMTTrapReceiver.getInstance("0.0.0.0", Integer.valueOf(portCombo.getText().trim()),null,trapTable);
					receiver.stop();
				}catch (Exception e1) {
					MessageDialog.openError(getShell(), "错误", e1.getMessage());
					e1.printStackTrace();
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
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		//createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(918, 623);
	}
}
