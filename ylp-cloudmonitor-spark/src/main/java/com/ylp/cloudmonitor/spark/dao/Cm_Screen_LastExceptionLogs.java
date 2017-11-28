package com.ylp.cloudmonitor.spark.dao;

import com.ylp.cloudmonitor.spark.model.RowLog;

import redis.clients.jedis.JedisCluster;

public interface Cm_Screen_LastExceptionLogs {
	public void saveExceptionLogToList(JedisCluster jedis,RowLog log,Integer listLimit);
}
