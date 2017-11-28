package com.ylp.common.entity;


import com.ylp.common.enums.RedisEnum;


public class RequestCountInfo {
//	request_count("request_count","总调用次数"),
//	error_number("error_number","错误数"),
//	warn_number("warn_number","警告数"),
//	max_using_time("max_using_time","最大耗时"),
//	total_using_time("total_using_time","总耗时"),
//	min_using_time("min_using_time","最小耗时"),
//	max_frequency("max_frequency","最高调用频次"),
	private String requestCount;
	private String errorNumber;
	private String warnNumber;
	private String totalUsingTime;
	private String maxUsingTime;
	private String minUsingTime;
	private String maxFrequency;
	
	//各频次调用次数
		private String frequency_0000_0030;
		private String frequency_0030_0100;
		private String frequency_0100_0130;
		private String frequency_0130_0200;
		private String frequency_0200_0230;
		private String frequency_0230_0300;
		private String frequency_0300_0330;
		private String frequency_0330_0400;
		private String frequency_0400_0430;
		private String frequency_0430_0500;
		private String frequency_0500_0530;
		private String frequency_0530_0600;
		private String frequency_0600_0630;
		private String frequency_0630_0700;
		private String frequency_0700_0730;
		private String frequency_0730_0800;
		private String frequency_0800_0830;
		private String frequency_0830_0900;
		private String frequency_0900_0930;
		private String frequency_0930_1000;
		private String frequency_1000_1030;
		private String frequency_1030_1100;
		private String frequency_1100_1130;
		private String frequency_1130_1200;
		private String frequency_1200_1230;
		private String frequency_1230_1300;
		private String frequency_1300_1330;
		private String frequency_1330_1400;
		private String frequency_1400_1430;
		private String frequency_1430_1500;
		private String frequency_1500_1530;
		private String frequency_1530_1600;
		private String frequency_1600_1630;
		private String frequency_1630_1700;
		private String frequency_1700_1730;
		private String frequency_1730_1800;
		private String frequency_1800_1830;
		private String frequency_1830_1900;
		private String frequency_1900_1930;
		private String frequency_1930_2000;
		private String frequency_2000_2030;
		private String frequency_2030_2100;
		private String frequency_2100_2130;
		private String frequency_2130_2200;
		private String frequency_2200_2230;
		private String frequency_2230_2300;
		private String frequency_2300_2330;
		private String frequency_2330_0000;
	
	public String getRequestCount() {
		return requestCount;
	}
	public void setRequestCount(String requestCount) {
		this.requestCount = requestCount;
	}
	public String getErrorNumber() {
		return errorNumber;
	}
	public void setErrorNumber(String errorNumber) {
		this.errorNumber = errorNumber;
	}
	public String getWarnNumber() {
		return warnNumber;
	}
	public void setWarnNumber(String warnNumber) {
		this.warnNumber = warnNumber;
	}
	public String getTotalUsingTime() {
		return totalUsingTime;
	}
	public void setTotalUsingTime(String totalUsingTime) {
		this.totalUsingTime = totalUsingTime;
	}
	public String getMaxUsingTime() {
		return maxUsingTime;
	}
	public void setMaxUsingTime(String maxUsingTime) {
		this.maxUsingTime = maxUsingTime;
	}
	public String getMinUsingTime() {
		return minUsingTime;
	}
	public void setMinUsingTime(String minUsingTime) {
		this.minUsingTime = minUsingTime;
	}
	
