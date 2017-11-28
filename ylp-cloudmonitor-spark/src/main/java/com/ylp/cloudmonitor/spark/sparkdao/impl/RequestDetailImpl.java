package com.ylp.cloudmonitor.spark.sparkdao.impl;

import java.util.HashMap;
import java.util.Map;

import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.cloudmonitor.spark.sparkdao.RequestDetail;
import com.ylp.cloudmonitor.spark.sparkdao.factory.SparkDAOFactory;
import com.ylp.common.enums.FieldEnum;
import com.ylp.common.es.constant.RedisKey;

import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public class RequestDetailImpl implements RequestDetail{
	private Integer effectivetime =86400;
	private String getrequestDetailKey(){
		String key =RedisKey.REQUEST_DETAIL;
		return key;
		
	}

	public void saveRequestDetail(JedisCluster jedis,Tuple2<String, Iterable<RowLog>> tuple) {
		String s = tuple._1;
		String[] data = s.split("\\|");
		String requestId = data[0];
		String url = data[1];
		String startTime = data[2];
		String stopTime = data[3];
		String date = data[4];
		String rkey = String.format(getrequestDetailKey(), date);
		Map<String, String> cMap = new HashMap<String, String>();
		if (startTime.length() > 5) {
			cMap.put(requestId + "_" + FieldEnum.startTime.getEnName(), startTime);
		}
		if (stopTime.length() > 5) {
			cMap.put(requestId + "_" + FieldEnum.endTime.getEnName(), stopTime);
		}
		if (url.length() > 5) {
			cMap.put(requestId + "_" + FieldEnum.url.getEnName(), url);
			//String cl = URLUtil.getSysName(sysNameArray, url);
			String cl =SparkDAOFactory.getSysList().getSysName(url);
			if (null == cl) {
				cl = url.split("/")[2];
			}
			cMap.put(requestId + "_" + FieldEnum.callLocation.getEnName(), cl);
			// 从url中获取接口名称api_name
			//String an = URLUtil.getApiName(apiNameArray, url);
			String an =SparkDAOFactory.getAPIList().getApiName(url);
			if (null == an) {
				if (url.contains("?")) {
					an = url.split("\\?")[0].replace("http://" + url.split("/")[2] + "/", "");
				} else {
					an = url.replace("http://" + url.split("/")[2] + "/", "");
				}
			}
			cMap.put(requestId + "_" + FieldEnum.interfaceName.getEnName(), an);
		}
		jedis.hmset(rkey, cMap);
		jedis.expire(rkey, effectivetime);
	}

	

	public String getSystemName(JedisCluster jedis,String requestId, String date) {
		String rkey = String.format(getrequestDetailKey(), date);
		String cl = jedis.hget(rkey, requestId + "_" + FieldEnum.callLocation.getEnName());
		return cl ;
	}

	public String getApiName(JedisCluster jedis, String requestId, String date) {
		String rkey = String.format(getrequestDetailKey(), date);
		String an = jedis.hget(rkey, requestId + "_" + FieldEnum.interfaceName.getEnName());
		return an;
	}

	public String getUrl(JedisCluster jedis, String requestId, String date) {
		String rkey = String.format(getrequestDetailKey(), date);
		String url = jedis.hget(rkey, requestId + "_" + FieldEnum.url.getEnName());
		return url;
	}

	public String getStartTime(JedisCluster jedis, String requestId, String date) {
		String rkey = String.format(getrequestDetailKey(), date);
		String st = jedis.hget(rkey, requestId + "_" + FieldEnum.startTime.getEnName());
		return st;
	}

	public String getStopTime(JedisCluster jedis, String requestId, String date) {
		String rkey = String.format(getrequestDetailKey(), date);
		String et = jedis.hget(rkey, requestId + "_" + FieldEnum.endTime.getEnName());
		return et;
	}

}
