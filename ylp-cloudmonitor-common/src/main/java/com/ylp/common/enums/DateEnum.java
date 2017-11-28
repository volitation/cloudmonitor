package com.ylp.common.enums;

public enum DateEnum {
	//注：枚举写在最前面，否则编译出错
	day("day","一天内的情况"),
	week("week","一周内七天的情况"),
	month("month","一个月内31天的情况"),
	year("year","一年内12个月的情况");
	
	
	
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

	private DateEnum(String enName,String cnName){
		this.enName = enName; 
		this.cnName = cnName;
	}
	
}
