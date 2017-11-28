package com.ylp.common.redis.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.ylp.common.entity.ApiInfo;
import com.ylp.common.entity.RequestCountInfo;
import com.ylp.common.enums.FieldEnum;
import com.ylp.common.enums.RedisEnum;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.SystemRequestCountDAO;
import com.ylp.common.redis.dao.factory.RedisDAOFactory;

import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;

public class SystemRequestCountDAOImpl implements SystemRequestCountDAO {
	
	//初始化
	private void initialization(JedisCluster jedis,String rkey){
		if(!jedis.exists(rkey)){
			jedis.hincrBy(rkey, RedisEnum.request_count.getEnName(), 0);
			jedis.hincrBy(rkey, RedisEnum.warn_number.getEnName(), 0);
			jedis.hincrBy(rkey, RedisEnum.error_number.getEnName(), 0);
			jedis.hincrBy(rkey, RedisEnum.total_using_time.getEnName(), 0);
		}
	}

	public static String getRedisKey(String systemId, String date) {
		String key = RedisKey.System_Count;
		String id_date = systemId + "_" + date;
		String rkey = String.format(key, id_date);
		return rkey;
	}

	public void add(String systemId, String date, String field, Integer value) {
		String rkey = getRedisKey(systemId, date);
		JedisCluster jedis = JedisHelper.dataCluster();
		initialization(jedis, rkey);
		jedis.hincrBy(rkey, field, value);

	}

	public void add(JedisCluster jedis, String systemId, String date, String field, Integer value) {
		String rkey = getRedisKey(systemId, date);
		initialization(jedis, rkey);
		jedis.hincrBy(rkey, field, value);

	}

	public Map<String, String> getSystemRequestCount(String systemId, String date) {
		JedisCluster jedis = JedisHelper.dataCluster();
		String rkey = getRedisKey(systemId, date);
		Map<String, String> map = jedis.hgetAll(rkey);
		return map;
	}

	public RequestCountInfo getSystemRequesCountInfo(String systemId, String date) {
		// TODO Auto-generated method stub
		return null;
	}


	public void setMaxFrquency(JedisCluster jedis, String systemId, String date, String frquency) {
		String rkey = getRedisKey(systemId, date);
		Integer frqValue = Integer.valueOf(jedis.hget(rkey, frquency));
		String maxf = jedis.hget(rkey, RedisEnum.max_frequency.getEnName());
		if (null == maxf) {
			String s = "{" + "\"" + RedisEnum.max_frequency_time.getEnName() + "\":" + "\"" + frquency + "\"" + ","
					+ "\"" + RedisEnum.max_frequency_value.getEnName() + "\":" + "\"" + frqValue + "\"" + "}";
			jedis.hset(rkey, RedisEnum.max_frequency.getEnName(), s);
		} else {
			JSONObject object = JSONObject.fromObject(maxf);
			Integer maxValue = object.getInt(RedisEnum.max_frequency_value.getEnName());
			if (maxValue < frqValue) {
				String s = "{" + "\"" + RedisEnum.max_frequency_time.getEnName() + "\":" + "\"" + frquency + "\"" + ","
						+ "\"" + RedisEnum.max_frequency_value.getEnName() + "\":" + "\"" + frqValue + "\"" + "}";
				jedis.hset(rkey, RedisEnum.max_frequency.getEnName(), s);
			}
		}
	}

	public void addfloat(JedisCluster jedis, String systemId, String date, String field, Double value) {
		String rkey = getRedisKey(systemId, date);
		jedis.hincrByFloat(rkey, field, value);

	}

	public void setMaxUsingTime(JedisCluster jedis, String systemId, String date, Double MaxTime) {
		String rkey = getRedisKey(systemId, date);
		Double oldvalue = 0.0;
		if(!jedis.hexists(rkey, RedisEnum.max_using_time.getEnName())){
			jedis.hset(rkey, RedisEnum.max_using_time.getEnName(), String.valueOf(oldvalue));
		}
		try {
			oldvalue = Double.valueOf(jedis.hget(rkey, RedisEnum.max_using_time.getEnName()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (MaxTime > oldvalue) {
			jedis.hset(rkey, RedisEnum.max_using_time.getEnName(), String.valueOf(MaxTime));
		}

	}

	public void setMinUsingTime(JedisCluster jedis, String systemId, String date, Double MinTime) {
		String rkey = getRedisKey(systemId, date);
		Double oldvalue = 999999999.0;
		try {
			oldvalue = Double.valueOf(jedis.hget(rkey, RedisEnum.min_using_time.getEnName()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (MinTime < oldvalue&&MinTime>=0) {
			jedis.hset(rkey, RedisEnum.min_using_time.getEnName(), String.valueOf(MinTime));
		}

	}

	public List<Map<String, String>> getApiCountList(String systemId, String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<ApiInfo> apiList = RedisDAOFactory.getAPIListDAO().getApiList();
		for (ApiInfo apiInfo : apiList) {
			Map<String, String> apiMap = new HashMap<String, String>();
			String apiId = String.valueOf(apiInfo.getId());
			apiMap = RedisDAOFactory.getAPIRequestCountDAO().getAPIRequesCount(systemId, apiId, date);
			if (null != apiMap && apiMap.size() != 0) {
				apiMap.put(FieldEnum.interfaceName.getEnName(), apiInfo.getCnName());
				apiMap.put(FieldEnum.apiId.getEnName(), apiId);
				apiMap.put(FieldEnum.url.getEnName(), apiInfo.getUrl());
				list.add(apiMap);
			}
		}

		return sort(list);
	}

	private List<Map<String, String>> sort(List<Map<String, String>> list) {
		Collections.sort(list, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> m1, Map<String, String> m2) {
				Integer i1=Integer.valueOf(m1.get(RedisEnum.request_count.getEnName()));
				Integer i2=Integer.valueOf(m2.get(RedisEnum.request_count.getEnName()));
				try {
					if (i1>i2) {
						return -1;
					} else if (i1<i2) {
						return 1;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				return 0;
			}
		});
		return list;

	}
}
