package com.ylp.common.es.constant;

import com.ylp.common.es.util.PropertiesUtils;

/**
 * 服务器信息常量
 * @author chenyw
 * Dec 13, 2016 2:05:19 PM
 *
 */
public class ServerConstant {

	//es第一个节点ip
	public static final String ES_HOST1 = PropertiesUtils.getProperty("config.properties", "es.host1");
	//es第二个节点ip
	public static final String ES_HOST2 = PropertiesUtils.getProperty("config.properties", "es.host2");
	//es第三个节点ip
	public static final String ES_HOST3 = PropertiesUtils.getProperty("config.properties", "es.host3");
	//es第一个节点端口
	public static final int ES_PORT1 = Integer.parseInt( PropertiesUtils.getProperty("config.properties", "es.port1"));
	//es第二个节点端口
	public static final int ES_PORT2 =  Integer.parseInt( PropertiesUtils.getProperty("config.properties", "es.port2"));
	//es第三个节点端口
	public static final int ES_PORT3 = Integer.parseInt( PropertiesUtils.getProperty("config.properties", "es.port3"));
	
	//集群节点名称
	public static final String ES_CLUSTER_NAME = PropertiesUtils.getProperty("config.properties", "es.cluster_name");
	
}
