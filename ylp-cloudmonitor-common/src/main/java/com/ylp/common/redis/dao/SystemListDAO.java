package com.ylp.common.redis.dao;

import java.util.List;

import com.ylp.common.entity.SystemInfo;

public interface SystemListDAO {
	public String getSysName(String cnName);
	public String getSysId(String cnName);
	public SystemInfo getSystemInfo(String cnName);
	public List<SystemInfo>getSysList();
}
