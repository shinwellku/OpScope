package com.shinwell.mda.gui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class OidDialog extends Dialog {
	String oid;
	String value;
	private Text oidText;
	private Text valueText;
	int status;
	Table oidTable;
	TableItem tableItem;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param status 
	 * @param oidTable 
	 */
	public OidDialog(Shell parentShell,TableItem tableItem, int status, Table oidTable) {
		super(parentShell);
		this.oid = tableItem==null?"": tableItem.getText(0);
		this.value =  tableItem==null?"":tableItem.getText(1);
		this.status = status;
		this.tableItem = tableItem;
		this.oidTable = oidTable;
	}


	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(4, false));
		
		Label oidLabel = new Label(composite, SWT.NONE);
		oidLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		oidLabel.setSize(38, 24);
		oidLabel.setText("OID:");
		
		oidText = new Text(composite, SWT.BORDER);
		oidText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		oidText.setSize(301, 30);
		
		Label valueLabel = new Label(composite, SWT.NONE);
		valueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		valueLabel.setSize(22, 24);
		valueLabel.setText("值:");
		
		valueText = new Text(composite, SWT.BORDER);
		valueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		valueText.setSize(600, 30);
		
		if (status==1) {
			oidText.setText(this.oid);
			valueText.setText(this.value);
		}
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	
	@Override
	protected void okPressed() {
		if (oidText.getText().trim().length()>0) {
			if (status==1) {
				tableItem.setText(new String[]{oidText.getText().trim(),valueText.getText().trim()});
			}else {
				TableItem tableItem = new TableItem(oidTable, SWT.NONE);
				tableItem.setText(new String[]{oidText.getText().trim(),valueText.getText().trim()});
			}
		}
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(704, 189);
	}

}
