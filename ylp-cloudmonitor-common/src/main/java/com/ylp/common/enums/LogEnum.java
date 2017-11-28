package com.ylp.common.enums;

/**
 * 日志级别枚举类
 * @author chenyw
 * Dec 9, 2016 11:32:59 AM
 *
 */
public enum LogEnum {
	//注：枚举写在最前面，否则编译出错
	error("error","错误日志"),
	warn("warn","警告日志"),
	info("info","通知"),
	debug("debug","调试");
	
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

	private LogEnum(String enName,String cnName){
		this.enName = enName; 
		this.cnName = cnName;
	}
}
