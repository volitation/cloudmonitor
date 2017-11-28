package com.ylp.common.redis.dao;

import java.util.List;
import java.util.Map;

import com.ylp.common.entity.RequestCountInfo;

import redis.clients.jedis.JedisCluster;

public interface SystemRequestCountDAO {
	//往redis中的hashmap中field的值增加value值
	public void add(String systemId,String date,String field, Integer value);
	//往redis中的hashmap中field的值增加value值
	public void add(JedisCluster jedis,String systemId,String date,String field,Integer value);  
	//获取hash中所有key-value
	public Map<String,String> getSystemRequestCount(String systemId,String date);
	

   public List<Map<String,String>> getApiCountList(String systemId,String date);
	
	public RequestCountInfo getSystemRequesCountInfo(String systemId,String date);
	
	public void setMaxFrquency(JedisCluster jedis, String systemId,String date, String frquency);
	
	public void addfloat(JedisCluster jedis,String systemId,String date,String field,Double value);  
	
	public void setMaxUsingTime(JedisCluster jedis,String systemId,String date,Double MaxTime);
	
	public void setMinUsingTime(JedisCluster jedis,String systemId,String date,Double MinTime);
}
