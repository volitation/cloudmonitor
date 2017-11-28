package com.ylp.cloudmonitor.spark.dao;

import java.util.List;



public interface Cm_Settings_SysList {
	public String getSysName(String url);
	public String getSysName(List<String> list ,String url);
	public List<String>getSysList();
}
