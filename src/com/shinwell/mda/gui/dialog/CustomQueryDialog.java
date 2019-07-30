package com.shinwell.mda.gui.dialog;

import javax.cim.CIMInstance;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.shinwell.mda.gui.composite.SmisComposite;
import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;

/**
 * 
 * @author Shinwell Ku
 * @Date 2018年10月9日
 * @Desc
 */
public class CustomQueryDialog extends TitleAreaDialog {

	private Combo associatorClassCombo;
	private Combo resultClassCombo;
	String[] items;
	CIMInstance selectInstance;
	private Text sourceClassText;
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param selectInstance 
	 * @param instance 
	 * @param items 
	 */
	public CustomQueryDialog(Shell parentShell, CIMInstance selectInstance, String[] items) {
		super(parentShell);
		setHelpAvailable(false);
		this.selectInstance = selectInstance;
		this.items = items;
	}
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		UIBuilderFactory.setCenter(newShell);
		newShell.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_CUSTOM_QUERY));
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_CUSTOM_QUERY));
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.heightHint = 210;
		container.setLayoutData(gd_container);
		
		CLabel sourceClassLabel = new CLabel(container, SWT.NONE);
		sourceClassLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_DEFINE_CLASS));
		
		sourceClassText = new Text(container, SWT.BORDER);
		sourceClassText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		sourceClassText.setText(selectInstance.getClassName());
		sourceClassText.setEditable(false);
		
		CLabel associatorClassLabel = new CLabel(container, SWT.NONE);
		associatorClassLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_ASSOCIATOR_CLASS));
		
		associatorClassCombo = new Combo(container, SWT.DROP_DOWN);
		associatorClassCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		CLabel resultClassLabel = new CLabel(container, SWT.NONE);
		resultClassLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_RESULT_CLASS));
		
		resultClassCombo = new Combo(container, SWT.DROP_DOWN);
		resultClassCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		associatorClassCombo.setItems(items);
		resultClassCombo.setItems(items);
		new AutoCompleteField(associatorClassCombo, new ComboContentAdapter(),items);
		new AutoCompleteField(resultClassCombo, new ComboContentAdapter(),items);
		return area;
	}
	
	public void setAssociatorClassCombo(String[] items) {
		associatorClassCombo.setItems(items);
	}
	public void setResultClassCombo(String[] items) {
		resultClassCombo.setItems(items);
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(646, 355);
	}

	protected void okPressed() {
		//instances = cimAPI.associatorInstances(selectInstance.getObjectPath(), associatorClassCombo.getText().trim(), resultClassCombo.getText().trim());
		SmisComposite.associatorClass = associatorClassCombo.getText().trim();
		SmisComposite.resultClass = resultClassCombo.getText().trim();
		super.okPressed();
	}

}
