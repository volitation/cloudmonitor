package com.ylp.cloudmonitor.spark.util;

import java.io.Serializable;

import net.sf.json.JSONArray;

public class URLUtil implements Serializable {

	public static String getSysName(JSONArray array, String url) {
		String cn = null;
		String cl = null;
		try {
			cl = url.split("/")[2];
			for (int i = 0; i < array.size(); i++) {
				String sysUrl = String.valueOf(array.getJSONObject(i).get("url"));
				if (cl.equals(sysUrl)) {
					cn = String.valueOf(array.getJSONObject(i).get("cnName"));
					return cn;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return cn;
	}
	
	
	public static String getApiName(JSONArray array, String url){
		String apiName = null;
		String an =null;
		try {
			if (url.contains("?")) {
				an = url.split("\\?")[0].replace("http://" + url.split("/")[2] + "/", "");
			} else {
				an = url.replace("http://" + url.split("/")[2] + "/", "");
			}
			for (int i = 0; i < array.size(); i++) {
				String sysUrl = String.valueOf(array.getJSONObject(i).get("url"));
				if (an.equals(sysUrl)) {
					apiName = String.valueOf(array.getJSONObject(i).get("cnName"));
					return apiName;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return apiName;
	}
}
