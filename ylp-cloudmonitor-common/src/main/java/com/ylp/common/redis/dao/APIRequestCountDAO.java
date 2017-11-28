package com.ylp.common.redis.dao;

import java.util.Map;

import com.ylp.common.entity.RequestCountInfo;

import redis.clients.jedis.JedisCluster;

public interface APIRequestCountDAO {
	//往redis中的hashmap中field的值增加value值
	public void add(String systemId,String apiId,String date,String field, Integer value);
	//往redis中的hashmap中field的值增加value值
	public void add(JedisCluster jedis,String systemId,String apiId,String date,String field,Integer value);  
	//获取hash中所有key-value
	public Map<String,String> getAPIRequesCount(String systemId,String apiId,String date);
	
	public RequestCountInfo getAPIRequesCountInfo(String systemId,String apiId,String date);
	
	public void setMaxFrquency(JedisCluster jedis, String systemId,String apiId,String date, String frquency) ;
	
	public void addfloat(JedisCluster jedis,String systemId,String apiId,String date,String field,Double value); 
	
	public void setMaxUsingTime(JedisCluster jedis,String systemId,String apiId,String date,Double MaxTime);
	
	public void setMinUsingTime(JedisCluster jedis,String systemId,String apiId,String date,Double MinTime);
}
