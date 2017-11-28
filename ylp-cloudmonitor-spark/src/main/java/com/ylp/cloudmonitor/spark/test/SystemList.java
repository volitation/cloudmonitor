package com.ylp.cloudmonitor.spark.test;

import java.util.List;

import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;

import net.sf.json.JSONArray;
import redis.clients.jedis.JedisCluster;

public class SystemList {
	public static void main(String[] args) {
		JedisCluster jedis = JedisHelper.dataCluster();
		List<String> list1 = jedis.lrange(RedisKey.API_LIST, 0, -1);
		JSONArray array1 = JSONArray.fromObject(list1);
		for (int i = 0; i < array1.size(); i++) {
			System.out.println(array1.getJSONObject(i));
		}
	}
}
