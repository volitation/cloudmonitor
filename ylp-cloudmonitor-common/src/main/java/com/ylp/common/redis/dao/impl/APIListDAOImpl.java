package com.ylp.common.redis.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ylp.common.entity.ApiInfo;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.APIListDAO;

import net.sf.json.JSONArray;
import redis.clients.jedis.JedisCluster;

public class APIListDAOImpl implements APIListDAO{
	
	private String rkey=RedisKey.API_LIST;

	public String getApiName(String url) {
		ApiInfo info =getApiInfo(url);
		
		return info.getCnName();
	}

	public String getApiId(String url) {
		ApiInfo info =getApiInfo(url);
		return String.valueOf(info.getId());
	}

	public String getSysId(String url) {
		ApiInfo info =getApiInfo(url);
		return String.valueOf(info.getSysBizId());
	}

	public ApiInfo getApiInfo(String url) {
		// 拉取系统名称映射表
		JedisCluster jedis = JedisHelper.dataCluster();
		List<String> apiList = jedis.lrange(RedisKey.API_LIST, 0, -1);
		JSONArray array = JSONArray.fromObject(apiList);
		String apiName = null;
		String an =null;
		ApiInfo info =new ApiInfo();
		try {
			if (url.contains("?")) {
				an = url.split("\\?")[0].replace("http://" + url.split("/")[2] + "/", "");
			} else {
				an = url.replace("http://" + url.split("/")[2] + "/", "");
			}
			for (int i = 0; i < array.size(); i++) {
				String sysUrl = String.valueOf(array.getJSONObject(i).get("url"));
				if (an.equals(sysUrl)) {
					info.setCnName(array.getJSONObject(i).getString("cnName"));
					info.setCreateTime(StrToDate(array.getJSONObject(i).getString("createTime")));
					info.setId(array.getJSONObject(i).getLong("id"));
					info.setName(array.getJSONObject(i).getString("name"));
					info.setSysBizId(array.getJSONObject(i).getLong("sysBizId"));
					info.setUrl(array.getJSONObject(i).getString("url"));
					return info;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return info;
	}

	public List<ApiInfo> getApiList() {
		// 拉取系统名称映射表
				JedisCluster jedis = JedisHelper.dataCluster();
				List<String> apiList = jedis.lrange(RedisKey.API_LIST, 0, -1);
				List<ApiInfo>list=new ArrayList<ApiInfo>();
				JSONArray array = JSONArray.fromObject(apiList);
			
					for (int i = 0; i < array.size(); i++) {
						try {
						ApiInfo info =new ApiInfo();
							info.setCnName(array.getJSONObject(i).getString("cnName"));
							info.setCreateTime(StrToDate(array.getJSONObject(i).getString("createTime")));
							info.setId(array.getJSONObject(i).getLong("id"));
							info.setName(array.getJSONObject(i).getString("name"));
							info.setSysBizId(array.getJSONObject(i).getLong("sysBizId"));
							info.setUrl(array.getJSONObject(i).getString("url"));
							list.add(info);
					} catch (Exception e) {
						// TODO: handle exception
					}
					}

				return list;
	}
	

	/**
	* 日期转换成字符串
	* @param date 
	* @return str
	*/
	private String DateToStr(Date date) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String str = format.format(date);
	   return str;
	} 

	/**
	* 字符串转换成日期
	* @param str
	* @return date
	*/
	private Date StrToDate(String str) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   Date date = null;
	   try {
	    date = format.parse(str);
	   } catch (ParseException e) {

	   }
	   return date;
	}

}
