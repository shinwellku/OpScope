/**
 * 
 */
package com.shinwell.mda.gui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月19日下午2:37:16
 * @Version: 1.0
 * @Desc:
 */
public class SmisAction extends Action {

	public SmisAction() {
		super();
		setText("SMI-S操作");
		
	}


	@Override
	public void run() {
		System.out.println(getText());
	}
}
