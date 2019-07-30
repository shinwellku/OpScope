package com.shinwell.mda.util;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2017年8月9日下午12:34:14
 * @Version: 1.0
 * @Desc:
 */
public final class ResourceBundleUtil {
	static Logger logger = Logger.getLogger(ResourceBundleUtil.class.getSimpleName());
	static final String BUNDLE_NAME = "com.shinwell.mda.gui.i18n.ApplicationResources";
	// static ResourceBundle resourceBundle =
	// ResourceBundle.getBundle(BUNDLE_NAME, locale_ZH);
	static ResourceBundle bundle;
	static Locale locale;
	// 使用Singleton使得ResourceManager在程序里只有一个实例
	private static ResourceBundleUtil _instance = new ResourceBundleUtil();

	public static ResourceBundleUtil getInstance() {
		return _instance;
	}

	// 更换语言显示
	public static void changeLang(String lang) {
		logger.info("Change Locale:"+lang);
		if (lang.equalsIgnoreCase("zh_CN")) {
			locale = Locale.CHINA;
		} else {
			locale = Locale.US;
		}
	}

	/**
	 * 取得设置后的语言版本，如果没有设置，则使用默认值Local.US
	 * 
	 * @return
	 */
	private static ResourceBundle getBundle() {
		if (locale == null)
			//locale = Locale.CHINA;
			locale = Locale.getDefault();
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		}
		return bundle;
	}

	public static String getValueByKey(String key) {
		String value = getBundle().getString(key);
		logger.debug("Key:"+key+" Value:"+value);
		return value;
	}

	public static boolean containsKey(String key) {
		return getBundle().containsKey(key);
	}
	public static void main(String[] args) {
		ResourceBundleUtil.changeLang("en");
		System.out.println(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_WELCOME));
		ResourceBundleUtil.changeLang("zh_CN");
		System.out.println(ResourceBundleUtil.getValueByKey(ResourceConstant.I18N_KEY_WELCOME));
	}

}
