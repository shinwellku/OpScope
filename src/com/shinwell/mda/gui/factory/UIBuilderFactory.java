/**
 * 
 */
package com.shinwell.mda.gui.factory;

import java.awt.Toolkit;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.shinwell.mda.gui.MdaAppWindow;

/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月25日下午4:34:20
 * @Version: 1.0
 * @Desc:
 */
public final class UIBuilderFactory {
	
	public static final String DIALOG_TYPE_INFO="INFO";
	public static final String DIALOG_TYPE_ERROR="ERROR";
	public static final String DIALOG_TYPE_QUEST="QUEST";
	/**
	 * 居中
	 * 
	 * @param shell
	 */
	public static void setCenter(Shell shell) {
		Rectangle displayBounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle shellBounds = shell.getBounds();
		int x = displayBounds.x + (displayBounds.width - shellBounds.width)/2;
		int y = displayBounds.y + (displayBounds.height - shellBounds.height)/2;
		shell.setLocation(x, y);
	}
	public static void setCenter2(Shell shell) {
		Rectangle displayBounds = shell.getDisplay().getClientArea();
		Point point = shell.getSize();
		int x =  (displayBounds.width - point.x)/2;
		int y =  (displayBounds.height - point.y)/2;
		shell.setLocation(x, y);
	}
	public static void setCenter1(Shell shell) {
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int shellH = shell.getBounds().height;
		int shellW = shell.getBounds().width;
		if (shellH > screenH)
			shellH = screenH;
		if (shellW > screenW)
			shellW = screenW;
		shell.setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));
	}
	public static int getScreenHeight(){
		return Display.getDefault().getPrimaryMonitor().getBounds().height;
	}
	public static int getScreenWidth(){
		return Display.getDefault().getPrimaryMonitor().getBounds().width;
	}

	/**
	 * 全屏
	 * 
	 * @param shell
	 */
	public static void setMax(Shell shell) {
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		shell.setSize(screenW, screenH);
		shell.setLocation(0, 0);
	}

	public static ImageDescriptor createImageDescriptor(String key) {
		//return ImageDescriptor.createFromImage(new Image(Display.getCurrent(), key));
		return ImageDescriptor.createFromFile(MdaAppWindow.class, key);
	}

	public static Image createImage(String key) {
		//return new Image(Display.getCurrent(), key);
		return createImageDescriptor(key).createImage();
	}

	public static MenuManager ceateMenu(String menuName) {
		MenuManager menu = new MenuManager(menuName);
		return menu;
	}

	public static MenuManager createMenu(MenuManager menu) {
		return menu;
	}

	public static Action createAction(Action action) {
		return action;
	}
	
	public static void createSimpleMessageDialog(Shell shell,String title,String content,String type){
		
	}

	/**
	 * 
	 * @param composite
	 *            父容器
	 * @param name
	 *            名称
	 * @param tipText
	 *            提示信息
	 * @param imageName
	 *            图片名称
	 * @param style
	 *            样式
	 * @return
	 */
	public static Button createButton(Composite composite, String name, String tipText, String imageName, int style) {
		Button button = new Button(composite, style != 0 ? style : SWT.NONE);
		if (null != name && name.length() > 0) {
			button.setText(name);
		}
		if (null != tipText && tipText.length() > 0) {
			button.setToolTipText(tipText);
		}
		if (null != imageName && imageName.length() > 0) {
			button.setImage(createImage(imageName));
		}
		return button;
	}


}
