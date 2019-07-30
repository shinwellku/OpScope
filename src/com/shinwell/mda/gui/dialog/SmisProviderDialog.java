package com.shinwell.mda.gui.dialog;


import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.shinwell.mda.domain.SmisProvider;
import com.shinwell.mda.gui.composite.SmisComposite;
import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;

public class SmisProviderDialog extends TitleAreaDialog {
	static Logger logger = Logger.getLogger(SmisProviderDialog.class.getSimpleName());
	private Combo namespaceCombo, protocolCombo, portCombo;
	private Text userText,passworText,displayNameText,hostText;
	Button saveCheckButton;
	SmisProvider provider;
	int status = 0;
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param i 
	 * @param object 
	 */
	public SmisProviderDialog(Shell parentShell, SmisProvider provider, int status) {
		super(parentShell);
		setHelpAvailable(false);
		this.provider = provider;
		this.status = status;
	}

	
	@Override
	protected void okPressed() {
		SmisProvider provider = new SmisProvider();
		provider.setUserName(userText.getText().trim());
		provider.setPassword(passworText.getText().trim());
		if (hostText.getText().trim().length()==0) {
			setErrorMessage("host is not null!");
			return;
		}
		provider.setHost(hostText.getText().trim());
		provider.setNamespace(namespaceCombo.getText().trim());
		if (portCombo.getText().trim().length()==0) {
			setErrorMessage("port is not null!");
			return;
		}
		provider.setPort(Integer.valueOf(portCombo.getText().trim()));
		
		if (protocolCombo.getText().trim().length()==0) {
			setErrorMessage("protocol is not null!");
			return;
		}
		
		provider.setProtocol(protocolCombo.getText().trim());
		if (saveCheckButton.getSelection()) {
			SmisComposite.getSmisProviderSet().add(provider);
		}
		provider.setDisplayName(displayNameText.getText().trim());
		SmisComposite.scanSmisArray(provider,this.status);
		
		getShell().setVisible(false);
		super.okPressed();
	}


	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		UIBuilderFactory.setCenter(newShell);
		newShell.setText("SMI-S Provider");
	}


	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_TITLE));
		setMessage(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_TIP_MSG));
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.BORDER);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label displayNameLabel = new Label(container, SWT.NONE);
		displayNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		displayNameLabel.setAlignment(SWT.RIGHT);
		displayNameLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_DISPLAY_NAME_LABEL));
		
		displayNameText = new Text(container, SWT.BORDER);
		displayNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label protocolLabel = new Label(container, SWT.NONE);
		protocolLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		protocolLabel.setAlignment(SWT.RIGHT);
		protocolLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_PROTOCOL_LABEL));
		
		protocolCombo = new Combo(container, SWT.NONE);
		protocolCombo.setItems(new String[] {"HTTP", "HTTPS"});
		protocolCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label hostLabel = new Label(container, SWT.NONE);
		hostLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hostLabel.setAlignment(SWT.RIGHT);
		hostLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_HOST_LABEL));
		
		hostText = new Text(container, SWT.BORDER);
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label portLabel = new Label(container, SWT.NONE);
		portLabel.setAlignment(SWT.RIGHT);
		portLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		portLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_PORT_LABEL));
		
		portCombo = new Combo(container, SWT.NONE);
		portCombo.setItems(new String[] {"5988", "5989", "15988", "15989"});
		portCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label namespaceLabel = new Label(container, SWT.NONE);
		namespaceLabel.setAlignment(SWT.RIGHT);
		namespaceLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		namespaceLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_NAMESPACE_LABEL));
		
		String[] items = new String[] {"root/emc", "root/ibm", "root/huawei", "root/cimv2", "root/brocade", "root/LsiArray13", "root/ontap", "root/smis/current", "root/hitachi/smis", "root/eternus", "root/brocade1", "root/interop", "interop"};
		namespaceCombo = new Combo(container, SWT.BORDER);
		namespaceCombo.setItems(items);
		namespaceCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		new AutoCompleteField(namespaceCombo, new ComboContentAdapter(),items);
		
		Label userLabel = new Label(container, SWT.NONE);
		userLabel.setAlignment(SWT.RIGHT);
		userLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		userLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_USER_LABEL));
		
		userText = new Text(container, SWT.BORDER);
		userText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label passwordLabel = new Label(container, SWT.NONE);
		passwordLabel.setAlignment(SWT.RIGHT);
		passwordLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		passwordLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_PASSWORD_LABEL));
		
		passworText = new Text(container, SWT.BORDER | SWT.PASSWORD);
		passworText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		saveCheckButton = new Button(container, SWT.CHECK);
		saveCheckButton.setSelection(true);
		saveCheckButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_SAVE_DEFAULT_LABEL));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		initData(provider);
		return area;
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
		return new Point(727, 426);
	}
	
	public void initData(SmisProvider provider){
		if (null!=provider) {
			displayNameText.setText(provider.getDisplayName()==null?"":provider.getDisplayName());
			hostText.setText(provider.getHost()==null?"":provider.getHost());
			passworText.setText(provider.getPassword()==null?"":provider.getPassword());
			userText.setText(provider.getUserName()==null?"":provider.getUserName());
			protocolCombo.setText(provider.getProtocol()==null?"":provider.getProtocol());
			portCombo.setText(provider.getPort()==0?"":String.valueOf(provider.getPort()));
			namespaceCombo.setText(provider.getNamespace()==null?"":provider.getNamespace());
		}
	}
}
