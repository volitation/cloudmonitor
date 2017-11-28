package com.ylp.cloudmonitor.spark.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import kafka.serializer.StringDecoder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scala.Tuple2;

public class SparkKafkaTest implements Serializable {
	public static void main(String[] args) {
		int duration = 10;
		String kafkaTopics = "flume";
		String metadata_broker_list = "172.16.8.41:6667";

		SparkConf conf = new SparkConf().setAppName("SparkKafkaTest");

		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(duration));

		HashMap<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", metadata_broker_list);

		String[] kafkaTopicsSplited = kafkaTopics.split(",");

		Set<String> topics = new HashSet<String>();
		for (String kafkaTopic : kafkaTopicsSplited) {
			topics.add(kafkaTopic);
		}

		JavaPairInputDStream<String, String> kafkaDStream = KafkaUtils.createDirectStream(jssc, String.class,
				String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);
		JavaPairDStream<String, Integer> count = kafkaDStream
				.mapToPair(new PairFunction<Tuple2<String, String>, String, Integer>() {

					private static final long serialVersionUID = 1L;

					public Tuple2<String, Integer> call(Tuple2<String, String> tuple) throws Exception {

						return new Tuple2<String, Integer>( tuple._2, 1);
					}
				}).reduceByKey(new Function2<Integer, Integer, Integer>() {

					private static final long serialVersionUID = 1L;

					public Integer call(Integer v1, Integer v2) throws Exception {
						// TODO Auto-generated method stub
						return v1 + v2;
					}
				});

		count.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>() {

			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, Integer> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Integer>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<String, Integer>> iterator) throws Exception {
						JedisPool pool = new JedisPool(new JedisPoolConfig(), "172.16.8.41", 6379, 10000);
						Jedis jedis = pool.getResource();
						while (iterator.hasNext()) {
							Tuple2<String, Integer> tuple = iterator.next();
							Map<String, String> cMap = new HashMap<String, String>();
							cMap.put(tuple._1, String.valueOf(tuple._2));
							jedis.hmset("Test:flume-kafka-spark-redis", cMap);
						}
						jedis.close();
						pool.destroy();

					}
				});

			}

		});

		jssc.start();
		jssc.awaitTermination();
		jssc.close();
	}

}
