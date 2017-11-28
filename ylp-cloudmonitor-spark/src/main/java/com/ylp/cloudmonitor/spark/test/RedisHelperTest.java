package com.ylp.cloudmonitor.spark.test;

import com.ylp.common.es.util.JedisHelper;

import redis.clients.jedis.JedisCluster;

public class RedisHelperTest {
	public static void main(String[] args) {
      System.out.println(JedisHelper.dataCluster());
      JedisCluster jedis= JedisHelper.dataCluster();
	}
}
