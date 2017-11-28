package com.ylp.cloudmonitor.spark.model.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.cloudmonitor.spark.sparkdao.factory.SparkDAOFactory;
import com.ylp.cloudmonitor.spark.util.TimeUtil;
import com.ylp.common.enums.LogEnum;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.factory.RedisDAOFactory;

import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public class RequestUtil implements Serializable {
	public static String getStartTime(RowLog log) {
		String detail = log.getDetail();
		String startTime = null;
		if (null == detail) {
			return null;
		} else {
			String[] detaildata = detail.split(":");
			if (detaildata[0].equals("request started")) {
				startTime = log.getTime();
			}
			return startTime;
		}
	}

	public static String getEndTime(RowLog log) {
		String detail = log.getDetail();
		String endTime = null;
		if (null == detail) {
			return null;
		} else {
			String[] detaildata = detail.split(":");
			if (detaildata[0].equals("request completed")) {
				endTime = log.getTime();
			}
			return endTime;
		}
	}

	public static String getDate(Iterable<RowLog> iterable) {
		RowLog log = iterable.iterator().next();
		String date = log.getTime().split(" ")[0].replace("-", "");
		return date;
	}

	public static RequestLogs getRequestGroup(Tuple2<String, Iterable<RowLog>> tuple) {
		String requestId = tuple._1;
		String date = tuple._2.iterator().next().getTime().split(" ")[0].replace("-", "");
		String startTime = null;
		String url = null;
		String cl = null;
		String an = null;
		String stopTime = null;
		String userName = null;
		String role = null;
		String entrepreneurId = null;
		String ip = null;
		String browserType = null;
		String browserVersion = null;
		String mobileBrand = null;
		String systemVersion = null;
		String wechatVersion = null;
		String mobileVersion = null;
		String network = null;
		String warnCount = null;
		String errorCount = null;
		String st = null;
		String et = null;
		String usingTime = "0";
		String systemId = null;
		String apiId = null;
		RequestLogs rgroup = new RequestLogs();
		JedisCluster jedis = JedisHelper.dataCluster();
		List<RowLog> loglist = new ArrayList<RowLog>();
		// ============================================
		// 获取redis中缓存的RoeLog
		if (SparkDAOFactory.getRequestCache().filterRequestCache(tuple)) {
			List<RowLog> cachelist = SparkDAOFactory.getRequestCache().getRequestCache(jedis, date, requestId);
			loglist.addAll(cachelist);
		}
		// 对相同requestid的log按时间排序
		// ==============================================
		// 遍历缓存的日志和新获取的日志
		for (RowLog log : tuple._2) {
			loglist.add(log);
		}
		for (RowLog log : loglist) {
			// 获取request中最早的时间
			if (null == st) {
				st = log.getTime();
			} else {
				if (TimeUtil.after(st, log.getTime())) {
					st = log.getTime();
				}
			}
			// 获取request中最晚的时间
			if (null == et) {
				et = log.getTime();
			} else {
				if (TimeUtil.before(et, log.getTime())) {
					et = log.getTime();
				}
			}
			// 统计每个request group的error和warn
			String level = log.getLevel();
			rgroup.add(level);
			rgroup.setLevel(level);

			// 从request started字段中获取url与用户信息
			String detail = log.getDetail();
			String[] detaildata = detail.split(":");
			if (detaildata[0].equals("request started")) {
				startTime = log.getTime();
				// 匹配到http的便是含有url的日志
				try {
					// 尝试获取url，获取不到的url为nullvalue
					if (detaildata[1].equals("http")) {
						for (int i = 1; i < detaildata.length; i++) {
							if (i == 1) {
								url = detaildata[i];
							} else {
								url = url + ":" + detaildata[i];
							}
						}

						// 获取系统名
						cl = RedisDAOFactory.getSystemUrlDAO().getSysCnName(jedis, url);
						systemId = RedisDAOFactory.getSystemListDAO().getSysId(cl);
						if (null == cl || cl.equals("null")) {
							cl = url.split("/")[2];
						}
						if (null == systemId || systemId.equals("null")) {
							systemId = "0";
						}
						// 从url中获取接口名称api_name
						// an = URLUtil.getApiName(apiNameArray, url);
						// an=SparkDAOFactory.getAPIList().getApiName(url);
						an = RedisDAOFactory.getAPIListDAO().getApiName(url);
						apiId = RedisDAOFactory.getAPIListDAO().getApiId(url);
						if (null == an || an.equals("null")) {
							if (url.contains("?")) {
								an = url.split("\\?")[0].replace("http://" + url.split("/")[2] + "/", "");
							} else {
								an = url.replace("http://" + url.split("/")[2] + "/", "");
							}
						}

						if (null == apiId || apiId.equals("null")) {
							apiId = "0";
						}

					} else {
						try {
							// 没有匹配到http的便是含有用户信息的日志
							String[] requestDetail = detail.split("\\|");
							userName = requestDetail[0].split(":")[1];
							role = requestDetail[1];
							entrepreneurId = requestDetail[2];
							ip = requestDetail[3];
							browserType = requestDetail[4];
							browserVersion = requestDetail[5];
							mobileBrand = requestDetail[6];
							systemVersion = requestDetail[7];
							wechatVersion = requestDetail[8];
							mobileVersion = requestDetail[9];
							network = requestDetail[10];
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				} catch (Exception e) {

				}

			} else if (detaildata[0].equals("request completed")) {
				stopTime = log.getTime();
			}
			date = log.getTime().split(" ")[0].replace("-", "");
		}

		// 如果获取不到开始时间或结束时间就将request最早和最晚的时间当做开始结束时间
		if (null == startTime) {
			startTime = st;
		}
		if (null == stopTime) {
			stopTime = et;
		}

		// 计算request消耗时间
		usingTime = String.valueOf(TimeUtil.useTime(startTime, stopTime));

		// 将获取到的数据封装成RequestGroup形式
		rgroup.setRequestId(requestId);
		rgroup.setUrl(url);
		rgroup.setStratTime(startTime);
		rgroup.setStopTime(stopTime);
		rgroup.setCl(cl);
		rgroup.setAn(an);
		rgroup.setUserName(userName);
		rgroup.setRole(role);
		rgroup.setEntrepreneurId(entrepreneurId);
		rgroup.setIp(ip);
		rgroup.setBrowserType(browserType);
		rgroup.setBrowserVersion(browserVersion);
		rgroup.setMobileBrand(mobileBrand);
		rgroup.setSystemVersion(systemVersion);
		rgroup.setWechatVersion(wechatVersion);
		rgroup.setMobileVersion(mobileVersion);
		rgroup.setNetwork(network);
		rgroup.setLogList(sort(loglist));
		rgroup.setUseTime(usingTime);
		rgroup.setSystemId(systemId);
		rgroup.setApiId(apiId);
		rgroup.setFrequency(TimeUtil.getfrequency(startTime));
		return rgroup;
	}

	// 对list中的log按时间排序
	public static String sort(List<RowLog> loglist) {

		String logString = null;
		Collections.sort(loglist, new Comparator<RowLog>() {
			public int compare(RowLog log1, RowLog log2) {
				try {
					if (TimeUtil.before(log1.getTime(), log2.getTime())) {
						return -1;
					} else if (TimeUtil.after(log1.getTime(), log2.getTime())) {
						return 1;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				return 0;
			}
		});
		for (RowLog log : loglist) {
			try {
				if (null == logString) {

					logString = log.toString(log);

					if (log.getLevel().equals(LogEnum.error.getEnName())
							|| log.getLevel().equals(LogEnum.warn.getEnName())) {
						logString ="<span style=\"color:red\">"+logString+"</span>";

					}

				} else {
					if (log.getLevel().equals(LogEnum.error.getEnName())
							|| log.getLevel().equals(LogEnum.warn.getEnName())) {
						logString = logString + "\\\\n" +"<span style=\"color:red\">"+log.toString(log)+"</span>";

					}else{
						logString = logString + "\\\\n" + log.toString(log);
					}
				
				}
			} catch (Exception e) {
			}
		}
		return logString;

	}

	
	public static RequestLogs getRequestGroupByWindow(Tuple2<String, Iterable<RowLog>> tuple) {
		String requestId = tuple._1;
		String date = tuple._2.iterator().next().getTime().split(" ")[0].replace("-", "");
		String startTime = null;
		String url = null;
		String cl = null;
		String an = null;
		String stopTime = null;
		String userName = null;
		String role = null;
		String entrepreneurId = null;
		String ip = null;
		String browserType = null;
		String browserVersion = null;
		String mobileBrand = null;
		String systemVersion = null;
		String wechatVersion = null;
		String mobileVersion = null;
		String network = null;
		String warnCount = null;
		String errorCount = null;
		String st = null;
		String et = null;
		String usingTime = "0";
		String systemId = null;
		String apiId = null;
		RequestLogs rgroup = new RequestLogs();
		JedisCluster jedis = JedisHelper.dataCluster();
		List<RowLog> loglist = new ArrayList<RowLog>();
		// ============================================
		// 获取redis中缓存的RoeLog
		if (SparkDAOFactory.getRequestCache().filterRequestCache(tuple)) {
			List<RowLog> cachelist = SparkDAOFactory.getRequestCacheForWindow().getRequestCache(jedis, date, requestId);
			loglist.addAll(cachelist);
		}
		// 对相同requestid的log按时间排序
		// ==============================================
		// 遍历缓存的日志和新获取的日志
		for (RowLog log : tuple._2) {
			loglist.add(log);
		}
		for (RowLog log : loglist) {
			// 获取request中最早的时间
			if (null == st) {
				st = log.getTime();
			} else {
				if (TimeUtil.after(st, log.getTime())) {
					st = log.getTime();
				}
			}
			// 获取request中最晚的时间
			if (null == et) {
				et = log.getTime();
			} else {
				if (TimeUtil.before(et, log.getTime())) {
					et = log.getTime();
				}
			}
			// 统计每个request group的error和warn
			String level = log.getLevel();
			rgroup.add(level);
			rgroup.setLevel(level);

			// 从request started字段中获取url与用户信息
			String detail = log.getDetail();
			String[] detaildata = detail.split(":");
			if (detaildata[0].equals("request started")) {
				startTime = log.getTime();
				// 匹配到http的便是含有url的日志
				try {
					// 尝试获取url，获取不到的url为nullvalue
					if (detaildata[1].equals("http")) {
						for (int i = 1; i < detaildata.length; i++) {
							if (i == 1) {
								url = detaildata[i];
							} else {
								url = url + ":" + detaildata[i];
							}
						}

						// 获取系统名
						cl = RedisDAOFactory.getSystemUrlDAO().getSysCnName(jedis, url);
						systemId = RedisDAOFactory.getSystemListDAO().getSysId(cl);
						if (null == cl || cl.equals("null")) {
							cl = url.split("/")[2];
						}
						if (null == systemId || systemId.equals("null")) {
							systemId = "0";
						}
						// 从url中获取接口名称api_name
						// an = URLUtil.getApiName(apiNameArray, url);
						// an=SparkDAOFactory.getAPIList().getApiName(url);
						an = RedisDAOFactory.getAPIListDAO().getApiName(url);
						apiId = RedisDAOFactory.getAPIListDAO().getApiId(url);
						if (null == an || an.equals("null")) {
							if (url.contains("?")) {
								an = url.split("\\?")[0].replace("http://" + url.split("/")[2] + "/", "");
							} else {
								an = url.replace("http://" + url.split("/")[2] + "/", "");
							}
						}

						if (null == apiId || apiId.equals("null")) {
							apiId = "0";
						}

					} else {
						try {
							// 没有匹配到http的便是含有用户信息的日志
							String[] requestDetail = detail.split("\\|");
							userName = requestDetail[0].split(":")[1];
							role = requestDetail[1];
							entrepreneurId = requestDetail[2];
							ip = requestDetail[3];
							browserType = requestDetail[4];
							browserVersion = requestDetail[5];
							mobileBrand = requestDetail[6];
							systemVersion = requestDetail[7];
							wechatVersion = requestDetail[8];
							mobileVersion = requestDetail[9];
							network = requestDetail[10];
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				} catch (Exception e) {

				}

			} else if (detaildata[0].equals("request completed")) {
				stopTime = log.getTime();
			}
			date = log.getTime().split(" ")[0].replace("-", "");
		}

		// 如果获取不到开始时间或结束时间就将request最早和最晚的时间当做开始结束时间
		if (null == startTime) {
			startTime = st;
		}
		if (null == stopTime) {
			stopTime = et;
		}

		// 计算request消耗时间
		usingTime = String.valueOf(TimeUtil.useTime(startTime, stopTime));

		// 将获取到的数据封装成RequestGroup形式
		rgroup.setRequestId(requestId);
		rgroup.setUrl(url);
		rgroup.setStratTime(startTime);
		rgroup.setStopTime(stopTime);
		rgroup.setCl(cl);
		rgroup.setAn(an);
		rgroup.setUserName(userName);
		rgroup.setRole(role);
		rgroup.setEntrepreneurId(entrepreneurId);
		rgroup.setIp(ip);
		rgroup.setBrowserType(browserType);
		rgroup.setBrowserVersion(browserVersion);
		rgroup.setMobileBrand(mobileBrand);
		rgroup.setSystemVersion(systemVersion);
		rgroup.setWechatVersion(wechatVersion);
		rgroup.setMobileVersion(mobileVersion);
		rgroup.setNetwork(network);
		rgroup.setLogList(sort(loglist));
		rgroup.setUseTime(usingTime);
		rgroup.setSystemId(systemId);
		rgroup.setApiId(apiId);
		rgroup.setFrequency(TimeUtil.getfrequency(startTime));
		return rgroup;
	}

}
