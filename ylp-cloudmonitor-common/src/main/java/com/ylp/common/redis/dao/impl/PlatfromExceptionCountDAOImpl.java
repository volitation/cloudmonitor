package com.ylp.common.redis.dao.impl;

import java.util.Map;

import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.PlatfromExceptionCountDAO;

import redis.clients.jedis.JedisCluster;

public class PlatfromExceptionCountDAOImpl implements PlatfromExceptionCountDAO{
	
	public static String getRedisKey(String date){
		String systemCountKey =RedisKey.Platfrom_Exception_Count;
		String rkey = String.format(systemCountKey, date);
		return rkey;	
	}

	public void add(String date, String field, Integer value) {
		JedisCluster jedis = JedisHelper.dataCluster();
		String rkey =getRedisKey(date);
		jedis.hincrBy(rkey, field, value);
		
	}

	public void add(JedisCluster jedis, String date, String field, Integer value) {
		String rkey =getRedisKey(date);
		jedis.hincrBy(rkey, field, value);
		
	}

	public Map<String, String> getExceptionCount(String date) {
		JedisCluster jedis = JedisHelper.dataCluster();
		String rkey =getRedisKey(date);
		Map<String, String> map = jedis.hgetAll(rkey);
		return map;
	}

}
