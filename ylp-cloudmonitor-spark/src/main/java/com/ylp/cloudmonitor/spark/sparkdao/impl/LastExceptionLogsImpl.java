package com.ylp.cloudmonitor.spark.sparkdao.impl;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.cloudmonitor.spark.sparkdao.LastExceptionLogs;
import com.ylp.cloudmonitor.spark.util.ErrorCodeUtil;
import com.ylp.cloudmonitor.spark.util.RedisUtil;
import com.ylp.common.enums.FieldEnum;
import com.ylp.common.enums.LogEnum;
import com.ylp.common.es.constant.RedisKey;

import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;

public class LastExceptionLogsImpl implements LastExceptionLogs {

	public String getRedisDetailKey() {
		String redisDetailKey = RedisKey.REQUEST_DETAIL;
		return redisDetailKey;
	}

	public String getListKey() {
		String listKey = RedisKey.LAST_EXCEPTION;
		return listKey;
	}

	public void saveExceptionLogToList(JedisCluster jedis, RowLog log, Integer listLimit) {
		// 将error和warn级别的日志详情储存到redis的list中
		String redisDetailKey = getRedisDetailKey();
		String listKey = getListKey();
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
					cl = "其他服务";
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

	public void saveExceptionLogToList(JedisCluster jedis, RequestLogs rgroup, Integer listLimit) {
		String redisDetailKey = getRedisDetailKey();
		String listKey = getListKey();
		if (!rgroup.getRequestId().equals(" ") && !rgroup.getRequestId().equals("nullvalue")) {
			if (rgroup.getLevel().equals(LogEnum.error.getEnName()) || rgroup.getLevel().equals(LogEnum.warn.getEnName())) {
				String date = rgroup.getStratTime().split(" ")[0].replace("-", "");
				String rkey = String.format(redisDetailKey, date);
				String key = rgroup.getRequestId() + "_" + rgroup.getLevel().replace(" ", "");
				RedisUtil redisUtil = new RedisUtil();
				redisUtil.hmSadd(jedis, rkey, key, rgroup.getLogList(), null);
				// 将error和warn级别的日志的相关信息储存到一个list中
				String requestID = rgroup.getRequestId();
				String time = rgroup.getStratTime();
				String cl = rgroup.getCl();
				String an =rgroup.getAn();
				String le = rgroup.getLevel();
				String ld = rgroup.getLogList();
				//String ld=null;
				Integer errorCode = ErrorCodeUtil.getErrorCode(le);
				if (null == cl) {
					cl = jedis.hget(rkey, requestID + "_" + FieldEnum.callLocation.getEnName());
					if (null == cl) {
						cl = "其他服务";
					}
				}
				if (null == an) {
					an = jedis.hget(rkey, requestID + "_" + FieldEnum.interfaceName.getEnName());
					if (null == an) {
						an = "nullvalue";
					}
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
