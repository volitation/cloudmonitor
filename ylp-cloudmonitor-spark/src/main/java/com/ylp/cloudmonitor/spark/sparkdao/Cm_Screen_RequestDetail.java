package com.ylp.cloudmonitor.spark.sparkdao;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.cloudmonitor.spark.model.RowLog;

import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public interface Cm_Screen_RequestDetail {

	public void saveRequestDetail(JedisCluster jedis,Tuple2<String, Iterable<RowLog>> tuple);

	public String getSystemName(JedisCluster jedis,String requestId,String date);
	
	public String getApiName(JedisCluster jedis,String requestId,String date);
	
	public String getUrl(JedisCluster jedis,String requestId,String date);
	
	public String getStartTime(JedisCluster jedis,String requestId,String date);
	
	public String getStopTime(JedisCluster jedis,String requestId,String date);
}
