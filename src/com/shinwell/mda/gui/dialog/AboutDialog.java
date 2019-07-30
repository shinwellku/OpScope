package com.shinwell.mda.gui.dialog;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import swing2swt.layout.BorderLayout;

import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;
import com.shinwell.mda.util.SWTResourceManager;

/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2017年8月7日下午1:07:56
 * @Version: 1.0
 * @Desc:
 */
public class AboutDialog extends TitleAreaDialog {
	static Logger logger = Logger.getLogger(AboutDialog.class.getSimpleName());

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AboutDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
		
	}
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		UIBuilderFactory.setCenter(newShell);
		newShell.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_ABOUT_MSG));
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_ABOUT_MSG));
		setMessage(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_COPY_RIGHT));
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new BorderLayout(0, 0));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite_logo = new Composite(container, SWT.NONE);
		composite_logo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_logo.setLayoutData(BorderLayout.WEST);
		GridLayout gl_composite_logo = new GridLayout(1, false);
		gl_composite_logo.verticalSpacing = 0;
		gl_composite_logo.marginWidth = 0;
		gl_composite_logo.marginHeight = 0;
		gl_composite_logo.horizontalSpacing = 0;
		composite_logo.setLayout(gl_composite_logo);
		
		
		
		CLabel logoLabel = new CLabel(composite_logo, SWT.NONE);
		logoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		logoLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		logoLabel.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_ABOUT_DIALOG));
		//logoLabel.setImage(UIBuilderFactory.createImage(ResourceConstant.IMAGE_ENTERPRISE_LOGO));
		
		Composite composite_content = new Composite(container, SWT.NONE);
		composite_content.setLayoutData(BorderLayout.CENTER);
		composite_content.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_content.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite_content, SWT.SEPARATOR | SWT.VERTICAL);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 9));
		Properties props=System.getProperties();     
		
		CLabel appNameLabel = new CLabel(composite_content, SWT.NONE);
		appNameLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		appNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		appNameLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_APP_NAME));
		
		CLabel versionLabel = new CLabel(composite_content, SWT.NONE);
		versionLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		versionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		versionLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_VERSION_LABEL)+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_APP_VERSION));
		
		CLabel osNameLabel = new CLabel(composite_content, SWT.NONE);
		osNameLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		osNameLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CLIENT_OS_NAME)+props.getProperty("os.name"));
		
		CLabel osVersionLabel = new CLabel(composite_content, SWT.NONE);
		osVersionLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		osVersionLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CLIENT_OS_VERSION)+props.getProperty("os.version"));
		
		CLabel osArchLabel = new CLabel(composite_content, SWT.NONE);
		osArchLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		osArchLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CLIENT_OS_ARCH)+props.getProperty("os.arch"));
		
		CLabel javaRuntimeLabel = new CLabel(composite_content, SWT.NONE);
		javaRuntimeLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		javaRuntimeLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CLIENT_JAVA_RUNTIME_VERSION)+props.getProperty("java.version"));
		
		CLabel emailLabel = new CLabel(composite_content, SWT.NONE);
		emailLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		emailLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CLIENT_EMAIL_LABEL)+"kuxiangwei@163.com");
		
		CLabel qqLabel = new CLabel(composite_content, SWT.NONE);
		qqLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		qqLabel.setText("QQ:372510766");
		new Label(composite_content, SWT.NONE);
		
//		CLabel copyrightLabel = new CLabel(composite_content, SWT.NONE);
//		copyrightLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
//		copyrightLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_CLIENT_COPY_RIGHT_LABEL)+ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_COPYRIGHT));

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(702, 472);
	}
}
