package com.ylp.cloudmonitor.spark.streaming;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.elasticsearch.action.bulk.BulkResponse;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.cloudmonitor.spark.model.util.RequestUtil;
import com.ylp.cloudmonitor.spark.util.ErrorCodeUtil;
import com.ylp.cloudmonitor.spark.util.RedisUtil;
import com.ylp.cloudmonitor.spark.util.TimeUtil;
import com.ylp.cloudmonitor.spark.util.URLUtil;
import com.ylp.common.enums.FieldEnum;
import com.ylp.common.enums.LogEnum;
import com.ylp.common.enums.TypeEnum;
import com.ylp.common.es.api.ESClient;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.factory.MapFactory;
import com.ylp.common.es.util.JedisHelper;

import kafka.serializer.StringDecoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public class LogCount implements Serializable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// ======================================================//
		// 输入参数
		// 第一个参数为SparkStreaming统计的时间间隔，以秒为单位
		int duration = Integer.valueOf(args[0]);
		// 第二个参数为kafkaTopics
		String kafkaTopics = args[1];
		// 第三个参数为kafka集群（kafkahosts1:port,kafkahosts2:port）
		String metadata_broker_list = args[2];
		//设置spark checkpoint
		String checkpoint = args[3];
		// =====================================================//
		// 构建sparkcontext
		SparkConf conf = new SparkConf().setAppName("LogCount");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(duration));
		jssc.checkpoint(checkpoint);
		// ======================================================//
		// Input Data
		HashMap<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", metadata_broker_list);

		String[] kafkaTopicsSplited = kafkaTopics.split(",");

		Set<String> topics = new HashSet<String>();
		for (String kafkaTopic : kafkaTopicsSplited) {
			topics.add(kafkaTopic);
		}

		JavaPairInputDStream<String, String> logEventsDStream = KafkaUtils.createDirectStream(jssc, String.class,
				String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);
		// ======================================================//
		// Process Data
		// 日志统计的rediskey
		String count_key = RedisKey.REDIS_COUNT;
		// request detail 的redis key
		String request_detail = RedisKey.REQUEST_DETAIL;
		// 错误与警告日志列表的redis list key
		String last_exception = RedisKey.LAST_EXCEPTION;
		// 日志质量分析的redis hash key
		String logNumberCount = RedisKey.LOG_NUMBER_COUNT;
		// request耗时统计
		String requestTimeCount = "cm:screen:requestTimeCount";
		// 拉取系统名称映射表
		JedisCluster jedis = JedisHelper.dataCluster();
		List<String> sysNameList = jedis.lrange(RedisKey.SYSTEM_LIST, 0, -1);
		JSONArray sysNamearray = JSONArray.fromObject(sysNameList);
		// 拉取接口名映射表
		List<String> apiNameList = jedis.lrange(RedisKey.API_LIST, 0, -1);
		JSONArray apiNamearray = JSONArray.fromObject(apiNameList);

		// 处理数据
		JavaDStream<RowLog> logDStream = dataToLog(logEventsDStream);
		sysLogCount(logDStream, count_key, request_detail, sysNamearray, apiNamearray);
		// saveLogToRedis(logEventsDStream, request_detail, last_exception,
		// logNumberCount);
		saveExceptionLogToRedis(logDStream, request_detail, last_exception, 500);
		// saveDetailToES(logEventsDStream, request_detail);
		JavaPairDStream<RequestLogs, Iterable<RowLog>> requestGroupDStream = requestGroup(logDStream,
				request_detail, sysNamearray, apiNamearray);
		saveDataToES(requestGroupDStream);
		requestTimeConsuming(requestGroupDStream, request_detail, requestTimeCount);
		// ======================================================//
		// 关闭SparkStreamingContext
		jssc.start();
		jssc.awaitTermination();
		jssc.close();
	}

	// dataToLog函数用于将kafka得到的数据进行封装

	public static JavaDStream<RowLog> dataToLog(JavaPairInputDStream<String, String> logEventsDStream) {

		// 使用filter方法过滤掉无法封装的日志数据，比如错误数据和换行数据，然后再到map端进行日志封装，重复封装操作会降低效率，但可提高可靠性
		JavaDStream<RowLog> logDStream = logEventsDStream.filter(new Function<Tuple2<String, String>, Boolean>() {

			private static final long serialVersionUID = 1L;

			public Boolean call(Tuple2<String, String> tuple) throws Exception {
				try {
					String[] data = tuple._2.split("\\|");
					if (data.length > 4) {
						RowLog log = new RowLog();
						log.setVersion(data[0]);
						log.setLanguage(data[1]);
						log.setTime(data[2]);
						log.setLevel(data[3]);
						log.setHost(data[4]);
						log.setCodeLocation(data[5]);
						log.setRequestId(data[6]);
						log.setDetail(data[7]);
						return true;
					} else {
						return false;
					}
				} catch (Exception e) {

					return false;
				}
			}
		}).map(new Function<Tuple2<String, String>, RowLog>() {

			private static final long serialVersionUID = 1L;

			public RowLog call(Tuple2<String, String> tuple) throws Exception {

				String[] data = tuple._2.split("\\|");
				RowLog log = new RowLog();
				log.setVersion(data[0]);
				log.setLanguage(data[1]);
				log.setTime(data[2]);
				log.setLevel(data[3]);
				log.setHost(data[4]);
				log.setCodeLocation(data[5]);
				log.setRequestId(data[6]);
				String detail = data[7];
				if (data.length > 8) {
					for (int i = 8; i < data.length; i++)
						detail = detail + "|"+ data[i] ;
				}
				log.setDetail(detail);
				return log;

			}
		});
		return logDStream;

	}

	// 获取Request数据（将日志聚合后获取request的开始时间，结束时间，url，用户信息等数据）
	public static JavaPairDStream<RequestLogs, Iterable<RowLog>> requestGroup(JavaDStream<RowLog> logDStream,
			final String requestIdDetailKey, final JSONArray sysNameArray, final JSONArray apiNameArray) {

		// 使用filter过滤掉没有requestid的日志数据，然后对日志进行分组
		JavaPairDStream<String, Iterable<RowLog>> logGroupDStream = logDStream
				.filter(new Function<RowLog, Boolean>() {

					private static final long serialVersionUID = 1L;

					public Boolean call(RowLog log) throws Exception {
						String requestId = log.getRequestId();

						if (!requestId.isEmpty() && !requestId.equals(" ")) {
							return true;
						} else {
							return false;
						}
					}
				}).mapToPair(new PairFunction<RowLog, String, RowLog>() {
					// 将数据从log映射成<requestid,log>的形式
					private static final long serialVersionUID = 1L;

					public Tuple2<String, RowLog> call(RowLog log) throws Exception {
						String requestId = log.getRequestId();
						return new Tuple2<String, RowLog>(requestId, log);
					}
				}).groupByKey();

		// 对分组后的数据进行匹配，获取每个requestid组的开始时间，结束时间，url等信息
		JavaPairDStream<RequestLogs, Iterable<RowLog>> requestGroupDStream = logGroupDStream
				.mapToPair(new PairFunction<Tuple2<String, Iterable<RowLog>>, RequestLogs, Iterable<RowLog>>() {

					private static final long serialVersionUID = 1L;

					public Tuple2<RequestLogs, Iterable<RowLog>> call(Tuple2<String, Iterable<RowLog>> tuple)
							throws Exception {

						RequestLogs rgroup = RequestUtil.getRequestGroup(tuple);
						return new Tuple2<RequestLogs, Iterable<RowLog>>(rgroup, tuple._2);
					}
				});

		return requestGroupDStream;

	}

	// RequestIdLogCount函数按照系统分类统计日志级别
	public static void sysLogCount(JavaDStream<RowLog> logDStream, final String redisKey,
			final String requestIdDetailKey, final JSONArray sysNameArray, final JSONArray apiNameArray) {

		// 使用filter过滤掉没有requestid的日志数据，然后对日志进行分组
		JavaPairDStream<String, Iterable<RowLog>> logEventsGroupDStream = logDStream
				.filter(new Function<RowLog, Boolean>() {

					private static final long serialVersionUID = 1L;

					public Boolean call(RowLog log) throws Exception {
						String requestId = log.getRequestId();

						if (!requestId.isEmpty() && !requestId.equals(" ") && requestId.length() > 4) {
							return true;
						} else {
							return false;
						}
					}
				}).mapToPair(new PairFunction<RowLog, String, RowLog>() {
					// 将数据从log映射成<requestid,log>的形式
					private static final long serialVersionUID = 1L;

					public Tuple2<String, RowLog> call(RowLog log) throws Exception {
						String requestId = log.getRequestId();
						return new Tuple2<String, RowLog>(requestId, log);
					}
				}).groupByKey();

		// 对分组后的数据进行匹配，获取每个requestid组的开始时间，结束时间，url等信息
		JavaPairDStream<String, Iterable<RowLog>> groupDStream = logEventsGroupDStream
				.mapToPair(new PairFunction<Tuple2<String, Iterable<RowLog>>, String, Iterable<RowLog>>() {

					private static final long serialVersionUID = 1L;

					public Tuple2<String, Iterable<RowLog>> call(Tuple2<String, Iterable<RowLog>> tuple)
							throws Exception {
						String requestId = tuple._1;
						String startTime = null;
						String url = null;
						String stopTime = null;
						String date = null;
						for (RowLog log : tuple._2) {
							String detail = log.getDetail();
							String[] detaildata = detail.split(":");
							if (detaildata[0].equals("request started")) {
								startTime = log.getTime();
								try {
									if (detaildata[1].equals("http")) {
										for (int i = 1; i < detaildata.length; i++) {
											if (i == 1) {
												url = detaildata[i];
											} else {
												url = url + ":" + detaildata[i];
											}
										}
									}
								} catch (Exception e) {
									// TODO: handle exception
								}

							} else if (detaildata[0].equals("request completed")) {
								stopTime = log.getTime();
							}
							date = log.getTime().split(" ")[0].replace("-", "");
						}

						String key = requestId + "|" + url + "|" + startTime + "|" + stopTime + "|" + date + "|";
						return new Tuple2<String, Iterable<RowLog>>(key, tuple._2);
					}
				});
		// 将获取到的requestid信息写入到redis中
		groupDStream.foreachRDD(new VoidFunction<JavaPairRDD<String, Iterable<RowLog>>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, Iterable<RowLog>> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Iterable<RowLog>>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<String, Iterable<RowLog>>> iterator) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						while (iterator.hasNext()) {
							try {
								String s = iterator.next()._1;
								String[] data = s.split("\\|");
								String requestId = data[0];
								String url = data[1];
								String startTime = data[2];
								String stopTime = data[3];
								String date = data[4];
								String rkey = String.format(requestIdDetailKey, date);
								Map<String, String> cMap = new HashMap<String, String>();
								if (startTime.length() > 5) {
									cMap.put(requestId + "_" + FieldEnum.startTime.getEnName(), startTime);
								}
								if (stopTime.length() > 5) {
									cMap.put(requestId + "_" + FieldEnum.endTime.getEnName(), stopTime);
								}
								if (url.length() > 5) {
									cMap.put(requestId + "_" + FieldEnum.url.getEnName(), url);
									String cl = URLUtil.getSysName(sysNameArray, url);
									if (null == cl) {
										cl = url.split("/")[2];
									}
									cMap.put(requestId + "_" + FieldEnum.callLocation.getEnName(), cl);
									// 从url中获取接口名称api_name
									String an = URLUtil.getApiName(apiNameArray, url);
									if (null == an) {
										if (url.contains("?")) {
											an = url.split("\\?")[0].replace("http://" + url.split("/")[2] + "/", "");
										} else {
											an = url.replace("http://" + url.split("/")[2] + "/", "");
										}
									}
									cMap.put(requestId + "_" + FieldEnum.interfaceName.getEnName(), an);
								}
								jedis.hmset(rkey, cMap);
							} catch (Exception e) {
							}
						}
					}
				});

			}
		});
		// 从reuqestid对应的url中获取日志所属系统，按照系统进行日志级别统计
		JavaPairDStream<String, Integer> systemCountDStream = groupDStream
				.flatMapToPair(new PairFlatMapFunction<Tuple2<String, Iterable<RowLog>>, String, Integer>() {

					private static final long serialVersionUID = 1L;

					public Iterable<Tuple2<String, Integer>> call(Tuple2<String, Iterable<RowLog>> tuple)
							throws Exception {
						String[] data = tuple._1.split("\\|");
						String requestId = data[0];
						String date = data[4];
						String rkey = String.format(requestIdDetailKey, date);
						JedisCluster jedis = JedisHelper.dataCluster();
						String cl = jedis.hget(rkey, requestId + "_" + FieldEnum.callLocation.getEnName());
						if (null == cl) {
							String url = tuple._1.split("\\|")[1];
							if (url.isEmpty()) {
								url = jedis.hget(rkey, requestId + "_" + FieldEnum.url.getEnName());
							}
							cl = URLUtil.getSysName(sysNameArray, url);
							if (null == cl) {
								try {
									cl = url.split("/")[2];
								} catch (Exception e) {
								}
								if (null == cl) {
									cl = "otherSystem";
								}
							}
						}

						List<Tuple2<String, Integer>> list = new ArrayList<Tuple2<String, Integer>>();
						for (RowLog log : tuple._2) {
							String level = log.getLevel();
							String logdate = log.getTime().split(" ")[0].replace("-", "");
							String key = logdate + "|" + cl + "_" + level;
							list.add(new Tuple2<String, Integer>(key, 1));
						}
						return list;
					}
				}).reduceByKey(new Function2<Integer, Integer, Integer>() {

					private static final long serialVersionUID = 1L;

					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
				});

		// 将统计后的结果写入redis中
		systemCountDStream.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, Integer> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Integer>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<String, Integer>> iterator) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						while (iterator.hasNext()) {
							Tuple2<String, Integer> tuple = iterator.next();
							String[] data = tuple._1.split("\\|");
							String countKey = data[1];
							String date = data[0];
							Integer count = tuple._2;
							String rkey = String.format(redisKey, date);
							RedisUtil redisUtil = new RedisUtil();
							redisUtil.hmIadd(jedis, rkey, countKey, count, null);
						}
					}
				});

			}
		});

	}

	// saveLog将日志中换行的信息拼接起来再存储到redis
	public static void saveLogToRedis(JavaPairInputDStream<String, String> logEventsDStream,
			final String redisDetailKey, final String ListKey, final String logNumberCount) {

		// 使用foreachRDD函数，对获取到的DSteam数据里面的每个RDD执行操作
		logEventsDStream.foreachRDD(new VoidFunction<JavaPairRDD<String, String>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, String> rdd) throws Exception {
				JavaRDD<String> logEventsRDD = rdd.map(new Function<Tuple2<String, String>, String>() {

					private static final long serialVersionUID = 1L;

					public String call(Tuple2<String, String> tuple) throws Exception {
						return tuple._2;
					}
				});
				// 将从kafka获取到的数据转换成list
				List<String> list = logEventsRDD.collect();
				List<String> list2 = new ArrayList<String>();
				List<Map<String, Object>> eslist = new ArrayList<Map<String, Object>>();
				// 遍历list的元素，将换行的日志拼接到原日志中
				for (String line : list) {
					String[] data = line.split("\\|");
					if (data.length > 4) {
						list2.add(line);
					} else {
						Integer i = list2.size();
						if (i > 0) {
							String old = list2.get(i - 1);
							list2.set(i - 1, old + "\\n" + line);
						}
					}
				}
				Map<String, String> rmap = new HashMap<String, String>();
				// 将error和warn日志储存到redis中
				JedisCluster jedis = JedisHelper.dataCluster();

				// 在遍历日志的同时统计原始日志总数
				// detailLogNumber，格式符合规范的日志总数userfulLogNumber，request的日志总数requestLogNumber
				Integer detailLogNumber = 0;
				Integer userfulLogNumber = 0;
				Integer requestLogNumber = 0;
				// 封装数据，方便存储
				for (String s : list2) {
					detailLogNumber = detailLogNumber + 1;
					try {
						String[] data = s.split("\\|");
						RowLog log = new RowLog();
						if (data.length > 4) {
							log.setVersion(data[0]);
							log.setLanguage(data[1]);
							log.setTime(data[2]);
							log.setLevel(data[3]);
							log.setHost(data[4]);
							log.setCodeLocation(data[5]);
							log.setRequestId(data[6]);
							String detail = "";
							if (data.length > 7) {
								for (int i = 7; i < data.length; i++)
									detail = detail + data[i] + "|";
							}
							log.setDetail(detail);
							userfulLogNumber = userfulLogNumber + 1;
						}
						// 将error与warn级别的数据储存到redis中
						if (log.getRequestId().length() > 5) {
							requestLogNumber = requestLogNumber + 1;
							// 将error和warn级别的日志详情储存到相应的requestid的hashmap中
							if (log.getLevel().equals(LogEnum.error.getEnName())
									|| log.getLevel().equals(LogEnum.warn.getEnName())) {
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
									ld = jedis.hget(rkey, requestID + "_" + le.replace(" ", ""));
									if (null == ld) {
										ld = "nullvalue";
									}
								}
								JSONObject json = new JSONObject();
								json.put("request_id", requestID);
								json.put("log_time", time);
								json.put("sys_name", cl);
								json.put("log_type", le);
								json.put("api_name", an);
								json.put("eco", errorCode);
								json.put("ld", ld);
								Long l = jedis.lpush(ListKey, json.toString());
								if (l > 500) {
									jedis.ltrim(ListKey, 0, 500);
								}
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				// 遍历list后将得到的统计数写入到redis中
				DecimalFormat df = new DecimalFormat("0.00");
				RedisUtil redisUtil = new RedisUtil();
				redisUtil.hmIadd(jedis, logNumberCount, "detailLogNumber" + "_" + TimeUtil.getDate(), detailLogNumber,
						null);
				redisUtil.hmIadd(jedis, logNumberCount, "userfulLogNumber" + "_" + TimeUtil.getDate(), userfulLogNumber,
						null);
				redisUtil.hmIadd(jedis, logNumberCount, "requestLogNumber" + "_" + TimeUtil.getDate(), requestLogNumber,
						null);
				Integer dn = Integer.valueOf(jedis.hget(logNumberCount, "detailLogNumber" + "_" + TimeUtil.getDate()));
				Integer un = Integer.valueOf(jedis.hget(logNumberCount, "userfulLogNumber" + "_" + TimeUtil.getDate()));
				Integer rn = Integer.valueOf(jedis.hget(logNumberCount, "requestLogNumber" + "_" + TimeUtil.getDate()));
				String passingRate = df.format((double) (un) / (double) (dn));
				String requestRate = df.format((double) (rn) / (double) (dn));
				jedis.hget(logNumberCount, "detailLogNumber" + "_" + TimeUtil.getDate());
				jedis.hset(logNumberCount, "passingRate", passingRate);
				jedis.hset(logNumberCount, "requestRate", requestRate);
			}
		});

	}

	// 将异常日志存储在redis的list中
	public static void saveExceptionLogToRedis(JavaDStream<RowLog> logDStream, final String redisDetailKey,
			final String listKey, final Integer listLimit) {
		logDStream.foreachRDD(new VoidFunction<JavaRDD<RowLog>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaRDD<RowLog> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<RowLog>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<RowLog> iterator) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						while (iterator.hasNext()) {
							RowLog log = iterator.next();
							RedisUtil redisUtil = new RedisUtil();
							redisUtil.saveExceptionLogToList(jedis, log, redisDetailKey, listKey, listLimit);
						}

					}
				});

			}
		});
	}

	// save detail data and request data to es
	public static void saveDataToES(JavaPairDStream<RequestLogs, Iterable<RowLog>> groupDStream) {
		groupDStream.foreachRDD(new VoidFunction<JavaPairRDD<RequestLogs, Iterable<RowLog>>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<RequestLogs, Iterable<RowLog>> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<RequestLogs, Iterable<RowLog>>>>() {

					private static final long serialVersionUID = 1L;

					@SuppressWarnings("unused")
					public void call(Iterator<Tuple2<RequestLogs, Iterable<RowLog>>> iterator) throws Exception {

						List<Map<String, Object>> requestPayList = new ArrayList<Map<String, Object>>();
						List<Map<String, Object>> detailPayList = new ArrayList<Map<String, Object>>();
						List<Map<String, Object>> requestAccountList = new ArrayList<Map<String, Object>>();
						List<Map<String, Object>> detailAccountList = new ArrayList<Map<String, Object>>();
						int detailCount =0;
						int requestCount =0;
						while (iterator.hasNext()) {
							Tuple2<RequestLogs, Iterable<RowLog>> tuple = iterator.next();
							RequestLogs rgroup = tuple._1;
							for (RowLog log : tuple._2) {

								// 存储detail数据

								Map<String, Object> detailMap = MapFactory
										.create(FieldEnum.callLocation.getEnName(), rgroup.getCl())
										.put(FieldEnum.version.getEnName(), log.getVersion())
										.put(FieldEnum.language.getEnName(), log.getLanguage())
										.put(FieldEnum.time.getEnName(), log.getTime())
										.put(FieldEnum.level.getEnName(), log.getLevel())
										.put(FieldEnum.host.getEnName(), log.getHost())
										.put(FieldEnum.location.getEnName(), log.getCodeLocation())
										.put(FieldEnum.requestId.getEnName(), log.getRequestId())
										.put(FieldEnum.logDetail.getEnName(), log.getDetail())
										.put(FieldEnum.startTime.getEnName(), rgroup.getStratTime())
										.put(FieldEnum.endTime.getEnName(), rgroup.getStopTime())
										.put(FieldEnum.errorCode.getEnName(), String.valueOf(rgroup.getErrorCode()))
										.put(FieldEnum.url.getEnName(), rgroup.getUrl())
										.put(FieldEnum.user.getEnName(), rgroup.getUserName())
										.put(FieldEnum.role.getEnName(), rgroup.getRole())
										.put(FieldEnum.ylpEnterpriseAccountNo.getEnName(), rgroup.getEntrepreneurId())
										.put(FieldEnum.ip.getEnName(), rgroup.getIp())
										.put(FieldEnum.browserType.getEnName(), rgroup.getBrowserType())
										.put(FieldEnum.browserVersion.getEnName(), rgroup.getBrowserVersion())
										.put(FieldEnum.mobile.getEnName(), rgroup.getMobileBrand())
										.put(FieldEnum.systemVersion.getEnName(), rgroup.getSystemVersion())
										.put(FieldEnum.mobileVersion.getEnName(), rgroup.getMobileVersion())
										.put(FieldEnum.networkType.getEnName(), rgroup.getNetwork()).getData();
								if(rgroup.getCl().equals("账户系统")){
									detailAccountList.add(detailMap);
								}else{
									detailPayList.add(detailMap);
								}
								
//								detailCount++;
//								//每1000条写入一次，增加写入速度
//								if(detailCount%1000==0){
//									try {
//										// 2、选择索引类型，传入类型和封装的参数
//										if (null != detailList && !detailList.isEmpty()) {
//											BulkResponse detailResponse = ESClient.preparePayBulkIndex(TypeEnum.detailType, detailList);
//											detailList = new ArrayList<Map<String, Object>>();
//										}
//									} catch (Exception e) {
//									}
//								}
							}
							// 1、封装数据

							Map<String, Object> requestmap = MapFactory
									.create(FieldEnum.requestId.getEnName(), rgroup.getRequestId())
									.put(FieldEnum.startTime.getEnName(), rgroup.getStratTime())
									.put(FieldEnum.endTime.getEnName(), rgroup.getStopTime())
									.put(FieldEnum.systemName.getEnName(), rgroup.getCl())
									.put(FieldEnum.interfaceName.getEnName(), rgroup.getAn())
									.put(FieldEnum.level.getEnName(), rgroup.getLevel())
									.put(FieldEnum.warnCount.getEnName(), String.valueOf(rgroup.getWarnCount()))
									.put(FieldEnum.errorCount.getEnName(), String.valueOf(rgroup.getErrorCount()))
									.put(FieldEnum.errorCode.getEnName(), String.valueOf(rgroup.getErrorCode()))
									.put(FieldEnum.logList.getEnName(), rgroup.getLogList())
									.put(FieldEnum.url.getEnName(), rgroup.getUrl())
									.put(FieldEnum.user.getEnName(), rgroup.getUserName())
									.put(FieldEnum.role.getEnName(), rgroup.getRole())
									.put(FieldEnum.ylpEnterpriseAccountNo.getEnName(), rgroup.getEntrepreneurId())
									.put(FieldEnum.ip.getEnName(), rgroup.getIp())
									.put(FieldEnum.browserType.getEnName(), rgroup.getBrowserType())
									.put(FieldEnum.browserVersion.getEnName(), rgroup.getBrowserVersion())
									.put(FieldEnum.mobile.getEnName(), rgroup.getMobileBrand())
									.put(FieldEnum.systemVersion.getEnName(), rgroup.getSystemVersion())
									.put(FieldEnum.mobileVersion.getEnName(), rgroup.getMobileVersion())
									.put(FieldEnum.networkType.getEnName(), rgroup.getNetwork()).getData();

							if(rgroup.getCl().equals("账户系统")){
								requestAccountList.add(requestmap);
							}else{
								requestPayList.add(requestmap);
							}
//							requestList.add(requestmap);
//							requestCount++;
//							if(requestCount%1000==0){
//								try {
//									// 2、选择索引类型，传入类型和封装的参数
//									if (null != requestList && !requestList.isEmpty()) {
//										BulkResponse requestResponse = ESClient.preparePayBulkIndex(TypeEnum.requestType,
//												requestList);
//										requestList = new ArrayList<Map<String, Object>>();
//									}
//								} catch (Exception e) {
//								}
//							}

						}

						try {
							// 2、选择索引类型，传入类型和封装的参数
							if (null != requestAccountList && !requestAccountList.isEmpty()) {
								BulkResponse requestResponse = ESClient.prepareAccountBulkIndex(TypeEnum.requestType,
										requestAccountList);
							}
						} catch (Exception e) {
						}

						try {
							// 2、选择索引类型，传入类型和封装的参数
							if (null != detailAccountList && !detailAccountList.isEmpty()) {
								BulkResponse detailResponse = ESClient.prepareAccountBulkIndex(TypeEnum.detailType, detailAccountList);

							}
						} catch (Exception e) {
						}
						
						try {
							// 2、选择索引类型，传入类型和封装的参数
							if (null != requestPayList && !requestPayList.isEmpty()) {
								BulkResponse requestResponse = ESClient.preparePayBulkIndex(TypeEnum.requestType,
										requestPayList);
							}
						} catch (Exception e) {
						}

						try {
							// 2、选择索引类型，传入类型和封装的参数
							if (null != detailPayList && !detailPayList.isEmpty()) {
								BulkResponse detailResponse = ESClient.preparePayBulkIndex(TypeEnum.detailType, detailPayList);

							}
						} catch (Exception e) {
						}

					}
				});

			}
		});

	}

	// request耗时统计
	public static void requestTimeConsuming(JavaPairDStream<RequestLogs, Iterable<RowLog>> requestGroupDStream,
			final String requestIdDetailKey, final String requestTimeCountKey) {
		JavaPairDStream<String, Integer> timeConsumingCount = requestGroupDStream
				.filter(new Function<Tuple2<RequestLogs, Iterable<RowLog>>, Boolean>() {

					private static final long serialVersionUID = 1L;

					public Boolean call(Tuple2<RequestLogs, Iterable<RowLog>> tuple) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						RequestLogs rGroup = tuple._1;
						String date = null;
						RowLog log = tuple._2.iterator().next();
						date = log.getTime().split(" ")[0].replace("-", "");
						String rkey = String.format(requestIdDetailKey, date);
						String requestId = rGroup.getRequestId();
						String st = rGroup.getStratTime();
						String et = rGroup.getStopTime();
						if (null == st) {
							st = jedis.hget(rkey, requestId + "_" + FieldEnum.startTime.getEnName());
						}
						if (null == et) {
							et = jedis.hget(rkey, requestId + "_" + FieldEnum.endTime.getEnName());
						}
						if (null != st && null != et) {
							return true;
						} else {
							return false;
						}

					}
				}).map(new Function<Tuple2<RequestLogs, Iterable<RowLog>>, String>() {

					private static final long serialVersionUID = 1L;

					public String call(Tuple2<RequestLogs, Iterable<RowLog>> tuple) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						RequestLogs rGroup = tuple._1;
						String date = null;
						RowLog log = tuple._2.iterator().next();
						date = log.getTime().split(" ")[0].replace("-", "");
						String rkey = String.format(requestIdDetailKey, date);
						String requestId = rGroup.getRequestId();
						String st = rGroup.getStratTime();
						String et = rGroup.getStopTime();
						if (null == st) {
							st = jedis.hget(rkey, requestId + "_" + FieldEnum.startTime.getEnName());
						}
						if (null == et) {
							et = jedis.hget(rkey, requestId + "_" + FieldEnum.endTime.getEnName());
						}
						String timeConsuming = TimeUtil.timeLevel(st, et);
						String sysName = rGroup.getCl();
						String key = sysName + "_" + timeConsuming;
						return key;
					}

				}).mapToPair(new PairFunction<String, String, Integer>() {

					private static final long serialVersionUID = 1L;

					public Tuple2<String, Integer> call(String s) throws Exception {

						return new Tuple2<String, Integer>(s, 1);
					}
				}).reduceByKey(new Function2<Integer, Integer, Integer>() {

					private static final long serialVersionUID = 1L;

					public Integer call(Integer v1, Integer v2) throws Exception {

						return v1 + v2;
					}
				});

		timeConsumingCount.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, Integer> rdd) throws Exception {

				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Integer>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<String, Integer>> iterator) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						RedisUtil redisUtil = new RedisUtil();
						while (iterator.hasNext()) {
							Tuple2<String, Integer> tuple = iterator.next();
							String countKey = tuple._1;
							Integer count = tuple._2;
							redisUtil.hmIadd(jedis, requestTimeCountKey, countKey, count, null);
						}

					}
				});
			}

		});
	}

	// 日志质量分析，统计可用日志总占比
	public static void logQualityCount(JavaPairInputDStream<String, String> logEventsDStream,
			final String logNumberCount) {

		// 使用foreachRDD函数，对获取到的DSteam数据里面的每个RDD执行操作
		logEventsDStream.foreachRDD(new VoidFunction<JavaPairRDD<String, String>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, String> rdd) throws Exception {
				JavaRDD<String> logEventsRDD = rdd.map(new Function<Tuple2<String, String>, String>() {

					private static final long serialVersionUID = 1L;

					public String call(Tuple2<String, String> tuple) throws Exception {
						return tuple._2;
					}
				});
				// 将从kafka获取到的数据转换成list
				List<String> list = logEventsRDD.collect();
				List<String> list2 = new ArrayList<String>();
				List<Map<String, Object>> eslist = new ArrayList<Map<String, Object>>();

				Map<String, String> rmap = new HashMap<String, String>();
				// 将error和warn日志储存到redis中
				JedisCluster jedis = JedisHelper.dataCluster();

				// 在遍历日志的同时统计原始日志总数
				// detailLogNumber，格式符合规范的日志总数userfulLogNumber，request的日志总数requestLogNumber
				Integer detailLogNumber = 0;
				Integer userfulLogNumber = 0;
				Integer requestLogNumber = 0;
				// 封装数据，方便存储
				for (String s : list) {
					detailLogNumber = detailLogNumber + 1;
					try {
						String[] data = s.split("\\|");
						RowLog log = new RowLog();
						if (data.length > 4) {
							log.setVersion(data[0]);
							log.setLanguage(data[1]);
							log.setTime(data[2]);
							log.setLevel(data[3]);
							log.setHost(data[4]);
							log.setCodeLocation(data[5]);
							log.setRequestId(data[6]);
							String detail = "";

							if (data.length > 7) {
								for (int i = 7; i < data.length; i++)
									detail = detail + data[i] + "|";
							}
							log.setDetail(detail);
							userfulLogNumber = userfulLogNumber + 1;
						}
						// 将error与warn级别的数据储存到redis中
						if (log.getRequestId().length() > 5) {
							requestLogNumber = requestLogNumber + 1;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				// 遍历list后将得到的统计数写入到redis中
				DecimalFormat df = new DecimalFormat("0.00");
				RedisUtil redisUtil = new RedisUtil();
				redisUtil.hmIadd(jedis, logNumberCount, "detailLogNumber" + "_" + TimeUtil.getDate(), detailLogNumber,
						null);
				redisUtil.hmIadd(jedis, logNumberCount, "userfulLogNumber" + "_" + TimeUtil.getDate(), userfulLogNumber,
						null);
				redisUtil.hmIadd(jedis, logNumberCount, "requestLogNumber" + "_" + TimeUtil.getDate(), requestLogNumber,
						null);
				Integer dn = Integer.valueOf(jedis.hget(logNumberCount, "detailLogNumber" + "_" + TimeUtil.getDate()));
				Integer un = Integer.valueOf(jedis.hget(logNumberCount, "userfulLogNumber" + "_" + TimeUtil.getDate()));
				Integer rn = Integer.valueOf(jedis.hget(logNumberCount, "requestLogNumber" + "_" + TimeUtil.getDate()));
				String passingRate = df.format((double) (un) / (double) (dn));
				String requestRate = df.format((double) (rn) / (double) (dn));
				jedis.hget(logNumberCount, "detailLogNumber" + "_" + TimeUtil.getDate());
				jedis.hset(logNumberCount, "passingRate", passingRate);
				jedis.hset(logNumberCount, "requestRate", requestRate);
			}
		});

	}

}