package com.shinwell.mda.gui.dialog;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import com.shinwell.mda.domain.SnmpProvider;
import com.shinwell.mda.gui.composite.SnmpComposite;
import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;
/**
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2018年4月26日上午10:13:29
 * @Version: 1.0
 * @Desc:
 */
public class SnmpProviderDialog extends TitleAreaDialog{

	static Logger logger = Logger.getLogger(SnmpProviderDialog.class.getSimpleName());
	private Text eoidText;
	Combo portCombo;
	private Text readCommunityText,writeCommunityText,displayNameText,hostText;
	Button saveCheckButton;
	SnmpProvider provider;
	int status = 0;
	private Label displayNameLabel;
	private Label eoidLabel;
	private Label hostLabel;
	private Label portLabel;
	private Label readCommLabel;
	private Label writeCommLabel;
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param i 
	 * @param object 
	 */
	public SnmpProviderDialog(Shell parentShell, SnmpProvider provider, int status) {
		super(parentShell);
		setHelpAvailable(false);
		//setHelpAvailable(true);
		this.provider = provider;
		this.status = status;
	}

	
	@Override
	protected void okPressed() {
		SnmpProvider provider = new SnmpProvider();
		
		if (hostText.getText().trim().length()==0) {
			setErrorMessage("\""+hostLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
			return;
		}
		provider.setHost(hostText.getText().trim());
		if (portCombo.getText().trim().length()==0) {
			setErrorMessage("\""+portLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
			return;
		}
		provider.setPort(Integer.valueOf(portCombo.getText().trim()));
		
		if (eoidText.getText().trim().length()==0) {
			setErrorMessage("\""+eoidLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
			return;
		}
		
		provider.setEnterpriseOID(eoidText.getText().trim());
		
		
		if (readCommunityText.getText().trim().length()==0) {
			setErrorMessage("\""+readCommLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
			return;
		}
		provider.setReadCommunity(readCommunityText.getText().trim());
		
		if (writeCommunityText.getText().trim().length()==0) {
			setErrorMessage("\""+writeCommLabel.getText()+"\""+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MSG_NO_EMPTY));
			return;
		}
		provider.setWriteCommunity(writeCommunityText.getText().trim());
		
		provider.setDisplayName(displayNameText.getText().trim());
		if (saveCheckButton.getSelection()) {
			SnmpComposite.getSnmpProviderSet().add(provider);
		}
		SnmpComposite.scanSnmpArray(provider,this.status);
		
		getShell().setVisible(false);
		super.okPressed();
	}


	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		UIBuilderFactory.setCenter(newShell);
		newShell.setText("SNMP Provider");
	}


	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_DIALOG_TITLE));
		setMessage(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_DIALOG_TIP_MSG));
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.BORDER);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		displayNameLabel = new Label(container, SWT.NONE);
		displayNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		displayNameLabel.setAlignment(SWT.RIGHT);
		displayNameLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SMIS_DIALOG_DISPLAY_NAME_LABEL));
		
		displayNameText = new Text(container, SWT.BORDER);
		displayNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		eoidLabel = new Label(container, SWT.NONE);
		eoidLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		eoidLabel.setAlignment(SWT.RIGHT);
		eoidLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_DIALOG_EOID_LABEL));
		
		eoidText = new Text(container, SWT.BORDER);
		eoidText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		hostLabel = new Label(container, SWT.NONE);
		hostLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hostLabel.setAlignment(SWT.RIGHT);
		hostLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_DIALOG_HOST_LABEL));
		
		hostText = new Text(container, SWT.BORDER);
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		portLabel = new Label(container, SWT.NONE);
		portLabel.setAlignment(SWT.RIGHT);
		portLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		portLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_DIALOG_PORT_LABEL));
		
		portCombo = new Combo(container, SWT.NONE);
		portCombo.setItems(new String[] {"161"});
		portCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		readCommLabel = new Label(container, SWT.NONE);
		readCommLabel.setAlignment(SWT.RIGHT);
		readCommLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		readCommLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_DIALOG_READ_COMMUNITY_LABEL));
		
		readCommunityText = new Text(container, SWT.BORDER | SWT.PASSWORD);
		readCommunityText.setToolTipText(readCommunityText.getText());
		readCommunityText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		writeCommLabel = new Label(container, SWT.NONE);
		writeCommLabel.setAlignment(SWT.RIGHT);
		writeCommLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		writeCommLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_SNMP_DIALOG_WRITE_COMMUNITY_LABEL));
		
		writeCommunityText = new Text(container, SWT.BORDER | SWT.PASSWORD);
		writeCommunityText.setToolTipText(writeCommunityText.getText());
		writeCommunityText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
		return new Point(727, 391);
	}
	
	public void initData(SnmpProvider provider){
		if (null!=provider) {
			displayNameText.setText(provider.getDisplayName()==null?"":provider.getDisplayName());
			hostText.setText(provider.getHost()==null?"":provider.getHost());
			writeCommunityText.setText(provider.getWriteCommunity()==null?"":provider.getWriteCommunity());
			readCommunityText.setText(provider.getReadCommunity()==null?"":provider.getReadCommunity());
			eoidText.setText(provider.getEnterpriseOID()==null?"":provider.getEnterpriseOID());
			portCombo.setText(provider.getPort()==0?"":String.valueOf(provider.getPort()));
		}
	}


}
