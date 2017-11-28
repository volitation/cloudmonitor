package com.ylp.common.redis.dao;

import java.util.Map;

import redis.clients.jedis.JedisCluster;

public interface PlatfromExceptionCountDAO {
	//往redis中的hashmap中field的值增加value值
	public void add(String date,String field, Integer value);
	//往redis中的hashmap中field的值增加value值
	public void add(JedisCluster jedis,String date,String field,Integer value);  
	//获取hash中所有key-value
	public Map<String,String> getExceptionCount(String date);
	
}
