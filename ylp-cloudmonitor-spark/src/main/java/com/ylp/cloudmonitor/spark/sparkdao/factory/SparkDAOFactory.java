package com.ylp.cloudmonitor.spark.sparkdao.factory;

import com.ylp.cloudmonitor.spark.sparkdao.APIList;
import com.ylp.cloudmonitor.spark.sparkdao.LastExceptionLogs;
import com.ylp.cloudmonitor.spark.sparkdao.RequestCache;
import com.ylp.cloudmonitor.spark.sparkdao.RequestCacheForWindow;
import com.ylp.cloudmonitor.spark.sparkdao.RequestDetail;
import com.ylp.cloudmonitor.spark.sparkdao.SysList;
import com.ylp.cloudmonitor.spark.sparkdao.SystemExceptionCount;
import com.ylp.cloudmonitor.spark.sparkdao.impl.APIListImpl;
import com.ylp.cloudmonitor.spark.sparkdao.impl.LastExceptionLogsImpl;
import com.ylp.cloudmonitor.spark.sparkdao.impl.NewRequestCacheImpl;
import com.ylp.cloudmonitor.spark.sparkdao.impl.NewRequestCacheImpl2;
import com.ylp.cloudmonitor.spark.sparkdao.impl.RequestCacheForWindowImpl;
import com.ylp.cloudmonitor.spark.sparkdao.impl.RequestCacheImpl;
import com.ylp.cloudmonitor.spark.sparkdao.impl.RequestDetailImpl;
import com.ylp.cloudmonitor.spark.sparkdao.impl.SysListImpl;
import com.ylp.cloudmonitor.spark.sparkdao.impl.SystemExceptionCountImpl;

public class SparkDAOFactory {
	public static SysList getSysList() {
		return new SysListImpl();
	}
	
	public static APIList getAPIList() {
		return new APIListImpl();
	}
	
	public static SystemExceptionCount getSystemExceptionCount() {
		return new SystemExceptionCountImpl();
	}
	
	public static LastExceptionLogs getLastExceptionLogs() {
		return new LastExceptionLogsImpl();
	}
	
	public static RequestDetail getRequestDetail() {
		return new RequestDetailImpl();
	}
	public static RequestCache getRequestCache() {
		return new NewRequestCacheImpl2();
	}
	
	public static RequestCacheForWindow getRequestCacheForWindow() {
		return new RequestCacheForWindowImpl();
	}
}
