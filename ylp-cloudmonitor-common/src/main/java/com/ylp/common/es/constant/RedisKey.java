package com.ylp.common.es.constant;

import java.io.Serializable;

public class RedisKey {
	
	/**
	 * 日志列表 list结构
	 */
	public static final String LOG_LEVEL_LIST="cm:settings:levelList";

	/**
	 * 系统列表 list结构 
	 */
	public static final String SYSTEM_LIST="cm:settings:sysList";
	
	/**
	 * 接口列表 list结构
	 */
	public static final String API_LIST="cm:settings:apiList";
	
	/**
	 * 系统最短url映射系统中文名称 hash结构
	 */
	public static final String SYS_NAME_HASH_MAPPING = "cm:url:map";
	
	
	
	
	/**
	 * redis统计key  hash结构
	 * %s 表示时间参数
	 * 
	 * */
	public static final String REDIS_COUNT="cm:screen:systemExceptionCount_%s";


//	String dataStr = "20161221";
//	System.out.println(	String.format(RedisKey.REDIS_COUNT, dataStr));    cm:screen:systemExceptionCount_20161221

	
	/**
	 * request_detail_key
	 * */
	public static final String REQUEST_DETAIL="cm:screen:requestDetail_%s";
	

	/**
	 * request_cache_key
	 * */
	public static final String REQUEST_CACHE="cm:screen:requestCache_%s";
	
	/**
	 * request_cache_key
	 * */
	public static final String REQUEST_CACHE_Window="cm:screen:windowRequestCache_%s";
	
	
	/**
	 * 平台总体情况
	 * cm:plat:request_count_yyyymmdd
	 * v1.1
	 * */
	public static final String Platfrom_Count="cm:plat:request_count_%s";
	
	/**
	 * 平台异常情况
	 * 	cm:plat:ExceptionCount_yyyymmdd
	 * v1.1
	 * */
	public static final String Platfrom_Exception_Count="cm:plat:ExceptionCount_%s";
	
	/**
	 * 系统总体情况
	 * cm:sys:systemID_yyyymmdd
	 * v1.1
	 * */
	public static final String System_Count="cm:sys:%s";
	/**
	 * 接口总体情况
	 * cm:api:systemID_apiID_yyyymmdd
	 * v1.1
	 * */
	public static final String Interface_Count="cm:api:%s";
	
	
	
	/**
	 * 错误警告日志list_key  list结构
	 * */
	public static final String LAST_EXCEPTION="cm:screen:lastExceptionLogs";
	
	public static final String LOG_NUMBER_COUNT="cm:screen:logNumberCount";
}
