/**   
 * Copyright © 2003 - 2014 Lensoon. All Rights Reserved. 联迅公司 版权所有
 */
package com.ylp.common.es.util;

import java.io.IOException;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;


/**
 *  Properties配置文件帮助类
 * @author linhz   
 * @date 2014年10月16日 
 * @version V1.0
 */
public class PropertiesUtils {

	private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
			

	/**
	 * 根据文件名获得properties配置
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static Properties getProperties(String fileName) {

		Properties properties = new Properties();

		try {
			properties.load(PropertiesUtils.class.getClassLoader()
					.getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return properties;
	}

	
	/**
	 * 根据配置文件名和key获得value
	 *
	 * @param fileName
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String getProperty(String fileName, String key) {
		try {
			String value = getProperties(fileName).getProperty(key);
			return switchPropertyValue(value);
		} catch (Exception e) {
			logger.error("[PropertiesUtils.getProperty() 获取配置失败]", e);
		}
		return null;
	}

	/**
	 * 获取配置文件中全部的key
	 *
	 * @return
	 */
	public static List<String> getKeySet(String fileName,String keyName) {
		List<String> result = new ArrayList<String>();
		Set<Object> keySet = getProperties(fileName).keySet();
		if (CollectionUtils.isEmpty(keySet)) {
			return null;
		}
		Iterator<Object> iterator = keySet.iterator();
		String key = "";
		while (iterator.hasNext()) {
			key = (String) iterator.next();

			if (StringUtils.isEmpty(keyName)) {
				result.add(key);
			} else {
				if (key.contains(keyName)) {
					result.add(key);
				}
			}
		}
		return result;

	}

	public static String switchPropertyValue(String value) throws Exception {

		if (value == null) {
			return value;
		}

		return new String(value.getBytes("ISO8859-1"), "UTF-8").trim();
	}
}

