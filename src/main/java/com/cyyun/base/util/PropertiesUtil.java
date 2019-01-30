package com.cyyun.base.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * properties文件的读取
 * @author yaodw
 *
 */
public final class PropertiesUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	
	private static final Properties properties = new Properties();
	
	private Properties instance = null;
	
	private static final String default_path = "/config/fmanager.properties";
	
	static{
		loadResource();
	}
	
	public PropertiesUtil(){
		
	}
	
	public PropertiesUtil(String path){
		loadResource(path);
	}
	/**
	 * 默认路径
	 */
	private static void loadResource(){
		try {
			properties.load(PropertiesUtil.class.getResourceAsStream(default_path));
		} catch (IOException e) {
			logger.error("加载properties文件出错",e);
		}
	}
	/**
	 * 适用与指定路径
	 * @param path
	 */
	private void loadResource(String path){
		try {
			instance = new Properties();
			instance.load(PropertiesUtil.class.getResourceAsStream(path));
		} catch (IOException e) {
			logger.error("加载properties文件出错",e);
		}
	}
	
	public static String getValue(String key){
		return properties.getProperty(key);
	}
	
	public String getVal(String key){
		return instance.getProperty(key);
	}
	
	public static void main(String[] args) {
		System.out.println(PropertiesUtil.getValue("unprocessed"));
		PropertiesUtil propertiesUtil = new PropertiesUtil("/config/fmanager.properties");
		System.out.println(propertiesUtil.getVal("unprocessed"));
	}
}
