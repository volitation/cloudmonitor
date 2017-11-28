package com.ylp.cloudmonitor.spark.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.cloudmonitor.spark.model.RowLog;

import scala.Tuple2;

public class test02 implements Serializable{
	public static void main(String[] args) {
		SparkConf conf =new SparkConf().setAppName("test").setMaster("local[2]");
		JavaSparkContext sc=new JavaSparkContext(conf);
		List<String> list =new ArrayList<String>();
		list.add("v1|J|2016-12-08 14:10:13,588| INFO|172.16.8.81|TraceService:67|adee723c05f78044a509aa5021d4f55b6c06|request started:http://dev-pay-api.vipzhuang.cn/income/incomeSource?beginTime=2016-12-05+00%3A00%3A00&endTime=2016-12-11+23%3A59%3A59");
	    list.add("v1|J|2016-12-08 14:10:13,588| INFO|172.16.8.81|TraceService:97|adee723c05f78044a509aa5021d4f55b6c06|request started:172.16.10.1|Chrome|54.0.2840.98|MAC OS X|null|null|null|null|null|");
	    list.add("v1|J|2016-12-08 14:10:13,591| INFO|172.16.8.81|PayInterceptor:51|adee723c05f78044a509aa5021d4f55b6c06|login user info:");
	    list.add("v1|J|2016-12-08 14:10:13,596| INFO|172.16.8.81|IncomeController:126|adee723c05f78044a509aa5021d4f55b6c06|?跺.?ユ?缁..杩..缁..?..incomeSourceInfoList=[]");
	    list.add("v1|J|2016-12-08 14:10:13,597| INFO|172.16.8.81|TraceService:74|adee723c05f78044a509aa5021d4f55b6c06|request completed");
	    System.out.println(list.get(0).split("\\|")[6]);
	    JavaRDD<String>rdd =sc.parallelize(list);
	    
	    JavaPairRDD<String, Iterable<RowLog>>rdd2=rdd.mapToPair(new PairFunction<String, String, RowLog>() {

			private static final long serialVersionUID = 1L;

			public Tuple2<String, RowLog> call(String s) throws Exception {
				String[] data = s.split("\\|");
				RowLog log = new RowLog();
				log.setVersion(!data[0].equals(" ")? data[0] : "nullVersion");
				log.setLanguage(!data[1].equals(" ")? data[1] : "nullLanguage");
				log.setTime(!data[2].equals(" ")? data[2] : "nullTime");
				log.setLevel(!data[3].equals(" ") ? data[3] : "nullLevel");
				log.setHost(!data[4].equals(" ")? data[4] : "nullHost");
				log.setCodeLocation(!data[5].equals(" ") ? data[5] : "nullCodeLocation");
				log.setRequestId(!data[6].equals(" ")? data[6] : "nullRequestId");
				String detail = "";
				if(data.length>7){
					for(int i =7;i<data.length;i++)
					detail=detail  +data[i]+ "|";
				}
				log.setDetail(!detail.isEmpty()? data[7] : "nullDetail");
				return new Tuple2<String, RowLog>(log.getRequestId(), log);
			}
		}).groupByKey();
	    
	    	rdd2.map(new Function<Tuple2<String,Iterable<RowLog>>, RequestLogs>() {


			private static final long serialVersionUID = 1L;

			public RequestLogs call(Tuple2<String, Iterable<RowLog>> tuple) throws Exception {
				String requestId =tuple._1;
				RequestLogs group=new RequestLogs();
				group.setRequestId(requestId);
				for(RowLog log: tuple._2){
					String detail = log.getDetail();
					String[] detaildata =detail.split(":");
					if(detaildata[0].equals("request started")){
						group.setStratTime(log.getTime());
						if(detaildata[1].equals("http")){
							group.setUrl(detaildata[1]+":"+detaildata[2]);
						}
					}else if(detaildata[0].equals("request completed")){
						group.setStopTime(log.getTime());
					}
				}
				return group;
			}
		})
	    .foreach(new VoidFunction<RequestLogs>() {
			
			private static final long serialVersionUID = 1L;

			public void call(RequestLogs t) throws Exception {
				System.out.println("StratTime : " + t.getStratTime());
				System.out.println("StopTime : " + t.getStopTime());
				System.out.println("url : " + t.getUrl());
				System.out.println("request  : " + t.getRequestId());
			}
		});
	    
	    
	}

}
