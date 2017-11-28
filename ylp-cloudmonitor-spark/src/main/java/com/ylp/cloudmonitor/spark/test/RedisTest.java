package com.ylp.cloudmonitor.spark.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ylp.cloudmonitor.spark.util.TimeUtil;
import com.ylp.common.entity.ApiInfo;
import com.ylp.common.enums.FieldEnum;
import com.ylp.common.enums.RedisEnum;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.factory.RedisDAOFactory;

import net.sf.json.JSONArray;
import redis.clients.jedis.JedisCluster;

public class RedisTest {
	public static void main(String[] args) {

		// SparkConf conf = new
		// SparkConf().setAppName("test").setMaster("local[2]");
		// JavaSparkContext sc =new JavaSparkContext(conf);
		JedisCluster jedis = JedisHelper.dataCluster();
		Map<String, String> cMap = new HashMap<String, String>();
		cMap.put("test", "100");
		jedis.hmset("杨家杰", cMap);
		//jedis.hset("yangjiajieTest", "test", "100");
		jedis.hincrBy("杨家杰", "test", 100);
		jedis.hincrByFloat("杨家杰", "test", 0.6);
		System.out.println(jedis.hgetAll("杨家杰"));
		System.out.println(jedis.lrange(RedisKey.SYSTEM_LIST,0,-1));
		String key0 =RedisKey.Platfrom_Count;
		String date=TimeUtil.getDate();
		System.out.println(date);
		String rkey0 = String.format(key0, date);
		String key1 =RedisKey.System_Count;
		String rkey1 = String.format(key1, "1_"+date);
		String rkey2 = String.format(key1, "2_"+date);
		String rkey3 = String.format(key1, "3_"+date);
//		jedis.del("usingTimeTestByYangjiajie1");
		Map<String, String> m1=jedis.hgetAll("usingTimeTestByYangjiajie1");
		System.out.println("异常数目1 = "+m1.size());
		  for (Map.Entry<String, String> entry : m1.entrySet()) {
			   System.out.println("key= " + entry.getKey() +'\t'+ " and value= " + entry.getValue());
			  }

		  System.out.println(jedis.hgetAll(String.format(RedisKey.Platfrom_Exception_Count, date)));
       System.out.println(jedis.hgetAll(rkey0));
       System.out.println(jedis.hgetAll(rkey1));
       System.out.println(jedis.hgetAll(rkey2));
       System.out.println(jedis.hgetAll(rkey3));
//       System.out.println(jedis.hgetAll(rkey2));
//       System.out.println(rkey3);
//       System.out.println(jedis.hgetAll(rkey3));
//       System.out.println(jedis.lrange(RedisKey.SYSTEM_LIST,0,-1));
//       
//      List <Map<String, String>> list= RedisDAOFactory.getSystemRequestCountDAO().getApiCountList("1", "20170308");
//      list.addAll(RedisDAOFactory.getSystemRequestCountDAO().getApiCountList("2", "20170308"));
//      list.addAll(RedisDAOFactory.getSystemRequestCountDAO().getApiCountList("3", "20170308"));
//      for(Map<String, String> m :list){
//    	  //System.out.println(m);
//    	  System.out.print(m.get(FieldEnum.interfaceName.getEnName()));
//    	  System.out.print('\t');
//    	 // System.out.print(m.get(FieldEnum.url.getEnName()));
//    	  System.out.print('\t');
//    	  System.out.print(m.get(RedisEnum.total_using_time.getEnName()));
//    	  System.out.print('\t');
//    	  System.out.print(m.get(RedisEnum.max_using_time.getEnName()));
//    	  System.out.print('\t');
//    	  System.out.print(m.get(RedisEnum.min_using_time.getEnName()));
//    	  System.out.println("");
//      }
//       jedis.del(rkey0);
//       for(int i=0;i<10;i++){
//    	   String key =RedisKey.System_Count;
//    	   String id =i+"_20170311";
//    	   String rkey = String.format(key, id);
//    	   jedis.del(rkey);
//           for(int j=0;j<1000;j++){
//        	   String key00 =RedisKey.Interface_Count;
//        	   String id0 =i+"_"+j+"_20170311";
//        	   String rkey00 = String.format(key00, id0);
//        	   jedis.del(rkey00);
//        	   
//           }
//       }

       String apikey=RedisKey.API_LIST;
       List<ApiInfo> list =RedisDAOFactory.getAPIListDAO().getApiList();
       Boolean select =false;
       for(ApiInfo info:list){
    	   if(info.getCnName().equals("杨家杰")){
    		   System.out.println(info.toString());
    		   select =true;
    	   }
       }
      if(!select){
    	  System.out.println("no api info");
      }
	}

}
