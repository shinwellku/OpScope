/**
 * 
 */
package com.shinwell.mda.gui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import com.shinwell.mda.gui.dialog.AboutDialog;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;


/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月19日下午3:30:43
 * @Version: 1.0
 * @Desc:
 */
public class AboutAction extends Action {
	Shell shell;
	public AboutAction(Shell shell) {
		super();
		setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_ABOUT));
		this.shell = shell;
	}

	@Override
	public void run() {
		System.out.println(getText());
		AboutDialog aboutDialog =  new AboutDialog(shell);
		aboutDialog.open();
	}

	
}
