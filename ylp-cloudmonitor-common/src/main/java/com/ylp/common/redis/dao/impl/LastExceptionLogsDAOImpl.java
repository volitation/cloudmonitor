package com.ylp.common.redis.dao.impl;

import java.util.ArrayList;
import java.util.List;


import com.ylp.common.entity.ExceptionLogInfo;
import com.ylp.common.enums.LogEnum;
import com.ylp.common.enums.RedisEnum;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.LastExceptionLogsDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;

public class LastExceptionLogsDAOImpl implements LastExceptionLogsDAO {
	private String rkey = RedisKey.LAST_EXCEPTION;

//	public void saveExceptionLogToList(JedisCluster jedis, RequestLogs rgroup, Integer listLimit) {
//
//		if (!rgroup.getRequestId().equals(" ") && !rgroup.getRequestId().equals("nullvalue")) {
//			if (rgroup.getLevel().equals(LogEnum.error.getEnName())
//					|| rgroup.getLevel().equals(LogEnum.warn.getEnName())) {
//
//				// 将error和warn级别的日志的相关信息储存到一个list中
//				String requestID = rgroup.getRequestId();
//				String time = rgroup.getStratTime();
//				String cl = rgroup.getCl();
//				String an = rgroup.getAn();
//				String le = rgroup.getLevel();
//				String usingTime = rgroup.getUseTime();
//				String ld = rgroup.getLogList();
//				// String ld=null;
//				Integer errorCode = ErrorCodeUtil.getErrorCode(le);
//				if (null == cl) {
//					cl = "其他服务";
//				}
//				if (null == an) {
//					an = "nullvalue";
//				}
//				if (null == errorCode) {
//					errorCode = 0;
//				}
//				if (null == usingTime) {
//					usingTime = "0";
//				}
//				if (null == ld) {
//					ld = "nullvalue";
//				}
//				JSONObject json = new JSONObject();
//				json.put(RedisEnum.requestId.getEnName(), requestID);
//				json.put(RedisEnum.logTime.getEnName(), time);
//				json.put(RedisEnum.systemName.getEnName(), cl);
//				json.put(RedisEnum.logType.getEnName(), le);
//				json.put(RedisEnum.apiName.getEnName(), an);
//				json.put(RedisEnum.eco.getEnName(), errorCode);
//				json.put(RedisEnum.usingTime.getEnName(), usingTime);
//				json.put(RedisEnum.logDetail.getEnName(), ld);
//				Long l = jedis.lpush(rkey, json.toString());
//				if (null != listLimit) {
//					if (l > listLimit) {
//						jedis.ltrim(rkey, 0, listLimit);
//					}
//				}
//
//			}
//		}
//
//	}

	public List<ExceptionLogInfo> getExceptionLogList() {
		JedisCluster jedis = JedisHelper.dataCluster();
		List<String> exceptionList = jedis.lrange(rkey, 0, -1);
		List<ExceptionLogInfo> list = new ArrayList<ExceptionLogInfo>();
		JSONArray array = JSONArray.fromObject(exceptionList);

		for (int i = 0; i < array.size(); i++) {
			try {
				ExceptionLogInfo Info = new ExceptionLogInfo();
				Info.setRequestId(array.getJSONObject(i).getString(RedisEnum.requestId.getEnName()));
				Info.setLogTime(array.getJSONObject(i).getString(RedisEnum.logTime.getEnName()));
				Info.setSystemName(array.getJSONObject(i).getString(RedisEnum.systemName.getEnName()));
				Info.setLogType(array.getJSONObject(i).getString(RedisEnum.logType.getEnName()));
				Info.setApiName(array.getJSONObject(i).getString(RedisEnum.apiName.getEnName()));
				Info.setEco(array.getJSONObject(i).getString(RedisEnum.eco.getEnName()));
				Info.setUsingTime(array.getJSONObject(i).getString(RedisEnum.usingTime.getEnName()));
				Info.setLogDetail(array.getJSONObject(i).getString(RedisEnum.logDetail.getEnName()));
				list.add(Info);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return list;
	}

	public void saveExceptionLogToList(JedisCluster jedis, ExceptionLogInfo info, Integer listLimit) {
		if (!info.getRequestId().equals(" ") && !info.getRequestId().equals("nullvalue")) {
			if (info.getLogType().equals(LogEnum.error.getEnName())
					|| info.getLogType().equals(LogEnum.warn.getEnName())) {

				String requestID = info.getRequestId();
				String time = info.getLogTime();
				String cl = info.getSystemName();
				String an = info.getApiName();
				String le = info.getLogType();
				String usingTime = info.getUsingTime();
				String ld = info.getLogDetail();
				// String ld=null;
				Integer errorCode = Integer.valueOf(info.getEco());
				if (null == cl) {
					cl = "其他服务";
				}
				if (null == an) {
					an = "nullvalue";
				}
				if (null == errorCode) {
					errorCode = 0;
				}
				if (null == usingTime) {
					usingTime = "0";
				}
				if (null == ld) {
					ld = "nullvalue";
				}
				JSONObject json = new JSONObject();
				json.put(RedisEnum.requestId.getEnName(), requestID);
				json.put(RedisEnum.logTime.getEnName(), time);
				json.put(RedisEnum.systemName.getEnName(), cl);
				json.put(RedisEnum.logType.getEnName(), le);
				json.put(RedisEnum.apiName.getEnName(), an);
				json.put(RedisEnum.eco.getEnName(), errorCode);
				json.put(RedisEnum.usingTime.getEnName(), usingTime);
				json.put(RedisEnum.logDetail.getEnName(), ld);
				Long l = jedis.lpush(rkey, json.toString());
				if (null != listLimit) {
					if (l > listLimit) {
						jedis.ltrim(rkey, 0, listLimit);
					}
				}

			}
		}
	}
}
