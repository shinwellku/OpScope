/**
 * 
 */
package com.shinwell.mda.gui.composite;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.shinwell.mda.gui.MdaAppWindow;

import swing2swt.layout.BorderLayout;


/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月25日上午10:54:50
 * @Version: 1.0
 * @Desc:
 */
public class WmiComposite extends Composite {
	static Logger logger = Logger.getLogger(WmiComposite.class.getSimpleName());
	public WmiComposite(Composite parent, int style) {

		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new BorderLayout(0, 0));
		SashForm sashForm = new SashForm(composite, SWT.BORDER);
		sashForm.setLayoutData(BorderLayout.CENTER);
		sashForm.setSashWidth(1);
		
		
		Tree tree = new Tree(sashForm, SWT.NONE);
		TreeItem treeItem = new TreeItem(tree, SWT.NONE);
		treeItem.setText("WMI-ROOT");
		
		CTabFolder tabFolder = new CTabFolder(sashForm, SWT.NONE);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("WMI");
		
		sashForm.setWeights(new int[] { 15, 85 });
	
		
	
		
	}

}
