package com.ylp.common.enums;

/**
 * 索引库枚举类
 * @author chenyw
 * Dec 9, 2016 11:32:59 AM
 *
 */
public enum IndexEnum {
	//注：枚举写在最前面，否则编译出错
	payIndex("pay","支付索引库"),
	accountIndex("account","账户索引库");
	
	
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

	private IndexEnum(String enName,String cnName){
		this.enName = enName; 
		this.cnName = cnName;
	}
}
