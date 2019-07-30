package com.shinwell.mda.util;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.shinwell.mda.service.SmisService;


/**
 * 
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月26日上午10:34:01
 * @Version: 1.0
 * @Desc:
 */
public class BeanContextUtil {
	static Logger logger = Logger.getLogger(BeanContextUtil.class.getSimpleName());
	private static BeanContextUtil instace = null;
	private static  ApplicationContext context = null;
	private SmisService smisService;
	private Map<String, String> dedicatedMap;
	
	private BeanContextUtil(){
		context = new FileSystemXmlApplicationContext(new String[] {"conf/applicationContext.xml"}); 
		smisService = (SmisService)context.getBean("smisService");
		dedicatedMap = (Map<String, String>) context.getBean("dedicatedMap");
		
		logger.info(dedicatedMap.toString());
	}
	
	public static BeanContextUtil getInstance(){
		if(context == null||instace==null){
			instace = new BeanContextUtil();
		}
		return instace;
	}
	
	public SmisService getSmisService() {
		return smisService;
	}

	public Object getBean(String beanName){
		return context.getBean(beanName);
		
	}
	
	public Map<String, String> getDedicatedMap() {
		return dedicatedMap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BeanContextUtil.getInstance().getSmisService();
	}


}
