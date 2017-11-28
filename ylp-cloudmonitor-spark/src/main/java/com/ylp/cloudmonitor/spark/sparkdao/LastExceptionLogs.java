package com.ylp.cloudmonitor.spark.sparkdao;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.cloudmonitor.spark.model.RowLog;

import redis.clients.jedis.JedisCluster;

public interface LastExceptionLogs {
	public void saveExceptionLogToList(JedisCluster jedis,RowLog log,Integer listLimit);
	public void saveExceptionLogToList(JedisCluster jedis,RequestLogs rgroup,Integer listLimit);
}
