package com.ylp.cloudmonitor.spark.dao;

import java.util.List;

public interface Cm_Settings_APIList {
	public  String getApiName( List<String> apiList,String url);
	public String getApiName(String url);
	public List<String>getApiList();
}
