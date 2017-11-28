package com.ylp.cloudmonitor.spark.sparkdao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.rowset.internal.Row;
import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.cloudmonitor.spark.model.util.RowLogUtil;
import com.ylp.cloudmonitor.spark.sparkdao.RequestCache;
import com.ylp.cloudmonitor.spark.sparkdao.factory.SparkDAOFactory;
import com.ylp.common.es.constant.RedisKey;

import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public class RequestCacheImpl implements RequestCache {
	private String savesplit="\\L";
	private String getsplit="\\\\L";
	private String getRequestCachekey() {
		String key = RedisKey.REQUEST_CACHE;
		return key;
	}
	
	public Boolean filterRequestCache(Tuple2<String, Iterable<RowLog>> tuple) {
		Boolean result = false;
		for (RowLog log : tuple._2) {
			String detail = log.getDetail();
			String[] detaildata = detail.split(":");
			if (detaildata[0].equals("request completed")) {
				result = true;
			}
		}
		return result;

	}

	public void setRequestCache(JedisCluster jedis, Tuple2<String, Iterable<RowLog>> tuple) {
		if (!filterRequestCache(tuple)) {
			String logString = null;
			for (RowLog log : tuple._2) {
				if (null == logString) {

					logString = log.toString(log);

				} else {
					logString = logString + savesplit + log.toString(log);
				}
			}
			String requestid = tuple._2.iterator().next().getRequestId();
			String date = tuple._2.iterator().next().getTime().split(" ")[0].replace("-", "");
			String rkey = String.format(getRequestCachekey(), date);

			if (jedis.hget(rkey, requestid) != null) {
				String oldvalue = jedis.hget(rkey, requestid);
				String mapvalue = oldvalue + savesplit + logString;
				Map<String, String> cMap = new HashMap<String, String>();
				cMap.put(requestid, mapvalue);
				jedis.hmset(rkey, cMap);
				jedis.expire(rkey, 3600);

			} else {
				Map<String, String> cMap = new HashMap<String, String>();
				cMap.put(requestid, logString);
				jedis.hmset(rkey, cMap);
				jedis.expire(rkey, 3600);
			}
		
		}

	}

	public List<RowLog> getRequestCache(JedisCluster jedis, String date, String requestid) {
		String rkey = String.format(getRequestCachekey(), date);
		List<RowLog> list = new ArrayList<RowLog>();
		if(jedis.hget(rkey, requestid)!=null){
			String logString = jedis.hget(rkey, requestid);
			String[] logdata = logString.split(getsplit);
			for (String s : logdata) {
				RowLog log = RowLogUtil.setRowLog(s);
				list.add(log);
			}
		}
		return list;
	}

	public Boolean filterRequest(Tuple2<String, Iterable<RowLog>> tuple) {
		// TODO Auto-generated method stub
		return null;
	}

}
