package com.ylp.common.es.util;

import java.util.ResourceBundle;

/**
 * 读取配置文件工具类
 * @author chenyw
 * @date 2016年3月30日
 */
public class ReadConfigUtil {
	
	/*
	 * 根据配置classpath文件路径下读取key=value信息
	 * */
	public static ResourceBundle getResource(String filename){
		ResourceBundle bundle = ResourceBundle.getBundle(filename);
		return bundle;
	}
	
	/**
	 * 
	 * @param filename property文件名，不需要后缀
	 * @param key 需要的key
	 * @return
	 */
	public static String getResource(String filename,String key){
		ResourceBundle bundle = ResourceBundle.getBundle(filename);
		return bundle.getString(key);
	}
	
}
