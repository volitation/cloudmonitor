package com.ylp.cloudmonitor.spark;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import kafka.serializer.StringDecoder;
import scala.Tuple2;

public class SparkStreamingTest implements Serializable {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		
		//生成spark
		SparkConf conf =new SparkConf().setAppName("SparkStreamingTest");
		JavaStreamingContext jssc = new JavaStreamingContext(
				conf, Durations.seconds(30));  
		
		// 创建针对Kafka数据来源的输入DStream（离线流，代表了一个源源不断的数据来源，抽象）
		// 选用kafka direct api（很多好处，包括自己内部自适应调整每次接收数据量的特性，等等） 
		
		// 构建kafka参数map
		// 主要要放置的就是，你要连接的kafka集群的地址（broker集群的地址列表）
		Map<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", "172.16.8.41:6667,172.16.8.42:6667,172.16.8.43:6667"  );
		
		// 构建topic set
		String kafkaTopics = "TestTopic1";
		String[] kafkaTopicsSplited = kafkaTopics.split(",");  
		
		Set<String> topics = new HashSet<String>();
		for(String kafkaTopic : kafkaTopicsSplited) {
			topics.add(kafkaTopic);
		}
		
		// 基于kafka direct api模式，构建出了针对kafka集群中指定topic的输入DStream
		// 两个值，val1，val2；val1没有什么特殊的意义；val2中包含了kafka topic中的一条一条的实时日志数据
		JavaPairInputDStream<String, String> testDStream = KafkaUtils.createDirectStream(
				jssc, 
				String.class, 
				String.class, 
				StringDecoder.class, 
				StringDecoder.class, 
				kafkaParams, 
				topics);
		testDStream.foreachRDD(new Function<JavaPairRDD<String,String>, Void>() {

			private static final long serialVersionUID = 1L;

			public Void call(JavaPairRDD<String, String> rdd) throws Exception {
				rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String,String>>>() {

					private static final long serialVersionUID = 1L;

					public void call(Iterator<Tuple2<String, String>> iterator) throws Exception {
						
						while(iterator.hasNext()) {
							Tuple2<String, String> tuple = iterator.next();
							System.out.println(tuple._2);
							
						}
					}
				});
				return null;
			}
		});
		
		// 构建完spark streaming上下文之后，记得要进行上下文的启动、等待执行结束、关闭
		jssc.start();
		jssc.awaitTermination();
		jssc.close();
	}
}
