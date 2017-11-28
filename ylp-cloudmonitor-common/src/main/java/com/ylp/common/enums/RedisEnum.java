package com.ylp.common.enums;

public enum RedisEnum {
	//统计相关
	request_count("request_count","总调用次数"),
	error_number("error_number","错误数"),
	warn_number("warn_number","警告数"),
	max_using_time("max_using_time","最大耗时"),
	total_using_time("total_using_time","总耗时"),
	min_using_time("min_using_time","最小耗时"),
	max_frequency("max_frequency","最高调用频次"),
	max_frequency_time("max_frequency_time","最高调用频次时间段"),
	max_frequency_value("max_frequency_value","最高调用频次调用次数"),
	//各频次调用次数
	frequency_0000_0030("frequency_00:00-00:30","00:00-00:30的调用频次"),
	frequency_0030_0100("frequency_00:30-01:00","00:30-01:00的调用频次"),
	frequency_0100_0130("frequency_01:00-01:30","01:00-01:30的调用频次"),
	frequency_0130_0200("frequency_01:30-02:00","01:30-02:00的调用频次"),
	frequency_0200_0230("frequency_02:00-02:30","02:00-02:30的调用频次"),
	frequency_0230_0300("frequency_02:30-03:00","02:30-03:00的调用频次"),
	frequency_0300_0330("frequency_03:00-03:30","03:00-03:30的调用频次"),
	frequency_0330_0400("frequency_03:30-04:00","03:30-04:00的调用频次"),
	frequency_0400_0430("frequency_04:00-04:30","04:00-04:30的调用频次"),
	frequency_0430_0500("frequency_04:30-05:00","04:30-05:00的调用频次"),
	frequency_0500_0530("frequency_05:00-05:30","05:00-05:30的调用频次"),
	frequency_0530_0600("frequency_05:30-06:00","05:30-06:00的调用频次"),
	frequency_0600_0630("frequency_06:00-06:30","06:00-06:30的调用频次"),
	frequency_0630_0700("frequency_06:30-07:00","06:30-07:00的调用频次"),
	frequency_0700_0730("frequency_07:00-07:30","07:00-07:30的调用频次"),
	frequency_0730_0800("frequency_07:30-08:00","07:30-08:00的调用频次"),
	frequency_0800_0830("frequency_08:00-08:30","08:00-08:30的调用频次"),
	frequency_0830_0900("frequency_08:30-09:00","08:30-09:00的调用频次"),
	frequency_0900_0930("frequency_09:00-09:30","09:00-09:30的调用频次"),
	frequency_0930_1000("frequency_09:30-10:00","09:30-10:00的调用频次"),
	frequency_1000_1030("frequency_10:00-10:30","10:00-10:30的调用频次"),
	frequency_1030_1100("frequency_10:30-11:00","10:30-11:00的调用频次"),
	frequency_1100_1130("frequency_11:00-11:30","11:00-11:30的调用频次"),
	frequency_1130_1200("frequency_11:30-12:00","11:30-12:00的调用频次"),
	frequency_1200_1230("frequency_12:00-12:30","12:00-12:30的调用频次"),
	frequency_1230_1300("frequency_12:30-13:00","12:30-13:00的调用频次"),
	frequency_1300_1330("frequency_13:00-13:30","13:00-13:30的调用频次"),
	frequency_1330_1400("frequency_13:30-14:00","13:30-14:00的调用频次"),
	frequency_1400_1430("frequency_14:00-14:30","14:00-14:30的调用频次"),
	frequency_1430_1500("frequency_14:30-15:00","14:30-15:00的调用频次"),
	frequency_1500_1530("frequency_15:00-15:30","15:00-15:30的调用频次"),
	frequency_1530_1600("frequency_15:30-16:00","15:30-16:00的调用频次"),
	frequency_1600_1630("frequency_16:00-16:30","16:00-16:30的调用频次"),
	frequency_1630_1700("frequency_16:30-17:00","16:30-17:00的调用频次"),
	frequency_1700_1730("frequency_17:00-17:30","17:00-17:30的调用频次"),
	frequency_1730_1800("frequency_17:30-18:00","17:30-18:00的调用频次"),
	frequency_1800_1830("frequency_18:00-18:30","18:00-18:30的调用频次"),
	frequency_1830_1900("frequency_18:30-19:00","18:30-19:00的调用频次"),
	frequency_1900_1930("frequency_19:00-19:30","19:00-19:30的调用频次"),
	frequency_1930_2000("frequency_19:30-20:00","19:30-20:00的调用频次"),
	frequency_2000_2030("frequency_20:00-20:30","20:00-20:30的调用频次"),
	frequency_2030_2100("frequency_20:30-21:00","20:30-21:00的调用频次"),
	frequency_2100_2130("frequency_21:00-21:30","21:00-21:30的调用频次"),
	frequency_2130_2200("frequency_21:30-22:00","21:30-22:00的调用频次"),
	frequency_2200_2230("frequency_22:00-22:30","22:00-22:30的调用频次"),
	frequency_2230_2300("frequency_22:30-23:00","22:30-23:00的调用频次"),
	frequency_2300_2330("frequency_23:00-23:30","23:00-23:30的调用频次"),
	frequency_2330_0000("frequency_23:30-00:00","23:30-00:00的调用频次"),

	
	//异常日志相关
	requestId("request_id","请求id"),
	logTime("log_time","日志时间"),
	systemName("sys_name","系统名"),
	apiName("api_name","接口名"),
	logType("log_type","日志类别"),
	eco("eco","错误码"),
	usingTime("using_time","耗时"),
	logDetail("ld","日志详情");

	
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

	private RedisEnum(String enName,String cnName){
		this.enName = enName; 
		this.cnName = cnName;
	}
}
