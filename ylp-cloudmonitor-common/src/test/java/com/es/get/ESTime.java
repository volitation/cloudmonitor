package com.es.get;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import com.ylp.common.enums.FieldEnum;
import com.ylp.common.es.api.ESClient;

/*
 * 测试es时间过滤
 */
public class ESTime {
	 private static final String TIMESTAMP = "@timestamp";  

	@Test
	public void geneIndex(){
		
		Client client = ESClient.getClient();
		client.admin().indices().prepareCreate("timeindex").execute().actionGet();
		client.admin().indices().preparePutMapping("timeindex").setType("test").setSource(getDateMapping()).execute().actionGet();

	}
	@Test
	public void setValue(){
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("name","i am test content one");
		json.put("start_time","2016-09-25 14:18:46");
		json.put("end_time","2016-12-22 14:18:46");

		IndexResponse response = ESClient.getClient().prepareIndex("timeindex", "test").setSource(json).get();

		System.out.println(response);
		
	}
//=====================
	
	@Test
	public void geneIndexMillis(){
		
		Client client = ESClient.getClient();
		client.admin().indices().prepareCreate("timeindextwo").execute().actionGet();
		client.admin().indices().preparePutMapping("timeindextwo").setType("test").setSource(getDateMillisMapping()).execute().actionGet();

	}
	@Test
	public void setValueMillis(){
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("name","i am test content one mills");
		json.put("start_time","2016-08-22 09:18:46.3");
		json.put("end_time","2016-12-24 12:18:56.88");

		IndexResponse response = ESClient.getClient().prepareIndex("timeindextwo", "test").setSource(json).get();

		System.out.println(response);
		
	}

	
	/**
	 * 根据时间搜索
	 */
	@Test
	public void testQueryByTime(){
//		   SearchRequestBuilder srq = ESClient.getClient().prepareSearch().setTypes("request");  
//	        srq.setSearchType(SearchType.DFS_QUERY_AND_FETCH);  
//	        
	        
			BoolQueryBuilder bquery = QueryBuilders.boolQuery();
			//bquery.filter(QueryBuilders.rangeQuery(FieldEnum.startTime.getEnName()).format("yyyy-MM-dd HH:mm:ss").from(beginDate).to(endDate));
			
			SearchResponse response  =	ESClient.getClient().prepareSearch("timeindextwo").setTypes().
					setQuery(bquery).addSort(FieldEnum.startTime.getEnName(), SortOrder.DESC).execute().actionGet();
			
			System.out.println(response.toString());
			
	}
	
	
	
	
	
	
	
	/**
	 *根据时间过滤,使用格式
	 */
	@Test
	public void getTimeValueFormat(){
		BoolQueryBuilder bquery = QueryBuilders.boolQuery();
		bquery.filter(QueryBuilders.rangeQuery("start_time").format("yyyy-MM-dd HH:mm:ss").from("2016-08-25 00:00:00").to("2016-12-28 00:00:00"));
		SearchResponse response  =	ESClient.getClient().prepareSearch("pay").setTypes("request").
		setQuery(bquery).execute().actionGet();
		System.out.println(response.toString());		
	}
	
	/**
	 *根据时间过滤，不使用格式
	 */
	@Test
	public void getTimeValue(){
		BoolQueryBuilder bquery = QueryBuilders.boolQuery();
		bquery.filter(QueryBuilders.rangeQuery("start_time").format("yyyy-MM-dd HH:mm:ss.SSS").from("2016-08-21 14:18:46.000").to("2016-12-28 14:18:46.000"));
		SearchResponse response  =	ESClient.getClient().prepareSearch("timeindextwo").setTypes("test").
		setQuery(bquery).execute().actionGet();
		System.out.println(response.toString());		
	}
	
	
	
	
	
	
	
	
	
	/**
	 * 获取颗粒数据的Mapping
	 */
	public static XContentBuilder getDateMapping(){
		XContentBuilder mapping = null;
		
		try {
			mapping = jsonBuilder()
			.startObject()
					.startObject("properties")
					.startObject("start_time")
						.field("type","date")
						.field("format","yyyy-MM-dd HH:mm:ss")
					.endObject()
					.startObject("name")
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject("end_time")
						.field("type","date")
						.field("format","yyyy-MM-dd HH:mm:ss")
				.endObject()
			.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mapping;
	}
	
	
	/**
	 * 增加毫秒类型
	 * @return
	 */
	public static XContentBuilder getDateMillisMapping(){
		XContentBuilder mapping = null;
		
		try {
			mapping = jsonBuilder()
			.startObject()
					.startObject("properties")
					.startObject("start_time")
						.field("type","date")
						.field("format","yyyy-MM-dd HH:mm:ss.SSS")
					.endObject()
					.startObject("name")
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject("end_time")
						.field("type","date")
						.field("format","yyyy-MM-dd HH:mm:ss.SSS")
				.endObject()
			.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mapping;
	}
	
}
