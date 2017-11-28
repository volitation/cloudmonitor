package com.es.get;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ylp.common.enums.FieldEnum;
import com.ylp.common.enums.TypeEnum;
import com.ylp.common.es.api.ESClient;
import com.ylp.common.es.factory.ClientFactory;
import com.ylp.common.es.factory.MapFactory;
import com.ylp.common.es.util.ESUtil;

/**
 * client操作案例
 * @author chenyw
 * Dec 1, 2016 4:41:57 PM
 *  
 */
public class ClientDemo {
	
	private static final Logger logger = LoggerFactory.getLogger(ClientDemo.class);
	
	private static TransportClient client = ClientFactory.getClient();
	
	//case 1 : 新增单条索引文档
	@Test
	public void preparePayIndex(){
		//1、封装数据
		Map<String,Object> map = MapFactory.create(FieldEnum.browserType.getEnName(), "Apple")
		.put(FieldEnum.version.getEnName(), "V1")
		.put(FieldEnum.ip.getEnName(), "24.1348.131.12")
		.put(FieldEnum.location.getEnName(), "daaav-PayChannelHandlerContext")
		.put(FieldEnum.mobile.getEnName(), "android")
		.put(FieldEnum.ylpEnterpriseAccountNo.getEnName(), "E88t5ewe521536120910006577")
		.put(FieldEnum.browserType.getEnName(), "chrome")
		.put(FieldEnum.systemVersion.getEnName(), "ios 10.6")
		.put(FieldEnum.language.getEnName(), "java")
		.put(FieldEnum.errorCount.getEnName(), "55232")
		.put(FieldEnum.warnCount.getEnName(), "4222")
		.put(FieldEnum.requestId.getEnName(), "waitforl44i")
		.put(FieldEnum.interfaceName.getEnName(),"登录")
		.put("snn","账户系统")
		.put(FieldEnum.level.getEnName(),"1")
		.put(FieldEnum.url.getEnName(), "www.baidrruqq.com").getData();
		
		
		
		
//		errorCount("ec","错误总数"),//233
//		warnCount("wc","警告总数"),//99
//		errorCode("eco","错误码"), //10012
//		interfaceName("in","接口名称"), //接口中文名称  查询订单
//		SystemName("sn","系统名称"), //系统中文名称  支付系统
		
		
		
		try {
			//2、选择索引类型，传入类型和封装的参数
			IndexResponse response = ESClient.preparePayIndex(TypeEnum.requestType, map);
			System.out.println(response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//case 2 : 批量新增索引文档
	@Test
	public void preparePayBulkIndex(){
			//1、封装数据
			Map<String,Object> map1 = MapFactory.create(FieldEnum.browserType.getEnName(), "chrome")
			.put(FieldEnum.version.getEnName(), "V1")
			.put(FieldEnum.ip.getEnName(), "192.178.1.9")
			.put(FieldEnum.url.getEnName(), "www.google.com").getData();
			
			Map<String,Object> map2 = MapFactory.create(FieldEnum.browserType.getEnName(), "apple")
					.put(FieldEnum.version.getEnName(), "V44")
					.put(FieldEnum.ip.getEnName(), "212.128.33.1")
					.put(FieldEnum.url.getEnName(), "www.16web.com").getData();
			
			Map<String,Object> map3 = MapFactory.create(FieldEnum.browserType.getEnName(), "FireFox")
					.put(FieldEnum.version.getEnName(), "V3")
					.put(FieldEnum.ip.getEnName(), "112.134.1.32")
					.put(FieldEnum.url.getEnName(), "www.baidu.com").getData();
			
			
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			list.add(map1);
			list.add(map2);
			list.add(map3);
			try {
				//2、选择索引类型，传入类型和封装的参数
				BulkResponse response = ESClient.preparePayBulkIndex(TypeEnum.detailType, list);
				//3、遍历结果集
				Iterator<BulkItemResponse> car =response.iterator();
				//List<DocWriteResponse> responseList = new ArrayList<DocWriteResponse>();
				
				while(car.hasNext() ) {
				//4、收集结果集
				//  responseList.add(car.next().getResponse());
				}
			//	System.out.println(responseList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	
	
	
	
	
	
	
	@Test
	public void prepareSearch(){
		SearchResponse response = client.prepareSearch("index1", "index2")
		        .setTypes("type1", "type2")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery("multi", "test"))             // Query
		        //.setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
		        .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();
	}
	
	
	@Test
	public void multiGet(){
		SearchResponse response = client.prepareSearch("index1", "index2","twitter","twitter_id")
		        .setTypes("type1", "type2","tweet")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery("multi", "kimchy"))                 // Query
		        .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
		        .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();

		logger.info(response.toString());
		//以上的查询参数均为可选配置，不加参数则会搜索全部内容
		//SearchResponse response = client.prepareSearch().execute().actionGet();
	}
	
	@Test
	public void multiGetWithoutQuery(){
		SearchResponse response = client.prepareSearch().execute().actionGet();
		logger.info(response.toString());
		//以上的查询参数均为可选配置，不加参数则会搜索全部内容
		
	}

	@Test
	public void index(){
		SearchResponse response = client.prepareSearch().addField("sn").setFrom(0).setSize(1000).execute().actionGet();
		
		for (SearchHit hit : response.getHits()) {  
            Map<String, Object> map = hit.getSource();  
            System.out.println(map);  
        }  
		
		//logger.info(response.toString());
		//以上的查询参数均为可选配置，不加参数则会搜索全部内容
		
	}
	
	
	
	@Test
	public void testMust(){
		String systemName = "";
		String logLevel = "DEBUG";
		String errorCode = null;
		String interfaceName = "";
		int page = 1;
		int pageSize = 15;
		BoolQueryBuilder bquery = QueryBuilders.boolQuery();
	
		if(!StringUtils.isEmpty(systemName)){
			
			bquery.must(QueryBuilders.matchQuery(FieldEnum.systemName.getEnName(), systemName));
		}
		if(!StringUtils.isEmpty(logLevel)){
			bquery.must(QueryBuilders.matchQuery(FieldEnum.level.getEnName(), logLevel));
		}
		if(!StringUtils.isEmpty(interfaceName)){
			bquery.must(QueryBuilders.matchQuery(FieldEnum.interfaceName.getEnName(), interfaceName));
		}
		if(!StringUtils.isEmpty(errorCode)){
			bquery.must(QueryBuilders.matchQuery(FieldEnum.errorCode.getEnName(), errorCode));	
		}
		
		
		SearchResponse response  =	ESClient.getClient().prepareSearch().
				setQuery(bquery).setFrom(pageSize * (page - 1)).setSize(pageSize).execute().actionGet();
		
		logger.info(response.toString());
		Long total = response.getHits().getTotalHits();
		List<Map<String,Object>>list =  ESUtil.getResponseDocList(response);
		System.out.println(list);
	}
	
	
	
	
	
	
	@Test
	public void serarchAllSystem(){
		SearchResponse response = client.prepareSearch("pay").setTypes("request","detail").setFetchSource(FieldEnum.systemName.getEnName(), "").setSize(1000).execute().actionGet();
		
		
		
		for (SearchHit hit : response.getHits()) {  
            Map<String, Object> map = hit.getSource();  
            System.out.println(map);  
        }  
			
			
	}
	
	
	@Test
	public void aggregation(){
		SearchResponse sr = client.prepareSearch()
			    .setQuery(QueryBuilders.matchAllQuery())
			    .addAggregation( AggregationBuilders.terms("agg1").field("ip") ).get();
			// Get your facet results
			Terms ipAgg = sr.getAggregations().get("agg1");

			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			for(Bucket obj :ipAgg.getBuckets()){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("key", obj.getKeyAsString());
				map.put("doc_count", obj.getDocCount());
				list.add(map);
			}
			//logger.info(list.toString());
			
			for (SearchHit hit : sr.getHits()) {  
	            Map<String, Object> map = hit.getSource();  
	            System.out.println(map);  
	        }  
			
			
			
			
			
			
//			 "aggregations" : {
//				    "agg1" : {
//				      "doc_count_error_upper_bound" : 0,
//				      "sum_other_doc_count" : 0,
//				      "buckets" : [ {
//				        "key" : "112.178.234.1",
//				        "doc_count" : 3
//				      }, {
//				        "key" : "192.178.1.1",
//				        "doc_count" : 2
//				      }, {
//				        "key" : "192.178.234.1",
//				        "doc_count" : 1
//				      } ]
//				    }
	}
	
	
}
