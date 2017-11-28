package com.ylp.cloudmonitor.spark.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.ylp.cloudmonitor.spark.model.RowLog;

import kafka.serializer.StringDecoder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scala.Tuple2;

public class spliceLogTest {

	public static void main(String[] args) {
		// 输入参数
		// 第一个参数为SparkStreaming统计的时间间隔，以秒为单位
		int duration = 10;
		// 第二个参数为kafkaTopics
		String kafkaTopics = "TestTopic1";
		// 第三个参数为kafka集群（kafkahosts1:port,kafkahosts2:port）
		String metadata_broker_list = "172.16.8.41:6667,172.16.8.42:6667,172.16.8.43:6667";
		// 第四个参数为redis集群:端口号
		String redishosts = "172.16.8.41:6379";
		// =====================================================//
		// 构建sparkcontext
		SparkConf conf = new SparkConf().setAppName("LogCount");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(duration));
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
		spliceLog(logEventsDStream, redishosts, "RequestIdLogCount", "RequestIdSortedSet");

		// ======================================================//
		// 关闭SparkStreamingContext
		jssc.start();
		jssc.awaitTermination();
		jssc.close();
	}

	// spliceLog将日志中换行的信息拼接起来再存储到redis中
	public static void spliceLog(JavaPairInputDStream<String, String> logEventsDStream, String redishosts,
			final String redisKey, final String sortedSetKey) {
		final String redishost = redishosts.split(":")[0];
		final Integer redisport = Integer.valueOf(redishosts.split(":")[1]);
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
				// 遍历list的元素，将换行的日志拼接到原日志中
				for (String line : list) {
					String[] data = line.split("\\|");
					if (data.length > 4) {
						list2.add(line);
					} else {
						Integer i = list2.size();
						if (i > 0) {
							String old = list2.get(i - 1);
							list2.set(i - 1, old + '\n' + line);
						}
					}
				}

				@SuppressWarnings("resource")
				// 调用redis中的连接池，将error和warn日志储存到redis中
				JedisPool pool = new JedisPool(new JedisPoolConfig(), redishost, redisport, 100000);
				Jedis jedis = pool.getResource();
				for (String s : list2) {
					try {
						String[] data = s.split("\\|");
						RowLog log = new RowLog();
						if (data.length > 4) {
							log.setVersion(!data[0].isEmpty() ? data[0] : "nullVersion");
							log.setLanguage(!data[1].isEmpty() ? data[1] : "nullLanguage");
							log.setTime(!data[2].isEmpty() ? data[2] : "nullTime");
							log.setLevel(!data[3].isEmpty() ? data[3] : "nullLevel");
							log.setHost(!data[4].isEmpty() ? data[4] : "nullHost");
							log.setCodeLocation(!data[5].isEmpty() ? data[5] : "nullCodeLocation");
							log.setRequestId(!data[6].isEmpty() ? data[6] : "nullRequestId");
							String detail = "";
							if (data.length > 7) {
								for (int i = 7; i < data.length; i++)
									detail = detail + data[i] + "|";
							}
							log.setDetail(detail);
						}
						if (log.getRequestId().length() > 5) {
							// 将error和warn级别的日志详情储存到相应的requestid的hashmap中
							if (log.getLevel().replace(" ", "").equals("ERROR")
									| log.getLevel().replace(" ", "").equals("WARN")) {
								String hour = log.getTime().split(":")[0].replace(" ", "-");
								String rkey = hour + "|" + redisKey;
								String key = log.getRequestId() + "|" + log.getLevel().replace(" ", "");
								jedis.set("test", "test");
								if (jedis.hget(rkey, key) != null) {
									String oldvalue = jedis.hget(rkey, key);
									String c = log.toString(log) + '\n' + oldvalue;
									Map<String, String> datecMap = new HashMap<String, String>();
									datecMap.put(key, c);
									jedis.hmset(rkey, datecMap);
									jedis.expire(rkey, 7200);
								} else {
									Map<String, String> cMap = new HashMap<String, String>();
									cMap.put(key, log.toString(log));
									jedis.hmset(rkey, cMap);
									jedis.expire(rkey, 7200);
								}

								// 将error和warn级别的日志的requestid储存到一个sortedset中，按照时间排序
								String time = log.getTime().replace("-", "").replace(" ", "").replace(":", "")
										.replace(",", "");
								jedis.zadd(hour + "|" + sortedSetKey, Double.valueOf(time),
										redisKey + "|" + log.getRequestId());
							}
						}
					} catch (Exception e) {
					}

				}
				jedis.close();
				pool.destroy();

			}
		});

	}

}
