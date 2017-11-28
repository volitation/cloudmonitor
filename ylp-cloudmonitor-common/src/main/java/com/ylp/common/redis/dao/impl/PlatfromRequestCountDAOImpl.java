package com.ylp.common.redis.dao.impl;

import java.util.Map;

import com.ylp.common.entity.RequestCountInfo;
import com.ylp.common.enums.RedisEnum;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.PlatfromRequestCountDAO;

import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;

public class PlatfromRequestCountDAOImpl implements PlatfromRequestCountDAO {

	// 初始化
	private void initialization(JedisCluster jedis, String rkey) {
		if (!jedis.exists(rkey)) {
			jedis.hincrBy(rkey, RedisEnum.request_count.getEnName(), 0);
			jedis.hincrBy(rkey, RedisEnum.warn_number.getEnName(), 0);
			jedis.hincrBy(rkey, RedisEnum.error_number.getEnName(), 0);
			jedis.hincrBy(rkey, RedisEnum.total_using_time.getEnName(), 0);
		}
	}

	public static String getRedisKey(String date) {
		String key = RedisKey.Platfrom_Count;
		String rkey = String.format(key, date);
		return rkey;
	}

	public void add(String date, String field, Integer value) {
		String rkey = getRedisKey(date);
		JedisCluster jedis = JedisHelper.dataCluster();
		initialization(jedis, rkey);
		jedis.hincrBy(rkey, field, value);

	}

	public void add(JedisCluster jedis, String date, String field, Integer value) {
		String rkey = getRedisKey(date);
		initialization(jedis, rkey);
		jedis.hincrBy(rkey, field, value);

	}

	public Map<String, String> getPlatfromRequesCount(String date) {
		String rkey = getRedisKey(date);
		JedisCluster jedis = JedisHelper.dataCluster();
		Map<String, String> map = jedis.hgetAll(rkey);
		return map;
	}

	public RequestCountInfo getPlatfromRequesCountInfo(String date) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unused")
	public void setMaxFrquency(JedisCluster jedis, String date, String frquency) {
		String rkey = getRedisKey(date);
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

	public void addfloat(JedisCluster jedis, String date, String field, Double value) {
		String rkey = getRedisKey(date);
		jedis.hincrByFloat(rkey, field, value);

	}

	public void setMaxUsingTime(JedisCluster jedis, String date, Double MaxTime) {
		String rkey = getRedisKey(date);
		Double oldvalue = 0.0;
		if (!jedis.hexists(rkey, RedisEnum.max_using_time.getEnName())) {
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

	public void setMinUsingTime(JedisCluster jedis, String date, Double MinTime) {
		String rkey = getRedisKey(date);
		Double oldvalue = 999999999.0;
		try {
			oldvalue = Double.valueOf(jedis.hget(rkey, RedisEnum.min_using_time.getEnName()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (MinTime < oldvalue && MinTime >= 0) {
			jedis.hset(rkey, RedisEnum.min_using_time.getEnName(), String.valueOf(MinTime));
		}

	}

}
