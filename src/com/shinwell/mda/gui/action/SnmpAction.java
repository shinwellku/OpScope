/**
 * 
 */
package com.shinwell.mda.gui.action;

import org.eclipse.jface.action.Action;


/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月19日下午3:24:16
 * @Version: 1.0
 * @Desc:
 */
public class SnmpAction extends Action {

	public SnmpAction() {
		super();
		
	}

	@Override
	public void run() {
		System.out.println(getText());
	}

	
}
