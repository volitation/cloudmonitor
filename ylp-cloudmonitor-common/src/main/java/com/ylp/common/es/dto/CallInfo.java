package com.ylp.common.es.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * 平台，系统，接口总体调用情况dto
 * @author jack
 *
 */
public class CallInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	//总调用次数
	private int totalCall;
	
	
	//错误数
	private int errorNum;
	//警告数
	private int warnNum;
	//错误率
	private String errorRate = "0";
	
	
	
	//最高耗时
	private double maxUseTime;
	//最低耗时
	private double minUseTime;
	//平均耗时
	private String aveUseTime = "0";
	
	
	
	//调用频次 如500 次
	private int callFrequencyCount;
	//持续时间 30 min
	private int callFrequencyUseTime;
	//调用时间段，如（17:30-18:00）
	private String callFrequencyTime = "";
	
	//存储其他字段
	private Map<String,Object> map = new HashMap<String, Object>();

	public int getTotalCall() {
		return totalCall;
	}

	public void setTotalCall(int totalCall) {
		this.totalCall = totalCall;
	}

	public int getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

	public int getWarnNum() {
		return warnNum;
	}

	public void setWarnNum(int warnNum) {
		this.warnNum = warnNum;
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

	public int getCallFrequencyCount() {
		return callFrequencyCount;
	}

	public void setCallFrequencyCount(int callFrequencyCount) {
		this.callFrequencyCount = callFrequencyCount;
	}

	public int getCallFrequencyUseTime() {
		return callFrequencyUseTime;
	}

	public void setCallFrequencyUseTime(int callFrequencyUseTime) {
		this.callFrequencyUseTime = callFrequencyUseTime;
	}

	public String getCallFrequencyTime() {
		return callFrequencyTime;
	}

	public void setCallFrequencyTime(String callFrequencyTime) {
		this.callFrequencyTime = callFrequencyTime;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	
	public String getErrorRate() {
		return errorRate;
	}

	public void setErrorRate(String errorRate) {
		this.errorRate = errorRate;
	}

	public String getAveUseTime() {
		return aveUseTime;
	}

	public void setAveUseTime(String aveUseTime) {
		this.aveUseTime = aveUseTime;
	}

	@Override
	public String toString() {
		return "CallInfo [totalCall=" + totalCall + ", errorNum=" + errorNum
				+ ", warnNum=" + warnNum + ", errorRate=" + errorRate
				+ ", maxUseTime=" + maxUseTime + ", minUseTime=" + minUseTime
				+ ", aveUseTime=" + aveUseTime + ", callFrequencyCount="
				+ callFrequencyCount + ", callFrequencyUseTime="
				+ callFrequencyUseTime + ", callFrequencyTime="
				+ callFrequencyTime + ", map=" + map + "]";
	}
	
	
}
