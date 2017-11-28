package com.ylp.cloudmonitor.spark.util;

import java.util.HashMap;
import java.util.Map;

import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.common.enums.FieldEnum;
import com.ylp.common.enums.LogEnum;

import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;

public class RedisUtil {

	public void hmIadd(JedisCluster jedis, String rediskey, String mapkey, Integer mapvalue, Integer effectivetime) {
		if (jedis.hget(rediskey, mapkey) != null) {
			Integer oldvalue = Integer.valueOf(jedis.hget(rediskey, mapkey));
			mapvalue = mapvalue + oldvalue;
			String c = String.valueOf(mapvalue);
			Map<String, String> datecMap = new HashMap<String, String>();
			datecMap.put(mapkey, c);
			jedis.hmset(rediskey, datecMap);
			if (effectivetime != null) {
				jedis.expire(rediskey, effectivetime);
			}
		} else {
			Map<String, String> cMap = new HashMap<String, String>();
			cMap.put(mapkey, String.valueOf(mapvalue));
			jedis.hmset(rediskey, cMap);
			if (effectivetime != null) {
				jedis.expire(rediskey, effectivetime);
			}
		}
	}

	public void hmSadd(JedisCluster jedis, String rediskey, String mapkey, String mapvalue, Integer effectivetime) {
		if (jedis.hget(rediskey, mapkey) != null) {
			String oldvalue = jedis.hget(rediskey, mapkey);
			mapvalue = oldvalue + "\\n" + mapvalue;
			Map<String, String> datecMap = new HashMap<String, String>();
			datecMap.put(mapkey, mapvalue);
			jedis.hmset(rediskey, datecMap);
			if (effectivetime != null) {
				jedis.expire(rediskey, effectivetime);
			}

		} else {
			Map<String, String> cMap = new HashMap<String, String>();
			cMap.put(mapkey, String.valueOf(mapvalue));
			jedis.hmset(rediskey, cMap);
			if (effectivetime != null) {
				jedis.expire(rediskey, effectivetime);
			}
		}
	}

	public void saveExceptionLogToList(JedisCluster jedis, RowLog log, String redisDetailKey, String listKey,
			Integer listLimit) {
		// 将error和warn级别的日志详情储存到redis的list中
		if (!log.getRequestId().equals(" ") && !log.getRequestId().equals("nullvalue")) {
			if (log.getLevel().equals(LogEnum.error.getEnName()) || log.getLevel().equals(LogEnum.warn.getEnName())) {
				String date = log.getTime().split(" ")[0].replace("-", "");
				String rkey = String.format(redisDetailKey, date);
				String key = log.getRequestId() + "_" + log.getLevel().replace(" ", "");
				RedisUtil redisUtil = new RedisUtil();
				redisUtil.hmSadd(jedis, rkey, key, log.toString(log), null);
				// 将error和warn级别的日志的相关信息储存到一个list中
				String requestID = log.getRequestId();
				String time = log.getTime();
				String cl = jedis.hget(rkey, requestID + "_" + FieldEnum.callLocation.getEnName());
				String an = jedis.hget(rkey, requestID + "_" + FieldEnum.interfaceName.getEnName());
				String le = log.getLevel();
				String ld = log.toString(log);
				Integer errorCode = ErrorCodeUtil.getErrorCode(le);
				if (null == cl) {
					cl = "otherSystem";
				}
				if (null == an) {
					an = "nullvalue";
				}
				if (null == errorCode) {
					errorCode = 0;
				}
				if (null == ld) {
					ld = "nullvalue";
				}
				JSONObject json = new JSONObject();
				json.put("request_id", requestID);
				json.put("log_time", time);
				json.put("sys_name", cl);
				json.put("log_type", le);
				json.put("api_name", an);
				json.put("eco", errorCode);
				json.put("ld", ld);
				Long l = jedis.lpush(listKey, json.toString());
				if (null != listLimit) {
					if (l > listLimit) {
						jedis.ltrim(listKey, 0, listLimit);
					}
				}

			}
		}
	}
}
