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

import swing2swt.layout.BorderLayout;

public class SshComposite extends Composite {

	static Logger logger = Logger.getLogger(SshComposite.class.getSimpleName());
	public SshComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new BorderLayout(0, 0));
		SashForm sashForm = new SashForm(composite, SWT.BORDER);
		sashForm.setLayoutData(BorderLayout.CENTER);
		sashForm.setSashWidth(1);
		
		
		Tree tree = new Tree(sashForm, SWT.NONE);
		TreeItem treeItem = new TreeItem(tree, SWT.NONE);
		treeItem.setText("SSH-ROOT");
		
		CTabFolder tabFolder = new CTabFolder(sashForm, SWT.NONE);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("SSH");
		
		sashForm.setWeights(new int[] { 15, 85 });
	}


}
