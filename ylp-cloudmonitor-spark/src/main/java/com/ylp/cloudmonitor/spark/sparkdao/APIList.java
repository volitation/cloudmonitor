package com.ylp.cloudmonitor.spark.sparkdao;

import java.util.List;

public interface APIList {
	public  String getApiName( List<String> apiList,String url);
	public String getApiName(String url);
	public List<String>getApiList();
}
