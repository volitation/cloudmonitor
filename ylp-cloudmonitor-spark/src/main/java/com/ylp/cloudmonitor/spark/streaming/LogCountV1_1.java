package com.ylp.cloudmonitor.spark.streaming;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
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
import com.ylp.cloudmonitor.spark.model.util.RowLogUtil;
import com.ylp.cloudmonitor.spark.sparkdao.factory.SparkDAOFactory;
import com.ylp.cloudmonitor.spark.util.ExceptionLogUtil;
import com.ylp.cloudmonitor.spark.util.TimeUtil;
import com.ylp.common.enums.FieldEnum;
import com.ylp.common.enums.LogEnum;
import com.ylp.common.enums.RedisEnum;
import com.ylp.common.enums.TypeEnum;
import com.ylp.common.es.api.ESClient;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.factory.MapFactory;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.factory.RedisDAOFactory;

import kafka.serializer.StringDecoder;
import net.sf.json.JSONArray;
import redis.clients.jedis.JedisCluster;
import scala.Tuple2;

public class LogCountV1_1 implements Serializable {

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
		// 设置spark checkpoint
		String checkpoint = args[3];
		// =====================================================//
		// 构建sparkcontext
		SparkConf conf = new SparkConf().setAppName("LogCount_V1.1");
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

		// 拉取系统名称映射表
		JedisCluster jedis = JedisHelper.dataCluster();
		List<String> sysNameList = jedis.lrange(RedisKey.SYSTEM_LIST, 0, -1);
		JSONArray sysNamearray = JSONArray.fromObject(sysNameList);
		// 拉取接口名映射表
		List<String> apiNameList = jedis.lrange(RedisKey.API_LIST, 0, -1);
		JSONArray apiNamearray = JSONArray.fromObject(apiNameList);

