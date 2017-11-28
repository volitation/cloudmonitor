package com.ylp.common.redis.dao;

import java.util.List;



import com.ylp.common.entity.ExceptionLogInfo;

import redis.clients.jedis.JedisCluster;

public interface LastExceptionLogsDAO {
//	public void saveExceptionLogToList(JedisCluster jedis,RequestLogs rgroup,Integer listLimit);
	public void saveExceptionLogToList(JedisCluster jedis,ExceptionLogInfo info,Integer listLimit);
	public List<ExceptionLogInfo> getExceptionLogList();
}
