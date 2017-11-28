package com.ylp.common.es.dto;

import java.io.Serializable;


/**
 * 接口总体情况（调用率最高的十个接口情况）
 */
public class ApiOverallInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// 接口编号
	private String apiId;
	// 接口名称
	private String apiName;
	// 接口url
	private String url;

	//总调用次数
	private int totalCall;
	//错误数
	private int successNum;
	//警告数
	private int faildNum;
	
	//最高耗时
	private double maxUseTime;
	//最低耗时
	private double minUseTime;
	//平均耗时
	private String aveUseTime;
	
	public String getApiId() {
		return apiId;
	}
	public void setApiId(String apiId) {
		this.apiId = apiId;
	}
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getTotalCall() {
		return totalCall;
	}
	public void setTotalCall(int totalCall) {
		this.totalCall = totalCall;
	}
	public int getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}
	public int getFaildNum() {
		return faildNum;
	}
	public void setFaildNum(int faildNum) {
		this.faildNum = faildNum;
	}
	public double getMaxUseTime() {
		return maxUseTime;
	}
	public void setMaxUseTime(double maxUseTime) {
		this.maxUseTime = maxUseTime;
	}
	public double getMinUseTime() {
		return minUseTime;
	}
	public void setMinUseTime(double minUseTime) {
		this.minUseTime = minUseTime;
	}
	public String getAveUseTime() {
		return aveUseTime;
	}
	public void setAveUseTime(String aveUseTime) {
		this.aveUseTime = aveUseTime;
	}
}
