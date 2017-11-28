package com.ylp.common.es.dto;

import java.io.Serializable;

/**
 * 折线图dto类
 * 包含数据和日期熟悉
 * @author jack
 *
 */
public class LineChartDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LineChartDto(){}
	
	public LineChartDto(Object [] xaxisArray, Object [] yaxisArray){
		this.xaxisArray = xaxisArray;
		this.yaxisArray = yaxisArray;
	}
	
	// x轴数组列表
	private Object [] xaxisArray;
	// y轴数组列表
	private Object [] yaxisArray;
	
	public Object[] getXaxisArray() {
		return xaxisArray;
	}
	public void setXaxisArray(Object[] xaxisArray) {
		this.xaxisArray = xaxisArray;
	}
	public Object[] getYaxisArray() {
		return yaxisArray;
	}
	public void setYaxisArray(Object[] yaxisArray) {
		this.yaxisArray = yaxisArray;
	}

	
	

}
