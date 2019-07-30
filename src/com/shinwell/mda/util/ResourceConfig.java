/**
 * 
 */
package com.shinwell.mda.util;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.shinwell.mda.gui.MdaAppWindow;


/**
 * 
 * project_name: clientAgent
 * Author: ku_xiangwei
 * Date: 2012-4-9
 * Version:
 * Desc:Resource Config
 */
public class ResourceConfig {
	static Logger logger = Logger.getLogger(ResourceConfig.class.getSimpleName());
    private PropertiesConfiguration propertiesConfiguration = null;

    private String path = "";

    public ResourceConfig(String configPath) {
        try {
        	this.path = configPath;
            propertiesConfiguration = new PropertiesConfiguration(path);
            propertiesConfiguration.setAutoSave(false);
        } catch (ConfigurationException e) {
        	logger.error("Can't find " + path + "!");
        }
    }

    public void setValue(String key, Object o) {
        propertiesConfiguration.setProperty(key, o);
        logger.info("Save Config " + key + "=" + o);
    }
    


    public int getInt(String key, int defaultValue) {
        try {
            defaultValue = propertiesConfiguration.getInt(key, defaultValue);
        } catch (ConversionException e) {
        	logger.info(path + " of " + key + " value is not a valid Numbers");
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            defaultValue = propertiesConfiguration.getBoolean(key, defaultValue);
        } catch (ConversionException e) {
        	logger.info(path + " of " + key + " value is not true or false");
        }
        return defaultValue;
    }


    public String getString(String key, String defaultValue) {
        return propertiesConfiguration.getString(key, defaultValue);
    }
    
    
    public long getLong(String key, long defaultValue) {
        return propertiesConfiguration.getLong(key, defaultValue);
    }

    public List getList(String key) {
        return propertiesConfiguration.getList(key);
    }
    public void save() {
        try {
            propertiesConfiguration.save();
            logger.info("Save configuration file");
        } catch (ConfigurationException e) {
        	logger.error("Save configuration file failed:"+e.getMessage());
        }
    }




}
