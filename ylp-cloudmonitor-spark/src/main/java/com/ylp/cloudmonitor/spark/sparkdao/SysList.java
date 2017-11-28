package com.ylp.cloudmonitor.spark.sparkdao;

import java.util.List;



public interface SysList {
	public String getSysName(String url);
	public String getSysName(List<String> list ,String url);
	public List<String>getSysList();
}
