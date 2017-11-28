package com.ylp.common.redis.dao.impl;

import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.redis.dao.SystemUrlDAO;

import redis.clients.jedis.JedisCluster;

public class SystemUrlDAOImpl implements SystemUrlDAO {

	private final String rkey =RedisKey.SYS_NAME_HASH_MAPPING;
	public String getSysCnName(JedisCluster jedis,String url) {
		String cl = url.split("/")[2];
		String cnName =jedis.hget(rkey, cl);
		return cnName;
	}

}
