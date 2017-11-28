package com.ylp.cloudmonitor.spark.sparkdao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Key;
import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.cloudmonitor.spark.model.util.RowLogUtil;
import com.ylp.cloudmonitor.spark.sparkdao.RequestCache;
import com.ylp.cloudmonitor.spark.sparkdao.RequestCacheForWindow;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;

import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public class RequestCacheForWindowImpl implements RequestCacheForWindow {

	private String savesplit = "\\L";
	private String getsplit = "\\\\L";
	private Integer cachetime = 3600;
	private String key=RedisKey.REQUEST_CACHE_Window;


	public Boolean filterRequest(List<RowLog> list) {
		Boolean result = false;
		Boolean start = false;
		Boolean ent = false;
		for (RowLog log : list) {
			String detail = log.getDetail();
			String[] detaildata = detail.split(":");
			if (detaildata[0].equals("request started")) {
				if (detaildata[1].equals("http")) {
					start = true;
				}
				
			}
			if (detaildata[0].equals("request completed")) {
				ent = true;
			}
		}
		result = start && ent;
		return result;

	}
	
	
	public Boolean filterRequestCache(Tuple2<String, Iterable<RowLog>> tuple) {
		JedisCluster jedis = JedisHelper.dataCluster();
		Boolean result = false;
		String requestid = tuple._2.iterator().next().getRequestId();
		String date = tuple._2.iterator().next().getTime().split(" ")[0].replace("-", "");
		String rkey = String.format(key, date);
		List<RowLog> loglist=getRequestCache(jedis, date, requestid);
		for (RowLog log : tuple._2) {
     
			loglist.add(log);
		}
		
		return filterRequest(loglist);

	}


	public void setRequestCache(JedisCluster jedis, Tuple2<String, Iterable<RowLog>> tuple) {
		String requestid = tuple._2.iterator().next().getRequestId();
		String date = tuple._2.iterator().next().getTime().split(" ")[0].replace("-", "");
		String rkey = String.format(key, date);
		String logString = null;
		List<String> list = new ArrayList<String>();
		if (!filterRequestCache(tuple)) {
			for (RowLog log : tuple._2) {
				list.add(log.toString(log));
			}

			save(jedis, requestid, rkey, list);

		}

	}

	private void save(JedisCluster jedis, String requestid, String rkey, List<String>list) {
		//滑动窗口会有数据重复现象，需要进行去重
		list.addAll(getStringList(jedis, rkey, requestid));
        Set set = new  HashSet(); 
        List<String> newList = new  ArrayList<String>(); 
        set.addAll(list);
        newList.addAll(set);
        String logString =null;
		for(String s:newList){
			if (null == logString) {

				logString = s;

			} else {
				logString = logString + savesplit + s;
			}

		}

			Map<String, String> cMap = new HashMap<String, String>();
			cMap.put(requestid, logString);
			jedis.hmset(rkey, cMap);
			jedis.expire(rkey, cachetime);

	}

	public List<RowLog> getRequestCache(JedisCluster jedis, String rkey, String requestid) {
		List<RowLog> list = new ArrayList<RowLog>();
		if (jedis.hget(rkey, requestid) != null) {
			String logString = jedis.hget(rkey, requestid);
			String[] logdata = logString.split(getsplit);
			for (String s : logdata) {
				RowLog log = RowLogUtil.setRowLog(s);
				list.add(log);
			}
		}
		return list;
	}
	
	public List<String> getStringList(JedisCluster jedis, String date, String requestid) {
		String rkey = String.format(key, date);
		List<String> list = new ArrayList<String>();
		if (jedis.hget(rkey, requestid) != null) {
			String logString = jedis.hget(rkey, requestid);
			String[] logdata = logString.split(getsplit);
			for (String s : logdata) {
				list.add(s);
			}
		}
		return list;
	}

}
