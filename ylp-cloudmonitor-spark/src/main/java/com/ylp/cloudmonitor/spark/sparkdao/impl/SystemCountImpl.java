package com.ylp.cloudmonitor.spark.sparkdao.impl;

import java.util.HashMap;
import java.util.Map;

import com.ylp.cloudmonitor.spark.sparkdao.Cm_Screen_SystemExceptionCount;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;

import redis.clients.jedis.JedisCluster;
import tachyon.thrift.BlockMasterService.AsyncProcessor.getBlockInfo;

public class SystemCountImpl implements Cm_Screen_SystemExceptionCount {
	
	public static String getSystemCountKey(String date){
		String systemCountKey =RedisKey.REDIS_COUNT;
		String rkey = String.format(systemCountKey, date);
		return rkey;	
	}
	
	public static JedisCluster getRedisCluster(){
		JedisCluster jedis = JedisHelper.dataCluster();
		return jedis;
	}

	public void hmadd(String date,String mapkey, Integer mapvalue,Integer effectivetime) {
		JedisCluster jedis=getRedisCluster();
		String rediskey =getSystemCountKey(date);
		if (jedis.hget(rediskey, mapkey) != null) {
			Integer oldvalue = Integer.valueOf(jedis.hget(rediskey, mapkey));
			mapvalue = mapvalue + oldvalue;
			String c = String.valueOf(mapvalue);
			Map<String, String> datecMap = new HashMap<String, String>();
			datecMap.put(mapkey, c);
			jedis.hmset(rediskey, datecMap);
			if(effectivetime!=null){
				jedis.expire(rediskey, effectivetime);
			}
		} else {
			Map<String, String> cMap = new HashMap<String, String>();
			cMap.put(mapkey, String.valueOf(mapvalue));
			jedis.hmset(rediskey, cMap);
			if(effectivetime!=null){
				jedis.expire(rediskey, effectivetime);
			}
		}

	}

	public void hmadd(JedisCluster jedis, String date,String mapkey, Integer mapvalue,Integer effectivetime) {
		String rediskey =getSystemCountKey(date);
		if (jedis.hget(rediskey, mapkey) != null) {
			Integer oldvalue = Integer.valueOf(jedis.hget(rediskey, mapkey));
			mapvalue = mapvalue + oldvalue;
			String c = String.valueOf(mapvalue);
			Map<String, String> datecMap = new HashMap<String, String>();
			datecMap.put(mapkey, c);
			jedis.hmset(rediskey, datecMap);
			if(effectivetime!=null){
				jedis.expire(rediskey, effectivetime);
			}
		} else {
			Map<String, String> cMap = new HashMap<String, String>();
			cMap.put(mapkey, String.valueOf(mapvalue));
			jedis.hmset(rediskey, cMap);
			if(effectivetime!=null){
				jedis.expire(rediskey, effectivetime);
			}
		}
		
	}

}
