package com.ylp.common.redis.dao;

import java.util.List;

import com.ylp.common.entity.ApiInfo;

public interface APIListDAO {

	public String getApiName(String url);
	public String getApiId(String url);
	public String getSysId(String url);
	public ApiInfo getApiInfo(String url);
	public List<ApiInfo>getApiList();
}
