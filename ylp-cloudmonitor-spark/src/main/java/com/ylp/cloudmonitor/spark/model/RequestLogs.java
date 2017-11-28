package com.ylp.cloudmonitor.spark.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ylp.cloudmonitor.spark.util.TimeUtil;
import com.ylp.common.enums.LogEnum;

/**
 * requestId; url; stratTime; stopTime; userName:用户名; role：操作角色;
 * entrepreneurId：商家编号; ip; browserType：浏览器类型; browserVersion：浏览器型号;
 * mobileBrand：手机品牌; systemVersion：系统型号; wechatVersion：微信版本; modelNumber：手机信号;
 * network：网络;
 */

public class RequestLogs implements Serializable {

	private String requestId;
	private String url;
	private String stratTime;
	private String stopTime;
	private String cl; // cl就是聚合后request的系统名称
	private String an;
	private String userName;
	private String role;
	private String entrepreneurId;
	private String ip;
	private String browserType;
	private String browserVersion;
	private String mobileBrand;
	private String systemVersion;
	private String wechatVersion;
	private String mobileVersion;
	private String network;
	private Integer warnCount;
	private Integer errorCount;
	private String level;
	private Integer errorCode;
	private String logList;
	private String useTime;
	private String systemId;
	private String apiId;
	private String frequency;

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public String getUseTime() {
		if (Double.valueOf(useTime)>3600.0){
			return String.valueOf(TimeUtil.useTime(stratTime, stopTime));
		}else{
			return useTime;
		}
	}

	public void setUseTime(String useTime) {
		if (Double.valueOf(useTime)>3600.0){
			this.useTime =String.valueOf(TimeUtil.useTime(stratTime, stopTime));
		}else{
			this.useTime = useTime;
		}
	
	}

	public String getLogList() {
		if(null==logList){
			logList="nullvalue";
		}
		return logList;
	}

	public void setLogList(String logList) {
		if(null==logList){
			logList="nullvalue";
		}
		this.logList = logList;
	}

	public Integer getErrorCode() {
		// return errorCode;
		try {
			if (null == errorCode) {
				if (this.level.equals(LogEnum.error.getEnName())) {
					return 400;
				} else if (this.level.equals(LogEnum.warn.getEnName())) {
					return 200;
				} else {
					return 0;
				}
			} else {
				return errorCode;
			}
		} catch (Exception e) {
			return 0;
		}

	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put(LogEnum.debug.getEnName(), 1);
		map.put(LogEnum.info.getEnName(), 2);
		map.put(LogEnum.warn.getEnName(), 3);
		map.put(LogEnum.error.getEnName(), 4);
		Integer oldvalue = 0;
		Integer newvalue = 0;
		if (null == this.level) {
			oldvalue = 0;
		} else {
			oldvalue = map.get(this.level.replace(" ", ""));
		}
		if (null == level) {
			newvalue = 0;
		} else {
			newvalue = map.get(level.replace(" ", ""));
		}
		if (newvalue > oldvalue) {
			if (null == this.level) {
				oldvalue = 0;
			} else {
				oldvalue = map.get(this.level.replace(" ", ""));
			}
			if (null == level) {
				newvalue = 0;
			} else {
				newvalue = map.get(level.replace(" ", ""));
			}
			if (newvalue > oldvalue) {
				this.level = level;
			}
		}
	}

	public void add(String level) {
		if (level.equals(LogEnum.error.getEnName())) {
			if (null == this.errorCount) {
				this.errorCount = 0;
			}
			this.errorCount = errorCount + 1;
		} else if (level.equals(LogEnum.warn.getEnName())) {
			if (null == this.warnCount) {
				this.warnCount = 0;
			}
			this.warnCount = warnCount + 1;
		}
	}

	public String getCl() {
		if (null == cl||cl.equals("null")) {
			return "其他服务";
		} else {
			return cl;
		}
	}

	public void setCl(String cl) {
		if (null == cl) {
			this.cl = "其他服务";
		} else {
			this.cl = cl;
		}
	}

	public String getAn() {
		if (null == an) {
			this.an = "nullvalue";
		}
		return an;
	}

	public void setAn(String an) {
		if (null == an) {
			this.an = "nullvalue";
		} else {
			this.an = an;
		}
	}

	public Integer getWarnCount() {

		if (null == warnCount) {
			return 0;
		} else {
			return warnCount;
		}
	}

