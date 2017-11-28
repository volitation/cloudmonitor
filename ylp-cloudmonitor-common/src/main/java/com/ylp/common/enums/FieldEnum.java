package com.ylp.common.enums;


/**
 * 颗粒数据类型 字段名称
 * @author chenyw
 * Dec 9, 2016 2:11:46 PM
 *
 */
public enum FieldEnum {

	
	//第一层颗粒颗粒数据私有字段 7+17个
	version("version","日志版本"), //V1
	language("language","语言"), //java
	time("time","日志时间"), //20161209 11:19:00
	level("log_level","日志级别"), //debug
	host("host","主机"), //192.168.2.10
	location("location","代码位置"), //payController:25
	requestId("requestid","请求id"), //2661fdfbhb2y32uh1j12121fbgb4w29
	
	//第二层聚合数据私有字段 6+17个
	errorCount("error_count","错误总数"),//233
	warnCount("warn_count","警告总数"),//99
	errorCode("error_code","错误码"), //10012
	interfaceName("interface_name","接口名称"), //接口中文名称  查询订单
	systemName("system_name","系统名称"), //系统中文名称  支付系统
	logList("log_list","日志汇总"),//存储所有系统汇总的日志
    usingTime("using_time", "消耗时间"),//该接口消耗时间
	
	
	
	//公共字段 17个
    sysBizId("sysBizId","系统id"),
    apiId("apiId","接口id"),
	url("url","url"), //http://mp.yolipai.com
	user("user","用户名"),//联讯商家
	role("role","操作角色"), //admin
	ylpEnterpriseAccountNo("ylpenterprise_account_no","商家编号"), //E8882016120910006577
	ip("ip","ip地址"), //101.121.10.9
	browserType("browser_type","浏览器类型"), //chrome
	browserVersion("browser_version","浏览器版本"), //54
	mobile("mobile","手机品牌"), //apple
	systemVersion("system_version","系统版本"), //ios 10.6
	mobileVersion("mobile_version","手机版本"), //7.1
	mobileNum("mobile_num","手机型号"), //iphone7
	networkType("network_type","网络类型"), //wifi
	startTime("start_time","请求开始时间"),//20161212 11:19:00
	endTime("end_time","请求结束时间"),//20161212 11:19:00
	callLocation ("call_location","请求所属系统"),//pay
	logDetail("log_detail","日志详情"), //日志日常信息
	extend("extend","拓展属性");//json字符串对象log4js:{ levels:{"log_file": "ALL","console": "INFO","log_date": "ALL"} }
	
	
	private String enName;
	private String cnName;
	
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	private FieldEnum(String enName,String cnName){
		this.enName = enName; 
		this.cnName = cnName;
	}

}
