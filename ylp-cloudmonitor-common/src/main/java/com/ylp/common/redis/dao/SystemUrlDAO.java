package com.ylp.common.redis.dao;

import com.ylp.common.es.constant.RedisKey;

import redis.clients.jedis.JedisCluster;

public interface SystemUrlDAO {

	public String getSysCnName(JedisCluster jedis,String url);

}
