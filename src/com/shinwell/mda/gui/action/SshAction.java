/**
 * 
 */
package com.shinwell.mda.gui.action;

import org.eclipse.jface.action.Action;


/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月19日下午3:22:26
 * @Version: 1.0
 * @Desc:
 */
public class SshAction extends Action {

	public SshAction() {
		setText("SSH");
	}

	@Override
	public void run() {
		System.out.println(getText());
	}

}