		// 处理数据
		JavaDStream<RowLog> logDStream = dataToLog(logEventsDStream);
		saveRequestCache(logDStream);
		JavaPairDStream<RequestLogs, Iterable<RowLog>> requestGroupDStream = requestGroup(logDStream).cache();
		saveExceptionLogToRedis(requestGroupDStream, 500);
		saveDataToES(requestGroupDStream);
		RequestCount(requestGroupDStream);
		requestLevelCount(requestGroupDStream);
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
				return RowLogUtil.filterRowLog(tuple._2);
			}
		}).map(new Function<Tuple2<String, String>, RowLog>() {

			private static final long serialVersionUID = 1L;

			public RowLog call(Tuple2<String, String> tuple) throws Exception {

				return RowLogUtil.setRowLog(tuple._2);

			}
		});
		return logDStream;

	}

	// 日志跨批次处理，将不完整日志缓存到redis中
	public static void saveRequestCache(JavaDStream<RowLog> logDStream) {

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

		// 日志跨批次处理，将不完整的日志缓存到redis中
		logEventsGroupDStream.foreachRDD(new VoidFunction<JavaPairRDD<String, Iterable<RowLog>>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, Iterable<RowLog>> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Iterable<RowLog>>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<String, Iterable<RowLog>>> iterator) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						while (iterator.hasNext()) {
							try {
								SparkDAOFactory.getRequestCache().setRequestCache(jedis, iterator.next());
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				});

			}
		});

	}

	// 日志跨批次处理，得到完整日志
	public static JavaPairDStream<RequestLogs, Iterable<RowLog>> requestGroup(JavaDStream<RowLog> logDStream) {

		// 使用filter过滤掉没有requestid的日志数据，然后对日志进行分组
		JavaPairDStream<String, Iterable<RowLog>> logGroupDStream = logDStream.filter(new Function<RowLog, Boolean>() {

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
		}).groupByKey().filter(new Function<Tuple2<String, Iterable<RowLog>>, Boolean>() {

			private static final long serialVersionUID = 1L;

			public Boolean call(Tuple2<String, Iterable<RowLog>> tuple) throws Exception {

				return SparkDAOFactory.getRequestCache().filterRequestCache(tuple);
			}
		});

		// 获取日志分批次处理中缓存的数据，得到完整的request logs
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

	// 将异常日志存储在redis的list中
	public static void saveExceptionLogToRedis(JavaPairDStream<RequestLogs, Iterable<RowLog>> requestGroup,
			final Integer listLimit) {
		requestGroup.foreachRDD(new VoidFunction<JavaPairRDD<RequestLogs, Iterable<RowLog>>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<RequestLogs, Iterable<RowLog>> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<RequestLogs, Iterable<RowLog>>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<RequestLogs, Iterable<RowLog>>> iterator) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						while (iterator.hasNext()) {
							Tuple2<RequestLogs, Iterable<RowLog>> tuple = iterator.next();

							RedisDAOFactory.getLastExceptionLogsDAO().saveExceptionLogToList(jedis,
									ExceptionLogUtil.getExceptionLogInfo(tuple._1), listLimit);
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
						int detailCount = 0;
						int requestCount = 0;
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
								if (rgroup.getCl().equals("账户服务")) {
									detailAccountList.add(detailMap);
								} else {
									detailPayList.add(detailMap);
								}

							}
							// 1、封装数据

							Map<String, Object> requestmap = MapFactory
									.create(FieldEnum.requestId.getEnName(), rgroup.getRequestId())
									.put(FieldEnum.startTime.getEnName(), rgroup.getStratTime())
									.put(FieldEnum.endTime.getEnName(), rgroup.getStopTime())
									.put(FieldEnum.usingTime.getEnName(), rgroup.getUseTime())
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
									.put(FieldEnum.apiId.getEnName(), rgroup.getApiId())
									.put(FieldEnum.sysBizId.getEnName(), rgroup.getSystemId())
									.put(FieldEnum.networkType.getEnName(), rgroup.getNetwork()).getData();

							if (rgroup.getCl().equals("账户服务")) {
								requestAccountList.add(requestmap);
							} else {
								requestPayList.add(requestmap);
							}
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
								BulkResponse detailResponse = ESClient.prepareAccountBulkIndex(TypeEnum.detailType,
										detailAccountList);

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
								BulkResponse detailResponse = ESClient.preparePayBulkIndex(TypeEnum.detailType,
										detailPayList);

							}
						} catch (Exception e) {
						}

					}
				});

			}
		});

	}

	// 统计request级别
	public static void requestLevelCount(JavaPairDStream<RequestLogs, Iterable<RowLog>> requestGroup) {
		JavaPairDStream<String, Integer> requestLevelCount = requestGroup
				.map(new Function<Tuple2<RequestLogs, Iterable<RowLog>>, String>() {

					private static final long serialVersionUID = 1L;

					public String call(Tuple2<RequestLogs, Iterable<RowLog>> tuple) throws Exception {
						String systemName = tuple._1.getCl();
						String level = tuple._1.getLevel();
						String date = tuple._1.getStratTime().split(" ")[0].replace("-", "");
						String key = date + "|" + systemName + "_" + level;
						return key;
					}
				}).mapToPair(new PairFunction<String, String, Integer>() {

					private static final long serialVersionUID = 1L;

					public Tuple2<String, Integer> call(String s) throws Exception {
						// TODO Auto-generated method stub
						return new Tuple2<String, Integer>(s, 1);
					}
				}).reduceByKey(new Function2<Integer, Integer, Integer>() {

					private static final long serialVersionUID = 1L;

					public Integer call(Integer v1, Integer v2) throws Exception {
						// TODO Auto-generated method stub
						return v1 + v2;
					}
				});
		requestLevelCount.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, Integer> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Integer>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<String, Integer>> iterator) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						while (iterator.hasNext()) {
							Tuple2<String, Integer> tuple = iterator.next();
							String[] data = tuple._1.split("\\|");
							String field = data[1];
							String date = data[0];
							Integer value = tuple._2;
							RedisDAOFactory.getPlatfromExceptionCountDAO().add(jedis, date, field, value);
						}

					}
				});

			}
		});

	}

	// 统计平台总体情况

	public static void RequestCount(JavaPairDStream<RequestLogs, Iterable<RowLog>> requestGroup) {

		/**
		 * 统计按requestId聚合后的数据 统计维度平台，系统，接口
		 * 统计类别总调用次数，错误数，警告数，总耗时，最高耗时，最低耗时，最高调用频次等类别
		 */
		// 两种思路
		// 一是按平台、系统、接口三种维度分别统计
		// 二是只统计接口维度，将数据累加到系统和平台维度上
		// 一的好处是redis写入次数少，二的好处是统计次数少，在此选择第二种思路

		// =======================================================================//
		// 统计总调用次数，错误数，警告数
		JavaPairDStream<String, Integer> requestNumberCount = requestGroup
				.map(new Function<Tuple2<RequestLogs, Iterable<RowLog>>, String>() {

					private static final long serialVersionUID = 1L;

					public String call(Tuple2<RequestLogs, Iterable<RowLog>> tuple) throws Exception {
						RequestLogs rgroup = tuple._1;
						String systemId = rgroup.getSystemId();
						String apiId = rgroup.getApiId();
						String logType = rgroup.getLevel();
						String date = rgroup.getStratTime().split(" ")[0].replace("-", "");
						String frquency = rgroup.getFrequency();
						String key = systemId + "|" + apiId + "|" + logType + "|" + date + "|" + frquency;
						return key;
					}
				}).mapToPair(new PairFunction<String, String, Integer>() {

					private static final long serialVersionUID = 1L;

					public Tuple2<String, Integer> call(String s) throws Exception {
						// TODO Auto-generated method stub
						return new Tuple2<String, Integer>(s, 1);
					}
				}).reduceByKey(new Function2<Integer, Integer, Integer>() {

					private static final long serialVersionUID = 1L;

					public Integer call(Integer v1, Integer v2) throws Exception {
						// TODO Auto-generated method stub
						return v1 + v2;
					}
				});

		requestNumberCount.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, Integer> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Integer>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<String, Integer>> iterator) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						while (iterator.hasNext()) {
							Tuple2<String, Integer> tuple = iterator.next();
							String systemId = tuple._1.split("\\|")[0];
							String apiId= tuple._1.split("\\|")[1];
							String logType = tuple._1.split("\\|")[2];
							String date = tuple._1.split("\\|")[3];
							String frquency = tuple._1.split("\\|")[4];
							Integer value = tuple._2;

							// 总调用次数统计
							RedisDAOFactory.getAPIRequestCountDAO().add(jedis, systemId, apiId, date,
									RedisEnum.request_count.getEnName(), value);
							RedisDAOFactory.getSystemRequestCountDAO().add(jedis, systemId, date,
									RedisEnum.request_count.getEnName(), value);
							RedisDAOFactory.getPlatfromRequestCountDAO().add(jedis, date,
									RedisEnum.request_count.getEnName(), value);
							// 错误数，警告数
							if (logType.equals(LogEnum.warn.getEnName())) {
								RedisDAOFactory.getAPIRequestCountDAO().add(jedis, systemId, apiId, date,
										RedisEnum.warn_number.getEnName(), value);
								RedisDAOFactory.getSystemRequestCountDAO().add(jedis, systemId, date,
										RedisEnum.warn_number.getEnName(), value);
								RedisDAOFactory.getPlatfromRequestCountDAO().add(jedis, date,
										RedisEnum.warn_number.getEnName(), value);
							}
							if (logType.equals(LogEnum.error.getEnName())) {
								RedisDAOFactory.getAPIRequestCountDAO().add(jedis, systemId, apiId, date,
										RedisEnum.error_number.getEnName(), value);
								RedisDAOFactory.getSystemRequestCountDAO().add(jedis, systemId, date,
										RedisEnum.error_number.getEnName(), value);
								RedisDAOFactory.getPlatfromRequestCountDAO().add(jedis, date,
										RedisEnum.error_number.getEnName(), value);
							}

							// 各频次统计
							RedisDAOFactory.getAPIRequestCountDAO().add(jedis, systemId, apiId, date, frquency, value);
							RedisDAOFactory.getSystemRequestCountDAO().add(jedis, systemId, date, frquency, value);
							RedisDAOFactory.getPlatfromRequestCountDAO().add(jedis, date, frquency, value);

							// 计算最大频次
							RedisDAOFactory.getPlatfromRequestCountDAO().setMaxFrquency(jedis, date, frquency);
							RedisDAOFactory.getSystemRequestCountDAO().setMaxFrquency(jedis, systemId, date, frquency);
							RedisDAOFactory.getAPIRequestCountDAO().setMaxFrquency(jedis, systemId, apiId, date,
									frquency);
						}
					}
				});

			}
		});
		// =======================================================================//
		// 统计耗时
		/**
		 * 
		 * */
		JavaPairDStream<String, Iterable<Double>> requestTimeGroup = requestGroup
				.map(new Function<Tuple2<RequestLogs, Iterable<RowLog>>, String>() {

					private static final long serialVersionUID = 1L;

					public String call(Tuple2<RequestLogs, Iterable<RowLog>> tuple) throws Exception {
						RequestLogs rgroup = tuple._1;
						String systemId = rgroup.getSystemId();
						String apiId = rgroup.getApiId();
						String date = rgroup.getStratTime().split(" ")[0].replace("-", "");
//						String usingTime = rgroup.getUseTime();
						//在日志聚合时直接计算usingtime会有个别耗时计算出错，原因暂未找到，所以直接在使用时计算
						Double usingTime = TimeUtil.useTime(rgroup.getStratTime(), rgroup.getStopTime());
						String key = systemId + "_" + apiId + "_" + date + "_" + usingTime;
						return key;
					}
				}).mapToPair(new PairFunction<String, String, Double>() {

					private static final long serialVersionUID = 1L;

					public Tuple2<String, Double> call(String s) throws Exception {
						String[] data = s.split("_");
						String systemId = data[0];
						String apiId = data[1];
						String date = data[2];
						Double usingTime = 0.0;
						try {
							usingTime = Double.valueOf(data[3]);
						} catch (Exception e) {
						}
						String key = systemId + "_" + apiId + "_" + date;
						return new Tuple2<String, Double>(key, usingTime);
					}
				}).groupByKey();

		requestTimeGroup.foreachRDD(new VoidFunction<JavaPairRDD<String, Iterable<Double>>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, Iterable<Double>> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Iterable<Double>>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<String, Iterable<Double>>> iterator) throws Exception {
						JedisCluster jedis = JedisHelper.dataCluster();
						while (iterator.hasNext()) {
							Tuple2<String, Iterable<Double>> tuple = iterator.next();
							String[] data = tuple._1.split("_");
							String systemName = data[0];
							String apiName= data[1];
							String date = data[2];
							Map<String, Double> tMap = TimeUtil.getRequestTimeGroup(tuple);
							Double maxTime = tMap.get(RedisEnum.max_using_time.getEnName());
							Double minTime = tMap.get(RedisEnum.min_using_time.getEnName());
							Double totalTime = tMap.get(RedisEnum.total_using_time.getEnName());

							//总耗时统计
							RedisDAOFactory.getPlatfromRequestCountDAO().addfloat(jedis, date,
									RedisEnum.total_using_time.getEnName(), totalTime);
							RedisDAOFactory.getSystemRequestCountDAO().addfloat(jedis, systemName, date,
									RedisEnum.total_using_time.getEnName(), totalTime);
							RedisDAOFactory.getAPIRequestCountDAO().addfloat(jedis, systemName, apiName, date,
									RedisEnum.total_using_time.getEnName(), totalTime);
							
							//最高耗时
							RedisDAOFactory.getPlatfromRequestCountDAO().setMaxUsingTime(jedis, date, maxTime);
							RedisDAOFactory.getSystemRequestCountDAO().setMaxUsingTime(jedis, systemName, date, maxTime);
							RedisDAOFactory.getAPIRequestCountDAO().setMaxUsingTime(jedis, systemName, apiName, date, maxTime);
							
							//最低耗时
							RedisDAOFactory.getPlatfromRequestCountDAO().setMinUsingTime(jedis, date, minTime);
							RedisDAOFactory.getSystemRequestCountDAO().setMinUsingTime(jedis, systemName, date, minTime);
							RedisDAOFactory.getAPIRequestCountDAO().setMinUsingTime(jedis, systemName, apiName, date, minTime);
						}

					}
				});

			}
		});
		
		// =======================================================================//
	
	}
}
