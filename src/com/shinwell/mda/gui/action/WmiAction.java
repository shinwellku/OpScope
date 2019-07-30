/**
 * 
 */
package com.shinwell.mda.gui.action;

import org.eclipse.jface.action.Action;


/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月19日下午2:50:10
 * @Version: 1.0
 * @Desc:
 */
public class WmiAction extends Action {

	public WmiAction() {
		super();
		setText("WMI");
	}

	@Override
	public void run() {
		System.out.println(getText());
	}

	
}
