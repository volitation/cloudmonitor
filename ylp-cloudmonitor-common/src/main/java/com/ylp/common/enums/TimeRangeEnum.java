package com.ylp.common.enums;

/**
 * 耗时枚举类型
 * @author xiejl
 */
public enum TimeRangeEnum {
	
	/** 1秒以内 */
	Range1("1", "0", "1"),
	
	/** 1-3秒 */
	Range2("2", "1", "3"),
	
	/** 3秒以上 */
	Range3("3", "3", String.valueOf(Integer.MAX_VALUE));
	
	private String value;
	private String rangeStart;
	private String rangeEnd;
	
	private TimeRangeEnum(String value, String rangeStart, String rangeEnd) {
		this.value = value;
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}

	/**
	 * 根据value获取枚举
	 * @param value
	 * @return
	 */
	public static TimeRangeEnum getEnumByValue(String value) {
		for(TimeRangeEnum timeRangeEnum : TimeRangeEnum.values()) {
			if(timeRangeEnum.getValue().equals(value)) {
				return timeRangeEnum;
			}
		}
		return null;
	}
	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(String rangeStart) {
		this.rangeStart = rangeStart;
	}

	public String getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(String rangeEnd) {
		this.rangeEnd = rangeEnd;
	}
}
