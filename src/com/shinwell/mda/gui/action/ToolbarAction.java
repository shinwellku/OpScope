package com.shinwell.mda.gui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.shinwell.mda.gui.MdaAppWindow;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;


/**
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2017年8月12日下午10:41:44
 * @Version: 1.0
 * @Desc:
 */
public class ToolbarAction extends Action {

	public ToolbarAction() {
		super();
		setText(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_MENU_ITEM_TOOLBAR));
		setChecked(true);
	}

	@Override
	public void run() {
		if (this.isChecked()) {
			this.setChecked(false);
			MdaAppWindow.getAppWindow().getToolBarManager().getControl().setVisible(this.isChecked());
		}else {
			this.setChecked(true);
			MdaAppWindow.getAppWindow().getToolBarManager().getControl().setVisible(this.isChecked());
		}
		System.out.println("===============");
	}
	

	
}
