package com.ylp.cloudmonitor.spark.sparkdao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Key;
import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.cloudmonitor.spark.model.util.RowLogUtil;
import com.ylp.cloudmonitor.spark.sparkdao.RequestCache;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;

import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public class NewRequestCacheImpl implements RequestCache {

	private String savesplit = "\\L";
	private String getsplit = "\\\\L";
	private Integer cachetime = 3600;
	private String key=RedisKey.REQUEST_CACHE;



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
		//原本判断日志是否完整，如果不完整就缓存到redis中
		//但这样对request开头和结尾日志在同一批次，中间部分在下一批次的日志会丢失中间部分
		//多以改成所有日志都进行缓存，对上述情况会造成重复统计，但不会丢失日志
//		if (!filterRequestCache(tuple)) {
			for (RowLog log : tuple._2) {
				if (null == logString) {

					logString = log.toString(log);

				} else {
					logString = logString + savesplit + log.toString(log);
				}
			}

			save(jedis, requestid, rkey, logString);

		}

//	}

	private void save(JedisCluster jedis, String requestid, String rkey, String logString) {
		if (jedis.hget(rkey, requestid) != null) {
			String oldvalue = jedis.hget(rkey, requestid);
			String mapvalue = oldvalue + savesplit + logString;
			Map<String, String> cMap = new HashMap<String, String>();
			cMap.put(requestid, mapvalue);
			jedis.hmset(rkey, cMap);
			jedis.expire(rkey, cachetime);

		} else {
			Map<String, String> cMap = new HashMap<String, String>();
			cMap.put(requestid, logString);
			jedis.hmset(rkey, cMap);
			jedis.expire(rkey, cachetime);
		}
	}

	public List<RowLog> getRequestCache(JedisCluster jedis, String date, String requestid) {
		String rkey = String.format(key, date);
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

}
