package com.ylp.common.redis.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.ylp.common.entity.SystemInfo;
import com.ylp.common.entity.SystemUrl;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.SystemListDAO;

import net.sf.json.JSONArray;
import redis.clients.jedis.JedisCluster;

public class SystemListDAOImpl implements SystemListDAO {

	private String rkey = RedisKey.SYSTEM_LIST;

	public String getSysName(String cnName) {
		SystemInfo info =getSystemInfo(cnName);
		return String.valueOf(info.getName());
	}

	public String getSysId(String cnName) {
		SystemInfo info =getSystemInfo(cnName);
		return String.valueOf(info.getSysBizId());
	}

	public SystemInfo getSystemInfo(String cnName) {
		// 拉取系统名称映射表
		JedisCluster jedis = JedisHelper.dataCluster();
		List<String> sysList = jedis.lrange(rkey, 0, -1);
		SystemInfo sysInfo = new SystemInfo();
		JSONArray array = JSONArray.fromObject(sysList);
		try {
			for (int i = 0; i < array.size(); i++) {
				String sysCnName= String.valueOf(array.getJSONObject(i).get("cnName"));
				if (cnName.equals(sysCnName)) {
					sysInfo.setCnName(array.getJSONObject(i).getString("cnName"));
					sysInfo.setId(array.getJSONObject(i).getLong("id"));
					sysInfo.setSysBizId(array.getJSONObject(i).getInt("sysBizId"));
					sysInfo.setName(array.getJSONObject(i).getString("name"));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return sysInfo;
	}

	public List<SystemInfo> getSysList() {
		// 拉取系统名称映射表
		JedisCluster jedis = JedisHelper.dataCluster();
		List<String> sysList = jedis.lrange(rkey, 0, -1);
        List<SystemInfo> list =new ArrayList<SystemInfo>();
		JSONArray array = JSONArray.fromObject(sysList);

		

			for (int i = 0; i < array.size(); i++) {
				try {
				SystemInfo sysInfo = new SystemInfo();
				sysInfo.setCnName(array.getJSONObject(i).getString("cnName"));
				sysInfo.setId(array.getJSONObject(i).getLong("id"));
				sysInfo.setSysBizId(array.getJSONObject(i).getInt("sysBizId"));
				sysInfo.setName(array.getJSONObject(i).getString("name"));

				list.add(sysInfo);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		

		return list;
	}

}
