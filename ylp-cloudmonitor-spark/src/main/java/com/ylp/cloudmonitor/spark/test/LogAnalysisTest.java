package com.ylp.cloudmonitor.spark.test;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import net.sf.json.JSONObject;
import scala.Tuple2;
import scala.reflect.internal.Trees.New;

public class LogAnalysisTest {
    public static void main(String[] args) {
        SparkConf conf =new SparkConf().setAppName("test").setMaster("local[2]");
        JavaSparkContext sc=new JavaSparkContext(conf);
        JavaRDD<String> rdd=sc.textFile("C:\\Users\\Administrator\\Desktop\\ttt.txt");
        JavaRDD<String>rdd2=rdd.filter(new Function<String, Boolean>() {

            private static final long serialVersionUID = 1L;

            public Boolean call(String s) throws Exception {
                // TODO Auto-generated method stub
                return logfilter(s);
            }
        }).map(new Function<String, String>() {

            private static final long serialVersionUID = 1L;

            public String call(String s) throws Exception {
                // TODO Auto-generated method stub
                return getDataLog(s);
            }
        })	;
        
        JavaPairRDD<String, Integer> rdd3=rdd2.mapToPair(new PairFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String, Integer> call(String t) throws Exception {
				String key =getReduceKey(t);
				return new Tuple2<String, Integer>(key,1);
			}
		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Integer call(Integer v1, Integer v2) throws Exception {
				// TODO Auto-generated method stub
				return v1+v2;
			}
		});
        rdd3.foreachPartition(new VoidFunction<Iterator<Tuple2<String,Integer>>>() {

			private static final long serialVersionUID = 1L;

			public void call(Iterator<Tuple2<String, Integer>> iterator) throws Exception {
				while(iterator.hasNext()){
					Tuple2<String, Integer> tuple =iterator.next();
					String key =tuple._1;
					Integer count = tuple._2;
					System.out.println(key + " : "+count);
				}
				
			}
		});
        

    }

    private static Boolean logfilter(String s){
        String pattern= "\\u67e5\\u8be2\\u8d26\\u6237\\u5bc6\\u7801\\u6210\\u529f\\u007c<DataSet:\\[(\\S+)\\]>";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);
        return m.find();
    }

    private static String getDataLog(String s){
        String pattern= ".*\\u67e5\\u8be2\\u8d26\\u6237\\u5bc6\\u7801\\u6210\\u529f\\u007c<DataSet:\\[(\\S+)\\]>";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);
        if(m.find()){
            String data =m.group().replace("<DataSet:[", "").replace("]>", "");
            return data;
        }else{
            return null;
        }

    }
    private static String getReduceKey(String s){
        String pattern= "\\{(\\S+)\\}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);
        if(m.find()){
            String []datas =m.group().replace("{", "").replace("}", "").split(",");
            String value=null;
            String id =null;
            for(String data:datas){
                if(data.split(":")[0].equals("acct_value")){
                    value=data.split(":")[1];
                }
                if(data.split(":")[0].equals("acct_id")){
                    id=data.split(":")[1];
                }
            }
            String key =id +"_"+value;
            return key;
        }else{
            return null;
        }

    }
}
