package com.ylp.common.redis.dao;

import java.util.Map;

import com.ylp.common.entity.RequestCountInfo;

import redis.clients.jedis.JedisCluster;

public interface PlatfromRequestCountDAO {
	//往redis中的hashmap中field的值增加value值
	public void add(String date,String field, Integer value);
	//往redis中的hashmap中field的值增加value值
	public void add(JedisCluster jedis,String date,String field,Integer value);  
	
	//获取hash中所有key-value
	public Map<String,String> getPlatfromRequesCount(String date);
	
	public RequestCountInfo getPlatfromRequesCountInfo(String date);
	
	//往redis中写入最大调用频次
	public void setMaxFrquency(JedisCluster jedis,String date,String frquency);
	
	public void addfloat(JedisCluster jedis,String date,String field,Double value);  
	
	public void setMaxUsingTime(JedisCluster jedis,String date,Double MaxTime);
	
	public void setMinUsingTime(JedisCluster jedis,String date,Double MinTime);
	
}
