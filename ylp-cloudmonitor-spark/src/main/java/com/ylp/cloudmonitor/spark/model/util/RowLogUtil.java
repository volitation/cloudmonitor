package com.ylp.cloudmonitor.spark.model.util;

import com.ylp.cloudmonitor.spark.model.RowLog;

public class RowLogUtil {
	public static RowLog setRowLog(String s){
		String[] data = s.split("\\|");
		RowLog log = new RowLog();
		log.setVersion(data[0]);
		log.setLanguage(data[1]);
		log.setTime(data[2]);
		log.setLevel(data[3]);
		log.setHost(data[4]);
		log.setCodeLocation(data[5]);
		log.setRequestId(data[6]);
		String detail = data[7];
		if (data.length > 8) {
			for (int i = 8; i < data.length; i++)
				detail = detail + "|"+ data[i] ;
		}
		log.setDetail(detail);
		return log;	
	}

	public static  Boolean filterRowLog(String s){
		try {
			setRowLog(s);
				return true;			
		} catch (Exception e) {
			return false;
		}
		
	}
}
