package com.ylp.cloudmonitor.spark.sparkdao.impl;

import java.util.List;

import com.ylp.cloudmonitor.spark.sparkdao.Cm_Settings_SysList;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;

import net.sf.json.JSONArray;
import redis.clients.jedis.JedisCluster;

public class CmSettingSysListImpl implements Cm_Settings_SysList{
	
	public List<String>getSysList(){
		// 拉取系统名称映射表
		JedisCluster jedis = JedisHelper.dataCluster();
		List<String> sysNameList = jedis.lrange(RedisKey.SYSTEM_LIST, 0, -1);
		return sysNameList;
		
	}

	public String getSysName(String url) {
		List<String> sysList=getSysList();
		JSONArray array = JSONArray.fromObject(sysList);
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
	public String getSysName(List<String> sysList,String url) {
		JSONArray array = JSONArray.fromObject(sysList);
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
}
