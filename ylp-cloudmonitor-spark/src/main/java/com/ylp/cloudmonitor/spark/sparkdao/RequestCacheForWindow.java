package com.ylp.cloudmonitor.spark.sparkdao;

import java.util.List;

import com.ylp.cloudmonitor.spark.model.RowLog;

import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public interface RequestCacheForWindow {
	
	public void setRequestCache(JedisCluster jedis,Tuple2<String, Iterable<RowLog>> tuple);
	public List<RowLog> getRequestCache(JedisCluster jedis,String date,String requestid);
	public Boolean filterRequestCache(Tuple2<String, Iterable<RowLog>> tuple);

}
