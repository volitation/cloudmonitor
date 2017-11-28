package com.ylp.cloudmonitor.spark.sparkdao.impl;

import java.util.List;

import com.ylp.cloudmonitor.spark.sparkdao.Cm_Settings_APIList;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;

import net.sf.json.JSONArray;
import redis.clients.jedis.JedisCluster;

public class CmSettingAPIListImpl implements Cm_Settings_APIList {

	public List<String>getApiList(){
		// 拉取系统名称映射表
		JedisCluster jedis = JedisHelper.dataCluster();
		List<String> APIList = jedis.lrange(RedisKey.API_LIST, 0, -1);
		return APIList;
		
	}
	public String getApiName(List<String> apiList,String url) {
		JSONArray array = JSONArray.fromObject(apiList);
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
	public String getApiName(String url) {
		List<String > apiList=getApiList();
		JSONArray array = JSONArray.fromObject(apiList);
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
