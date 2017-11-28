package com.ylp.common.enums;

public enum TypeEnum {
	//注：枚举写在最前面，否则编译出错
	detailType("detail","颗粒数据"),
	requestType("request","requestId聚合后的数据");
	
	
	
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

	private TypeEnum(String enName,String cnName){
		this.enName = enName; 
		this.cnName = cnName;
	}
	
}