	public void setWarnCount(Integer warnCount) {
		this.warnCount = warnCount;
	}

	public Integer getErrorCount() {
		if (null == errorCount) {
			return 0;
		} else {
			return errorCount;
		}
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public String getRequestId() {
		if (null == requestId) {
			this.requestId = "nullvalue";
		}
		return requestId;
	}

	public void setRequestId(String requestId) {
		if (null == requestId) {
			this.requestId = "nullvalue";
		} else {
			this.requestId = requestId;
		}
	}

	public String getUrl() {
		if (null == url) {
			this.url = "nullvalue";
		}
		return url;
	}

	public void setUrl(String url) {
		if (null == url) {
			this.url = "nullvalue";
		} else {
			this.url = url;
		}
	}

	public String getStratTime() {

		return stratTime;
	}

	public void setStratTime(String stratTime) {


		this.stratTime = stratTime;
	}

	public String getStopTime() {


		return stopTime;
	}

	public void setStopTime(String stopTime) {

		this.stopTime = stopTime;

	}

	public String getUserName() {
		if (null == userName) {
			this.userName = "nullvalue";
		}
		return userName;
	}

	public void setUserName(String userName) {

		if (null == userName) {
			this.userName = "nullvalue";
		} else {
			this.userName = userName;
		}
	}

	public String getRole() {
		if (null == role) {
			this.role = "nullvalue";
		}
		return role;
	}

	public void setRole(String role) {

		if (null == role) {
			this.role = "nullvalue";
		} else {
			this.role = role;
		}
	}

	public String getEntrepreneurId() {
		if (null == entrepreneurId) {
			this.entrepreneurId = "nullvalue";
		}
		return entrepreneurId;
	}

	public void setEntrepreneurId(String entrepreneurId) {

		if (null == entrepreneurId) {
			this.entrepreneurId = "nullvalue";
		} else {
			this.entrepreneurId = entrepreneurId;
		}
	}

	public String getIp() {
		if (null == ip) {
			this.ip = "nullvalue";
		}
		return ip;
	}

	public void setIp(String ip) {

		if (null == ip) {
			this.ip = "nullvalue";
		} else {
			this.ip = ip;
		}
	}

	public String getBrowserType() {
		if (null == browserType) {
			this.browserType = "nullvalue";
		}
		return browserType;
	}

	public void setBrowserType(String browserType) {

		if (null == browserType) {
			this.browserType = "nullvalue";
		} else {
			this.browserType = browserType;
		}
	}

	public String getBrowserVersion() {
		if (null == browserVersion) {
			this.browserVersion = "nullvalue";
		}
		return browserVersion;
	}

	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
		if (null == browserVersion) {
			this.browserVersion = "nullvalue";
		} else {
			this.browserVersion = browserVersion;
		}
	}

	public String getMobileBrand() {
		if (null == mobileBrand) {
			this.mobileBrand = "nullvalue";
		}
		return mobileBrand;
	}

	public void setMobileBrand(String mobileBrand) {
		if (null == mobileBrand) {
			this.mobileBrand = "nullvalue";
		} else {
			this.mobileBrand = mobileBrand;
		}
	}

	public String getSystemVersion() {
		if (null == systemVersion) {
			this.systemVersion = "nullvalue";
		}
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		if (null == systemVersion) {
			this.systemVersion = "nullvalue";
		} else {
			this.systemVersion = systemVersion;
		}
	}

	public String getWechatVersion() {
		if (null == wechatVersion) {
			this.wechatVersion = "nullvalue";
		}
		return wechatVersion;
	}

	public void setWechatVersion(String wechatVersion) {

		if (null == wechatVersion) {
			this.wechatVersion = "nullvalue";
		} else {
			this.wechatVersion = wechatVersion;
		}
	}

	public String getMobileVersion() {
		if (null == mobileVersion) {
			this.mobileVersion = "nullvalue";
		}
		return mobileVersion;
	}

	public void setMobileVersion(String mobileVersion) {

		if (null == mobileVersion) {
			this.mobileVersion = "nullvalue";
		} else {
			this.mobileVersion = mobileVersion;
		}
	}

	public String getNetwork() {
		if (null == network) {
			this.network = "nullvalue";
		}
		return network;
	}

	public void setNetwork(String network) {
		if (null == network) {
			this.network = "nullvalue";
		} else {
			this.network = network;
		}
	}

}