	public String getMaxFrequency() {
		return maxFrequency;
	}
	public void setMaxFrequency(String maxFrequency) {
		this.maxFrequency = maxFrequency;
	}
	public String getFrequency_0000_0030() {
		return frequency_0000_0030;
	}
	public void setFrequency_0000_0030(String frequency_0000_0030) {
		this.frequency_0000_0030 = frequency_0000_0030;
	}
	public String getFrequency_0030_0100() {
		return frequency_0030_0100;
	}
	public void setFrequency_0030_0100(String frequency_0030_0100) {
		this.frequency_0030_0100 = frequency_0030_0100;
	}
	public String getFrequency_0100_0130() {
		return frequency_0100_0130;
	}
	public void setFrequency_0100_0130(String frequency_0100_0130) {
		this.frequency_0100_0130 = frequency_0100_0130;
	}
	public String getFrequency_0130_0200() {
		return frequency_0130_0200;
	}
	public void setFrequency_0130_0200(String frequency_0130_0200) {
		this.frequency_0130_0200 = frequency_0130_0200;
	}
	public String getFrequency_0200_0230() {
		return frequency_0200_0230;
	}
	public void setFrequency_0200_0230(String frequency_0200_0230) {
		this.frequency_0200_0230 = frequency_0200_0230;
	}
	public String getFrequency_0230_0300() {
		return frequency_0230_0300;
	}
	public void setFrequency_0230_0300(String frequency_0230_0300) {
		this.frequency_0230_0300 = frequency_0230_0300;
	}
	public String getFrequency_0300_0330() {
		return frequency_0300_0330;
	}
	public void setFrequency_0300_0330(String frequency_0300_0330) {
		this.frequency_0300_0330 = frequency_0300_0330;
	}
	public String getFrequency_0330_0400() {
		return frequency_0330_0400;
	}
	public void setFrequency_0330_0400(String frequency_0330_0400) {
		this.frequency_0330_0400 = frequency_0330_0400;
	}
	public String getFrequency_0400_0430() {
		return frequency_0400_0430;
	}
	public void setFrequency_0400_0430(String frequency_0400_0430) {
		this.frequency_0400_0430 = frequency_0400_0430;
	}
	public String getFrequency_0430_0500() {
		return frequency_0430_0500;
	}
	public void setFrequency_0430_0500(String frequency_0430_0500) {
		this.frequency_0430_0500 = frequency_0430_0500;
	}
	public String getFrequency_0500_0530() {
		return frequency_0500_0530;
	}
	public void setFrequency_0500_0530(String frequency_0500_0530) {
		this.frequency_0500_0530 = frequency_0500_0530;
	}
	public String getFrequency_0530_0600() {
		return frequency_0530_0600;
	}
	public void setFrequency_0530_0600(String frequency_0530_0600) {
		this.frequency_0530_0600 = frequency_0530_0600;
	}
	public String getFrequency_0600_0630() {
		return frequency_0600_0630;
	}
	public void setFrequency_0600_0630(String frequency_0600_0630) {
		this.frequency_0600_0630 = frequency_0600_0630;
	}
	public String getFrequency_0630_0700() {
		return frequency_0630_0700;
	}
	public void setFrequency_0630_0700(String frequency_0630_0700) {
		this.frequency_0630_0700 = frequency_0630_0700;
	}
	public String getFrequency_0700_0730() {
		return frequency_0700_0730;
	}
	public void setFrequency_0700_0730(String frequency_0700_0730) {
		this.frequency_0700_0730 = frequency_0700_0730;
	}
	public String getFrequency_0730_0800() {
		return frequency_0730_0800;
	}
	public void setFrequency_0730_0800(String frequency_0730_0800) {
		this.frequency_0730_0800 = frequency_0730_0800;
	}
	public String getFrequency_0800_0830() {
		return frequency_0800_0830;
	}
	public void setFrequency_0800_0830(String frequency_0800_0830) {
		this.frequency_0800_0830 = frequency_0800_0830;
	}
	public String getFrequency_0830_0900() {
		return frequency_0830_0900;
	}
	public void setFrequency_0830_0900(String frequency_0830_0900) {
		this.frequency_0830_0900 = frequency_0830_0900;
	}
	public String getFrequency_0900_0930() {
		return frequency_0900_0930;
	}
	public void setFrequency_0900_0930(String frequency_0900_0930) {
		this.frequency_0900_0930 = frequency_0900_0930;
	}
	public String getFrequency_0930_1000() {
		return frequency_0930_1000;
	}
	public void setFrequency_0930_1000(String frequency_0930_1000) {
		this.frequency_0930_1000 = frequency_0930_1000;
	}
	public String getFrequency_1000_1030() {
		return frequency_1000_1030;
	}
	public void setFrequency_1000_1030(String frequency_1000_1030) {
		this.frequency_1000_1030 = frequency_1000_1030;
	}
	public String getFrequency_1030_1100() {
		return frequency_1030_1100;
	}
	public void setFrequency_1030_1100(String frequency_1030_1100) {
		this.frequency_1030_1100 = frequency_1030_1100;
	}
	public String getFrequency_1100_1130() {
		return frequency_1100_1130;
	}
	public void setFrequency_1100_1130(String frequency_1100_1130) {
		this.frequency_1100_1130 = frequency_1100_1130;
	}
	public String getFrequency_1130_1200() {
		return frequency_1130_1200;
	}
	public void setFrequency_1130_1200(String frequency_1130_1200) {
		this.frequency_1130_1200 = frequency_1130_1200;
	}
	public String getFrequency_1200_1230() {
		return frequency_1200_1230;
	}
	public void setFrequency_1200_1230(String frequency_1200_1230) {
		this.frequency_1200_1230 = frequency_1200_1230;
	}
	public String getFrequency_1230_1300() {
		return frequency_1230_1300;
	}
	public void setFrequency_1230_1300(String frequency_1230_1300) {
		this.frequency_1230_1300 = frequency_1230_1300;
	}
	public String getFrequency_1300_1330() {
		return frequency_1300_1330;
	}
	public void setFrequency_1300_1330(String frequency_1300_1330) {
		this.frequency_1300_1330 = frequency_1300_1330;
	}
	public String getFrequency_1330_1400() {
		return frequency_1330_1400;
	}
	public void setFrequency_1330_1400(String frequency_1330_1400) {
		this.frequency_1330_1400 = frequency_1330_1400;
	}
	public String getFrequency_1400_1430() {
		return frequency_1400_1430;
	}
	public void setFrequency_1400_1430(String frequency_1400_1430) {
		this.frequency_1400_1430 = frequency_1400_1430;
	}
	public String getFrequency_1430_1500() {
		return frequency_1430_1500;
	}
	public void setFrequency_1430_1500(String frequency_1430_1500) {
		this.frequency_1430_1500 = frequency_1430_1500;
	}
	public String getFrequency_1500_1530() {
		return frequency_1500_1530;
	}
	public void setFrequency_1500_1530(String frequency_1500_1530) {
		this.frequency_1500_1530 = frequency_1500_1530;
	}
	public String getFrequency_1530_1600() {
		return frequency_1530_1600;
	}
	public void setFrequency_1530_1600(String frequency_1530_1600) {
		this.frequency_1530_1600 = frequency_1530_1600;
	}
	public String getFrequency_1600_1630() {
		return frequency_1600_1630;
	}
	public void setFrequency_1600_1630(String frequency_1600_1630) {
		this.frequency_1600_1630 = frequency_1600_1630;
	}
	public String getFrequency_1630_1700() {
		return frequency_1630_1700;
	}
	public void setFrequency_1630_1700(String frequency_1630_1700) {
		this.frequency_1630_1700 = frequency_1630_1700;
	}
	public String getFrequency_1700_1730() {
		return frequency_1700_1730;
	}
	public void setFrequency_1700_1730(String frequency_1700_1730) {
		this.frequency_1700_1730 = frequency_1700_1730;
	}
	public String getFrequency_1730_1800() {
		return frequency_1730_1800;
	}
	public void setFrequency_1730_1800(String frequency_1730_1800) {
		this.frequency_1730_1800 = frequency_1730_1800;
	}
	public String getFrequency_1800_1830() {
		return frequency_1800_1830;
	}
	public void setFrequency_1800_1830(String frequency_1800_1830) {
		this.frequency_1800_1830 = frequency_1800_1830;
	}
	public String getFrequency_1830_1900() {
		return frequency_1830_1900;
	}
	public void setFrequency_1830_1900(String frequency_1830_1900) {
		this.frequency_1830_1900 = frequency_1830_1900;
	}
	public String getFrequency_1900_1930() {
		return frequency_1900_1930;
	}
	public void setFrequency_1900_1930(String frequency_1900_1930) {
		this.frequency_1900_1930 = frequency_1900_1930;
	}
	public String getFrequency_1930_2000() {
		return frequency_1930_2000;
	}
	public void setFrequency_1930_2000(String frequency_1930_2000) {
		this.frequency_1930_2000 = frequency_1930_2000;
	}
	public String getFrequency_2000_2030() {
		return frequency_2000_2030;
	}
	public void setFrequency_2000_2030(String frequency_2000_2030) {
		this.frequency_2000_2030 = frequency_2000_2030;
	}
	public String getFrequency_2030_2100() {
		return frequency_2030_2100;
	}
	public void setFrequency_2030_2100(String frequency_2030_2100) {
		this.frequency_2030_2100 = frequency_2030_2100;
	}
	public String getFrequency_2100_2130() {
		return frequency_2100_2130;
	}
	public void setFrequency_2100_2130(String frequency_2100_2130) {
		this.frequency_2100_2130 = frequency_2100_2130;
	}
	public String getFrequency_2130_2200() {
		return frequency_2130_2200;
	}
	public void setFrequency_2130_2200(String frequency_2130_2200) {
		this.frequency_2130_2200 = frequency_2130_2200;
	}
	public String getFrequency_2200_2230() {
		return frequency_2200_2230;
	}
	public void setFrequency_2200_2230(String frequency_2200_2230) {
		this.frequency_2200_2230 = frequency_2200_2230;
	}
	public String getFrequency_2230_2300() {
		return frequency_2230_2300;
	}
	public void setFrequency_2230_2300(String frequency_2230_2300) {
		this.frequency_2230_2300 = frequency_2230_2300;
	}
	public String getFrequency_2300_2330() {
		return frequency_2300_2330;
	}
	public void setFrequency_2300_2330(String frequency_2300_2330) {
		this.frequency_2300_2330 = frequency_2300_2330;
	}
	public String getFrequency_2330_0000() {
		return frequency_2330_0000;
	}
	public void setFrequency_2330_0000(String frequency_2330_0000) {
		this.frequency_2330_0000 = frequency_2330_0000;
	}
	
