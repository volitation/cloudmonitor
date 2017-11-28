package com.ylp.cloudmonitor.spark.util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jets3t.service.utils.TimeFormatter;

import com.ylp.common.enums.RedisEnum;
import com.ylp.common.es.util.JedisHelper;

import akka.event.slf4j.Logger;
import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public class TimeUtil implements Serializable {

	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public static String timeLevel(String st, String et) {

		double consuming = useTime(st, et);
		String timeLevel = null;

		if (consuming <= 1) {
			timeLevel = "1s";
		} else if (consuming <= 3) {
			timeLevel = "1~3s";
		} else if (consuming <= 10) {
			timeLevel = "3~10s";
		} else if (consuming <= 30) {
			timeLevel = "10~30s";
		} else {
			timeLevel = "out_of_30s";
		}

		return timeLevel;
	}

	public static Double useTime(String st, String et) {
		double consuming = 0.0;
		Long startTime = 0L;
		Long endTime = 0L;

		try {

			startTime = TIME_FORMAT.parse(st).getTime();
			endTime = TIME_FORMAT.parse(et).getTime();
			consuming = Double
					.valueOf(DataUtil.div(DataUtil.sub(String.valueOf(endTime), String.valueOf(startTime)), "1000"));
		} catch (Exception e) {

		}
		if (consuming <= 0.0 || consuming!=useTimeFilter(st, et)) {
			System.out.println(consuming);
			Boolean t = true;
			int i = 0;
			while (t) {
				i++;
				try {
					startTime = TIME_FORMAT.parse(st).getTime();
					endTime = TIME_FORMAT.parse(et).getTime();
					consuming = Double.valueOf(
							DataUtil.div(DataUtil.sub(String.valueOf(endTime), String.valueOf(startTime)), "1000"));
				} catch (Exception e) {
				}
				if (st.equals(et) && consuming == 0.0) {
					t = false;
				}
				if (consuming > 0.0 && consuming ==useTimeFilter(st, et)) {
					t = false;
				}
				if (i >= 100) {
					t = false;
				}
			}

		}

		if (consuming <0.0 || consuming > 100.0|| consuming!=useTimeFilter(st, et)) {
			// 由此耗时计算偶尔会出现异常值，查看异常值得输入参数确实正确的参数，使用该参数重新计算结果却是正确的
			// 暂时无法发现异常值的出现原因，所以将小于0大于一天的耗时都等于0
			// 经过测试得知是时间戳转换引起的错误
			// 2017-03-09 18:16:39.992转换成时间戳得到的值是1490663799992，这是错误的
			JedisCluster jedis = JedisHelper.dataCluster();
			String key = st + "|" + et;
			String value = startTime + "|" + endTime + "|" + consuming +"|" +useTimeFilter(st, et);
			jedis.hset("usingTimeTestByYangjiajie1", key, value);
			consuming = useTimeFilter(st, et);
		}
		if (consuming ==0.0 &&!st.equals(et)) {
			// 由此耗时计算偶尔会出现异常值，查看异常值得输入参数确实正确的参数，使用该参数重新计算结果却是正确的
			// 暂时无法发现异常值的出现原因，所以将小于0大于一天的耗时都等于0
			// 经过测试得知是时间戳转换引起的错误
			// 2017-03-09 18:16:39.992转换成时间戳得到的值是1490663799992，这是错误的
			JedisCluster jedis = JedisHelper.dataCluster();
			String key = st + "|" + et;
			String value = startTime + "|" + endTime + "|" + consuming +"|" +useTimeFilter(st, et);
			jedis.hset("usingTimeTestByYangjiajie1", key, value);
			consuming = useTimeFilter(st, et);
		}
		return consuming;

	}
	// ==============================================

	// =============================================================
	public static String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		String date = df.format(new Date());// new Date()为获取当前系统时间
		return date;
	}

	public static String getTime() {
		String date = TIME_FORMAT.format(new Date());// new Date()为获取当前系统时间
		return date;
	}

	/**
	 * 判断一个时间是否在另一个时间之前
	 * 
	 * @param time1
	 *            第一个时间
	 * @param time2
	 *            第二个时间
	 * @return 判断结果
	 */
	public static boolean before(String time1, String time2) {
		try {
			Date dateTime1 = TIME_FORMAT.parse(time1);
			Date dateTime2 = TIME_FORMAT.parse(time2);

			if (dateTime1.before(dateTime2)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断一个时间是否在另一个时间之后
	 * 
	 * @param time1
	 *            第一个时间
	 * @param time2
	 *            第二个时间
	 * @return 判断结果
	 */
	public static boolean after(String time1, String time2) {
		try {
			Date dateTime1 = TIME_FORMAT.parse(time1);
			Date dateTime2 = TIME_FORMAT.parse(time2);

			if (dateTime1.after(dateTime2)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date) {

		String str = TIME_FORMAT.format(date);
		return str;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {

		Date date = null;
		try {
			date = TIME_FORMAT.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	// 获取日志所在时间段
	public static String getfrequency(String time) {
		int t = 0;
		// 初始化默认为第一个时间段
		String frequency = RedisEnum.frequency_0000_0030.getEnName();
		try {
			String date = time.split(" ")[0] + " 00:00:00.000";
			double startTime = TIME_FORMAT.parse(date).getTime();
			double endTime = TIME_FORMAT.parse(time).getTime();
			t = (int) ((endTime - startTime) / 1800000);
		} catch (Exception e) {
			// TODO: handle exception
		}

		switch (t) {
		case 0:
			frequency = RedisEnum.frequency_0000_0030.getEnName();
			break;
		case 1:
			frequency = RedisEnum.frequency_0030_0100.getEnName();
			break;
		case 2:
			frequency = RedisEnum.frequency_0100_0130.getEnName();
			break;
		case 3:
			frequency = RedisEnum.frequency_0130_0200.getEnName();
			break;
		case 4:
			frequency = RedisEnum.frequency_0200_0230.getEnName();
			break;
		case 5:
			frequency = RedisEnum.frequency_0230_0300.getEnName();
			break;
		case 6:
			frequency = RedisEnum.frequency_0300_0330.getEnName();
			break;
		case 7:
			frequency = RedisEnum.frequency_0330_0400.getEnName();
			break;
		case 8:
			frequency = RedisEnum.frequency_0400_0430.getEnName();
			break;
		case 9:
			frequency = RedisEnum.frequency_0430_0500.getEnName();
			break;
		case 10:
			frequency = RedisEnum.frequency_0500_0530.getEnName();
			break;
		case 11:
			frequency = RedisEnum.frequency_0530_0600.getEnName();
			break;
		case 12:
			frequency = RedisEnum.frequency_0600_0630.getEnName();
			break;
		case 13:
			frequency = RedisEnum.frequency_0630_0700.getEnName();
			break;
		case 14:
			frequency = RedisEnum.frequency_0700_0730.getEnName();
			break;
		case 15:
			frequency = RedisEnum.frequency_0730_0800.getEnName();
			break;
		case 16:
			frequency = RedisEnum.frequency_0800_0830.getEnName();
			break;
		case 17:
			frequency = RedisEnum.frequency_0830_0900.getEnName();
			break;
		case 18:
			frequency = RedisEnum.frequency_0900_0930.getEnName();
			break;
		case 19:
			frequency = RedisEnum.frequency_0930_1000.getEnName();
			break;
		case 20:
			frequency = RedisEnum.frequency_1000_1030.getEnName();
			break;
		case 21:
			frequency = RedisEnum.frequency_1030_1100.getEnName();
			break;
		case 22:
			frequency = RedisEnum.frequency_1100_1130.getEnName();
			break;
		case 23:
			frequency = RedisEnum.frequency_1130_1200.getEnName();
			break;
		case 24:
			frequency = RedisEnum.frequency_1200_1230.getEnName();
			break;
		case 25:
			frequency = RedisEnum.frequency_1230_1300.getEnName();
			break;
		case 26:
			frequency = RedisEnum.frequency_1300_1330.getEnName();
			break;
		case 27:
			frequency = RedisEnum.frequency_1330_1400.getEnName();
			break;
		case 28:
			frequency = RedisEnum.frequency_1400_1430.getEnName();
			break;
		case 29:
			frequency = RedisEnum.frequency_1430_1500.getEnName();
			break;
		case 30:
			frequency = RedisEnum.frequency_1500_1530.getEnName();
			break;
		case 31:
			frequency = RedisEnum.frequency_1530_1600.getEnName();
			break;
		case 32:
			frequency = RedisEnum.frequency_1600_1630.getEnName();
			break;
		case 33:
			frequency = RedisEnum.frequency_1630_1700.getEnName();
			break;
		case 34:
			frequency = RedisEnum.frequency_1700_1730.getEnName();
			break;
		case 35:
			frequency = RedisEnum.frequency_1730_1800.getEnName();
			break;
		case 36:
			frequency = RedisEnum.frequency_1800_1830.getEnName();
			break;
		case 37:
			frequency = RedisEnum.frequency_1830_1900.getEnName();
			break;
		case 38:
			frequency = RedisEnum.frequency_1900_1930.getEnName();
			break;
		case 39:
			frequency = RedisEnum.frequency_1930_2000.getEnName();
			break;
		case 40:
			frequency = RedisEnum.frequency_2000_2030.getEnName();
			break;
		case 41:
			frequency = RedisEnum.frequency_2030_2100.getEnName();
			break;
		case 42:
			frequency = RedisEnum.frequency_2100_2130.getEnName();
			break;
		case 43:
			frequency = RedisEnum.frequency_2130_2200.getEnName();
			break;
		case 44:
			frequency = RedisEnum.frequency_2200_2230.getEnName();
			break;
		case 45:
			frequency = RedisEnum.frequency_2230_2300.getEnName();
			break;
		case 46:
			frequency = RedisEnum.frequency_2300_2330.getEnName();
			break;
		case 47:
			frequency = RedisEnum.frequency_2330_0000.getEnName();
			break;
		default:
			frequency = RedisEnum.frequency_0000_0030.getEnName();
			break;
		}
		return frequency;

	}

	public static Map<String, Double> getRequestTimeGroup(Tuple2<String, Iterable<Double>> tuple) {
		Iterable<Double> iterable = tuple._2;
		Double maxTime = null;
		Double minTime = null;
		Double totalTime = 0.0;
		for (Double time : iterable) {
			totalTime = totalTime + time;
			if (null == maxTime) {
				maxTime = time;
			}
			if (null == minTime) {
				minTime = time;
			}
			if (maxTime < time) {
				maxTime = time;
			}
			if (minTime > time) {
				minTime = time;
			}
		}
		Map<String, Double> tMap = new HashMap<String, Double>();
		tMap.put(RedisEnum.max_using_time.getEnName(), maxTime);
		tMap.put(RedisEnum.min_using_time.getEnName(), minTime);
		tMap.put(RedisEnum.total_using_time.getEnName(), totalTime);
		return tMap;

	}

	public static Double formatDouble(double d) {
		DecimalFormat df = new DecimalFormat("#.000");

		return Double.valueOf(df.format(d));
	}

	public static Double useTimeFilter(String st, String et) {
		Integer sh=Integer.valueOf(st.split(" ")[1].split(":")[0]);
		Integer eh=Integer.valueOf(et.split(" ")[1].split(":")[0]);
		Integer sm=Integer.valueOf(st.split(" ")[1].split(":")[1]);
		Integer em=Integer.valueOf(et.split(" ")[1].split(":")[1]);
		Integer ss=Integer.valueOf(st.split(" ")[1].split(":")[2].split("\\.")[0]);
		Integer es=Integer.valueOf(et.split(" ")[1].split(":")[2].split("\\.")[0]);
		Integer sms=Integer.valueOf(st.split(" ")[1].split(":")[2].split("\\.")[1]);
		Integer ems=Integer.valueOf(et.split(" ")[1].split(":")[2].split("\\.")[1]);
		
		Integer startTime =(sh*3600+sm*60+ss)*1000+sms;
		Integer endTime =(eh*3600+em*60+es)*1000+ems;
		
		String result =DataUtil.sub(String.valueOf(endTime), String.valueOf(startTime));
		
		return Double.valueOf(DataUtil.div(result, "1000"));
	}

}
