package com.ylp.common.es.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * log 对象，
 * @author chenyw
 * Dec 15, 2016 1:55:48 PM
 *
 */
public class LogModel implements Serializable {

	/**
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String requestid;
	
	//日志生成时间
	private String logTime;
	
	//系统名称
	private String systemName; 
	
	//日志类型表示error warning info debug
	private String logLevel;   

	//请求url
	private String url;
	
	//接口名称
	private String interfaceName;
	
	private String errorCode;
	
	//private String logText;

	private String logList;
	
	private List<Map<String, Object>> docs;
	
	
	
	
	public String getLogList() {
		return logList;
	}

	public void setLogList(String logList) {
		this.logList = logList;
	}

	public List<Map<String, Object>> getDocs() {
		return docs;
	}

	public void setDocs(List<Map<String, Object>> docs) {
		this.docs = docs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestid() {
		return requestid;
	}

	public void setRequestid(String requestid) {
		this.requestid = requestid;
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

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}



	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

//	public String getLogText() {
//		return logText;
//	}
//
//	public void setLogText(String logText) {
//		this.logText = logText;
//	}

	@Override
	public String toString() {
		return "Log [id=" + id + ", requestid=" + requestid + ", logTime="
				+ logTime + ", SystemName=" + systemName + ", logLevel=" + logLevel
				+ ", url=" + url + ", interfaceName=" + interfaceName
				+ ", errorCode=" + errorCode + ", logList=" + logList + "]";
	}
	
	
	
}
