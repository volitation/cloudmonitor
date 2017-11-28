package com.ylp.cloudmonitor.spark.model;

import java.io.Serializable;

import com.ylp.common.enums.LogEnum;

public class RowLog implements Serializable {
	private String version;
	private String Language;
	private String time;
	private String level;
	private String host;
	private String codeLocation;
	private String requestId;
	private String detail;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getLanguage() {
		return Language;
	}
	public void setLanguage(String language) {
		Language = language;
	}
	public String getTime() {
		if(time.contains(",")){
			time=time.replace(",", ".");
		}
		return time;
	}
	public void setTime(String time) {
		if(time.contains(",")){
			time=time.replace(",", ".");
		}
		this.time = time;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		if(level.replace(" ", "").equals("INFO")){
			this.level=LogEnum.info.getEnName();
		}else if(level.replace(" ", "").equals("DEBUG")){
			this.level=LogEnum.debug.getEnName();
		}else if(level.replace(" ", "").equals("WARN")){
			this.level=LogEnum.warn.getEnName();
		}else if (level.replace(" ", "").equals("ERROR")){
			this.level=LogEnum.error.getEnName();
		}else {
			{
				this.level = level.replace(" ", "");
			}
		}
		
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getCodeLocation() {
		return codeLocation;
	}
	public void setCodeLocation(String codeLocation) {
		this.codeLocation = codeLocation;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public String toString(RowLog log){
		 String version = log.getVersion();
		 String language = log.getLanguage();
		 String time = log.getTime();
		 String level = log.getLevel();
		 String host = log.getHost();
		 String codeLocation = log.getCodeLocation();
		 String requestId = log.getRequestId();
		 String detail = log.getDetail();
		 
		 return version +"|"+language+"|"+time+"|"+level+"|"+host+"|"+codeLocation+"|"+requestId+"|"+detail;
	}

}
