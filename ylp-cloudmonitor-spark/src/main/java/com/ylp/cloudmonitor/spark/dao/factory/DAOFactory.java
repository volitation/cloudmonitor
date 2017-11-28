package com.ylp.cloudmonitor.spark.dao.factory;

import com.ylp.cloudmonitor.spark.dao.Cm_Screen_LastExceptionLogs;
import com.ylp.cloudmonitor.spark.dao.Cm_Screen_SystemExceptionCount;
import com.ylp.cloudmonitor.spark.dao.Cm_Settings_APIList;
import com.ylp.cloudmonitor.spark.dao.Cm_Settings_SysList;
import com.ylp.cloudmonitor.spark.dao.impl.CmSettingAPIListImpl;
import com.ylp.cloudmonitor.spark.dao.impl.CmSettingSysListImpl;
import com.ylp.cloudmonitor.spark.dao.impl.LastExceptionLogsImpl;
import com.ylp.cloudmonitor.spark.dao.impl.SystemCountImpl;

public class DAOFactory {
	public static Cm_Settings_SysList getSysList() {
		return new CmSettingSysListImpl();
	}
	
	public static Cm_Settings_APIList getAPIList() {
		return new CmSettingAPIListImpl();
	}
	
	public static Cm_Screen_SystemExceptionCount getSystemExceptionCount() {
		return new SystemCountImpl();
	}
	
	public static Cm_Screen_LastExceptionLogs getLastExceptionLogs() {
		return new LastExceptionLogsImpl();
	}
}
