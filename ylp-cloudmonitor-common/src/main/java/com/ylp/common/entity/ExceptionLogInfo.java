package com.ylp.common.entity;

import com.ylp.common.enums.RedisEnum;

import net.sf.json.JSONObject;

public class ExceptionLogInfo {
	private String requestId;
	private String logTime;
	private String systemName;
	private String logType;
	private String apiName;
	private String eco;
	private String usingTime;
	private String logDetail;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String interfaceName) {
		this.apiName = interfaceName;
	}

	public String getEco() {
		return eco;
	}

	public void setEco(String eco) {
		this.eco = eco;
	}

	public String getUsingTime() {
		return usingTime;
	}

	public void setUsingTime(String usingTime) {
		this.usingTime = usingTime;
	}

	public String getLogDetail() {
		return logDetail;
	}

	public void setLogDetail(String logDetail) {
		this.logDetail = logDetail;
	}

	public String toJSONString() {
		String s = "{" + "\""+RedisEnum.requestId.getEnName()+"\":" + "\"" + requestId + "\"" + "," 
	            + "\""+RedisEnum.logTime.getEnName()+"\":" +"\"" +  logTime +"\"" +  ","
				+ "\""+RedisEnum.systemName.getEnName()+"\":" + "\"" + systemName+ "\"" + "," 
	            + "\""+RedisEnum.logType.getEnName()+"\":" + "\"" + logType + "\"" + "," 
				+ "\""+RedisEnum.apiName.getEnName()+"\":" + "\"" + apiName +"\"" +  ","
				+ "\""+RedisEnum.eco.getEnName()+"\":" +"\"" +  eco + "\"" + "," 
				+ "\""+RedisEnum.usingTime.getEnName()+"\":" + "\"" + usingTime + "\"" + "," 
				+ "\""+RedisEnum.logDetail.getEnName()+"\":" +"\"" +  logDetail +"\"" +  "}";
		return s;
	}
	
	public static ExceptionLogInfo JSONStringToInfo(String s){
		
		JSONObject obj = JSONObject.fromObject(s);
		ExceptionLogInfo info =new ExceptionLogInfo();
		info.setSystemName(obj.getString(RedisEnum.systemName.getEnName()));
		info.setApiName(obj.getString(RedisEnum.apiName.getEnName()));
		info.setLogTime(obj.getString(RedisEnum.logTime.getEnName()));
		info.setEco(obj.getString(RedisEnum.eco.getEnName()));
		info.setLogType(obj.getString(RedisEnum.logType.getEnName()));
		info.setRequestId(obj.getString(RedisEnum.requestId.getEnName()));
		info.setUsingTime(obj.getString(RedisEnum.usingTime.getEnName()));
		info.setLogDetail(obj.getString(RedisEnum.logDetail.getEnName()));

		return info;
		
	}
}