	public String toJSONString(){
		String s ="{" + "\""+RedisEnum.request_count.getEnName()+"\":" + "\"" + requestCount + "\"" + "," 
				+ "\""+RedisEnum.error_number.getEnName()+"\":" +"\"" +  errorNumber +"\"" +  ","
				+ "\""+RedisEnum.warn_number.getEnName()+"\":" +"\"" + warnNumber +"\"" +  ","
				+ "\""+RedisEnum.total_using_time.getEnName()+"\":" +"\"" + totalUsingTime +"\"" +  ","
				+ "\""+RedisEnum.max_using_time.getEnName()+"\":" +"\"" +  maxUsingTime +"\"" +  ","
				+ "\""+RedisEnum.min_using_time.getEnName()+"\":" +"\"" +  minUsingTime +"\"" +  ","
				+ "\""+RedisEnum.max_frequency.getEnName()+"\":" +"\"" +  maxFrequency +"\"" +  ","
				+ "\""+RedisEnum.frequency_0000_0030.getEnName()+"\":" +"\"" +  frequency_0000_0030 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0030_0100.getEnName()+"\":" +"\"" +  frequency_0030_0100 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0100_0130.getEnName()+"\":" +"\"" +  frequency_0100_0130 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0130_0200.getEnName()+"\":" +"\"" +  frequency_0130_0200 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0200_0230.getEnName()+"\":" +"\"" +  frequency_0200_0230 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0230_0300.getEnName()+"\":" +"\"" +  frequency_0230_0300 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0300_0330.getEnName()+"\":" +"\"" +  frequency_0300_0330 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0330_0400.getEnName()+"\":" +"\"" +  frequency_0330_0400 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0400_0430.getEnName()+"\":" +"\"" +  frequency_0400_0430 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0430_0500.getEnName()+"\":" +"\"" +  frequency_0430_0500 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0500_0530.getEnName()+"\":" +"\"" +  frequency_0500_0530 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0530_0600.getEnName()+"\":" +"\"" +  frequency_0530_0600 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0600_0630.getEnName()+"\":" +"\"" +  frequency_0600_0630 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0630_0700.getEnName()+"\":" +"\"" +  frequency_0630_0700 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0700_0730.getEnName()+"\":" +"\"" +  frequency_0700_0730 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0730_0800.getEnName()+"\":" +"\"" +  frequency_0730_0800 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0800_0830.getEnName()+"\":" +"\"" +  frequency_0800_0830 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0830_0900.getEnName()+"\":" +"\"" +  frequency_0830_0900 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0900_0930.getEnName()+"\":" +"\"" +  frequency_0900_0930 +"\"" +  ","
				+ "\""+RedisEnum.frequency_0930_1000.getEnName()+"\":" +"\"" +  frequency_0930_1000 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1000_1030.getEnName()+"\":" +"\"" +  frequency_1000_1030 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1030_1100.getEnName()+"\":" +"\"" +  frequency_1030_1100 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1100_1130.getEnName()+"\":" +"\"" +  frequency_1100_1130 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1130_1200.getEnName()+"\":" +"\"" +  frequency_1130_1200 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1200_1230.getEnName()+"\":" +"\"" +  frequency_1200_1230 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1230_1300.getEnName()+"\":" +"\"" +  frequency_1230_1300 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1300_1330.getEnName()+"\":" +"\"" +  frequency_1300_1330 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1330_1400.getEnName()+"\":" +"\"" +  frequency_1330_1400 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1400_1430.getEnName()+"\":" +"\"" +  frequency_1400_1430 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1430_1500.getEnName()+"\":" +"\"" +  frequency_1430_1500 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1500_1530.getEnName()+"\":" +"\"" +  frequency_1500_1530 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1530_1600.getEnName()+"\":" +"\"" +  frequency_1530_1600 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1600_1630.getEnName()+"\":" +"\"" +  frequency_1600_1630+"\"" +  ","
				+ "\""+RedisEnum.frequency_1630_1700.getEnName()+"\":" +"\"" +  frequency_1630_1700 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1700_1730.getEnName()+"\":" +"\"" +  frequency_1700_1730+"\"" +  ","
				+ "\""+RedisEnum.frequency_1730_1800.getEnName()+"\":" +"\"" +  frequency_1730_1800 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1800_1830.getEnName()+"\":" +"\"" +  frequency_1800_1830+"\"" +  ","
				+ "\""+RedisEnum.frequency_1830_1900.getEnName()+"\":" +"\"" +  frequency_1830_1900 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1900_1930.getEnName()+"\":" +"\"" +  frequency_1900_1930 +"\"" +  ","
				+ "\""+RedisEnum.frequency_1930_2000.getEnName()+"\":" +"\"" +  frequency_1930_2000 +"\"" +  ","
				+ "\""+RedisEnum.frequency_2000_2030.getEnName()+"\":" +"\"" +  frequency_2000_2030 +"\"" +  ","
				+ "\""+RedisEnum.frequency_2030_2100.getEnName()+"\":" +"\"" +  frequency_2030_2100 +"\"" +  ","
				+ "\""+RedisEnum.frequency_2100_2130.getEnName()+"\":" +"\"" +  frequency_2100_2130 +"\"" +  ","
				+ "\""+RedisEnum.frequency_2130_2200.getEnName()+"\":" +"\"" +  frequency_2130_2200 +"\"" +  ","
				+ "\""+RedisEnum.frequency_2200_2230.getEnName()+"\":" +"\"" +  frequency_2200_2230 +"\"" +  ","
				+ "\""+RedisEnum.frequency_2230_2300.getEnName()+"\":" +"\"" +  frequency_2230_2300 +"\"" +  ","
				+ "\""+RedisEnum.frequency_2300_2330.getEnName()+"\":" +"\"" +  frequency_2300_2330 +"\"" +  ","
		        + "\""+RedisEnum.frequency_2330_0000.getEnName()+"\":" +"\"" +  frequency_2330_0000 +"\"" +  "}";
		return s;
		
	}
}
