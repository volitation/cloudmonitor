package com.ylp.common.redis.dao.impl;

import java.util.Map;

import com.ylp.common.entity.RequestCountInfo;
import com.ylp.common.enums.RedisEnum;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.APIRequestCountDAO;

import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;

public class APIRequestCountDAOImpl implements APIRequestCountDAO {
	
	//初始化
	private void initialization(JedisCluster jedis ,String rkey){
		if(!jedis.exists(rkey)){
			jedis.hincrBy(rkey, RedisEnum.request_count.getEnName(), 0);
			jedis.hincrBy(rkey, RedisEnum.warn_number.getEnName(), 0);
			jedis.hincrBy(rkey, RedisEnum.error_number.getEnName(), 0);
			jedis.hincrBy(rkey, RedisEnum.total_using_time.getEnName(), 0);
		}
	}
	
	public static String getRedisKey(String systemId,String apiId,String date){
		String key =RedisKey.Interface_Count;
		String id_date=systemId+"_"+apiId+"_"+date;
		String rkey = String.format(key, id_date);
		return rkey;	
	}

	public void add(String systemId, String apiId, String date, String field, Integer value) {
		String rkey =getRedisKey(systemId,apiId,date);
		JedisCluster jedis = JedisHelper.dataCluster();
		initialization(jedis, rkey);
		jedis.hincrBy(rkey, field, value);

	}

	public void add(JedisCluster jedis, String systemId, String apiId, String date, String field, Integer value) {
		String rkey =getRedisKey(systemId,apiId,date);
		initialization(jedis, rkey);
		jedis.hincrBy(rkey, field, value);

	}

	public Map<String, String> getAPIRequesCount(String systemId, String apiId, String date) {
		JedisCluster jedis = JedisHelper.dataCluster();
		String rkey =getRedisKey(systemId,apiId,date);
		Map<String, String> map = jedis.hgetAll(rkey);
		return map;
	}

	public RequestCountInfo getAPIRequesCountInfo(String systemId, String apiId, String date) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unused")
	public void setMaxFrquency(JedisCluster jedis, String systemId, String apiId, String date, String frquency) {
		String rkey =getRedisKey(systemId,apiId,date);
		Integer frqValue=Integer.valueOf(jedis.hget(rkey, frquency));
		Integer maxValue=0;
		String maxf=jedis.hget(rkey, RedisEnum.max_frequency.getEnName());
		try {
			JSONObject object =JSONObject.fromObject(maxf);
			 maxValue =object.getInt(RedisEnum.max_frequency_value.getEnName());
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(null==maxValue){
			String s="{" + "\""+RedisEnum.max_frequency_time.getEnName()+"\":" + "\"" +frquency + "\"" + "," 
					+ "\""+RedisEnum.max_frequency_value.getEnName()+"\":" +"\"" + frqValue +"\"" +  "}";
			jedis.hset(rkey, RedisEnum.max_frequency.getEnName(), s);
		} else if (maxValue<frqValue){
			String s="{" + "\""+RedisEnum.max_frequency_time.getEnName()+"\":" + "\"" +frquency + "\"" + "," 
					+ "\""+RedisEnum.max_frequency_value.getEnName()+"\":" +"\"" + frqValue +"\"" +  "}";
			jedis.hset(rkey, RedisEnum.max_frequency.getEnName(), s);
		}
		
	}

	public void addfloat(JedisCluster jedis, String systemId, String apiId, String date, String field, Double value) {
		String rkey =getRedisKey(systemId,apiId,date);
		jedis.hincrByFloat(rkey, field, value);
		
	}

	public void setMaxUsingTime(JedisCluster jedis, String systemId, String apiId, String date, Double MaxTime) {
		String rkey =getRedisKey(systemId,apiId,date);
		Double oldvalue =0.0;
		if(!jedis.hexists(rkey, RedisEnum.max_using_time.getEnName())){
			jedis.hset(rkey, RedisEnum.max_using_time.getEnName(), String.valueOf(oldvalue));
		}
		try {
			oldvalue =Double.valueOf(jedis.hget(rkey,RedisEnum.max_using_time.getEnName()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(MaxTime>oldvalue){
			jedis.hset(rkey, RedisEnum.max_using_time.getEnName(), String.valueOf(MaxTime));
		}
		
	}

	public void setMinUsingTime(JedisCluster jedis, String systemId, String apiId, String date, Double MinTime) {
		String rkey =getRedisKey(systemId,apiId,date);
		Double oldvalue =999999.0;
		try {
			oldvalue =Double.valueOf(jedis.hget(rkey,RedisEnum.min_using_time.getEnName()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(MinTime<oldvalue&&MinTime>=0){
			jedis.hset(rkey, RedisEnum.min_using_time.getEnName(), String.valueOf(MinTime));
		}

	}

}
