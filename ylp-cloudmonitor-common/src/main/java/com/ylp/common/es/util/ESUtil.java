package com.ylp.common.es.util;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.SearchHit;

import com.ylp.common.enums.FieldEnum;
import com.ylp.common.enums.IndexEnum;
import com.ylp.common.enums.TypeEnum;
import com.ylp.common.es.api.ESClient;

public class ESUtil {

	
	/**
	 * 根据搜索响应结果封装成列表，打印出来
	 * @param searchResonse
	 * @return
	 */
	public static List<Map<String,Object>> getResponseDocList( SearchResponse searchResonse){
		
		if( null ==searchResonse || searchResonse.getHits() == null ) 
			return null;
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		for (SearchHit hit : searchResonse.getHits()) {  
	            Map<String, Object> map = hit.getSource();  
	           list.add(map);
	        }  
		 return list;
	}
	
	//创建索引和schema相关========================================
	
	/**
	 * 初始化索引库，及对应的配置
	 * @return
	 */
	public static String initCloudMonitor(){
		
		try {
			
			//删除索引库
			//TODO
			
			
			//创建支付索引库
			createIndex(IndexEnum.payIndex.getEnName());
			//创建账户索引库
			createIndex(IndexEnum.accountIndex.getEnName());
			
			
			//创建支付索引库和类型
			createMapping(IndexEnum.payIndex.getEnName(),TypeEnum.detailType.getEnName(),getDetailMapping().string());
			createMapping(IndexEnum.payIndex.getEnName(),TypeEnum.requestType.getEnName(),getRequestMapping().string());
			
			//创建账户索引库和类型
			createMapping(IndexEnum.accountIndex.getEnName(),TypeEnum.detailType.getEnName(),getDetailMapping().string());
			createMapping(IndexEnum.accountIndex.getEnName(),TypeEnum.requestType.getEnName(),getRequestMapping().string());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 创建索引
	 * @param index
	 */
	public static String createIndex(String indexName){
		CreateIndexRequest index = new CreateIndexRequest(indexName);
		ESClient.getClient().admin().indices().create(index);
		return indexName;
	}
	
	/**
	 * 创建索引，类型和mapping
	 * @param index
	 */
	public static String createIndexWithMapping(String indexName,String type,String mappingStr){
		
		Client client = ESClient.getClient();
		
		client.admin().indices().preparePutMapping(indexName).setType(type).setSource(mappingStr).execute().actionGet();

		return indexName;
	}
	
	/**
	 * 创建mapping
	 * @param index 索引名称
	 * @param type 类型
	 * @param mappingSource 约束字符串
	 */
	public static void createMapping(String index,String type, String mappingSource){
		
	   PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(mappingSource);
	   ESClient.getClient().admin().indices().putMapping(mapping).actionGet();

	}
	
	/**
	 * 获取颗粒数据的Mapping
	 */
	public static XContentBuilder getDetailMapping(){
		XContentBuilder mapping = null;
		
		try {
			mapping = jsonBuilder()
			.startObject()
					.startObject("properties")
					.startObject(FieldEnum.callLocation.getEnName())
						.field("type","string")
					.endObject()
					.startObject(FieldEnum.host.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.language.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.level.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.location.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.requestId.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.time.getEnName())
						.field("type","date")
						.field("format","yyyy-MM-dd HH:mm:ss.SSS")
					.endObject()
					.startObject(FieldEnum.version.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.url.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.user.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.role.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.ylpEnterpriseAccountNo.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.ip.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.browserType.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.browserVersion.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.mobile.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.systemVersion.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.mobileVersion.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.mobileNum.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.networkType.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.startTime.getEnName())
						.field("type","date")
						.field("format","yyyy-MM-dd HH:mm:ss.SSS")
					.endObject()
					.startObject(FieldEnum.endTime.getEnName())
						.field("type","date")
						.field("format","yyyy-MM-dd HH:mm:ss.SSS")
					.endObject()
					.startObject(FieldEnum.logDetail.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.extend.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					
			.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mapping;
	}
	
	/**
	 * 获取聚合后的数据Mapping
	 * @return
	 */
	public static XContentBuilder getRequestMapping(){
		XContentBuilder mapping = null;
		try {
			mapping = jsonBuilder()
			.startObject()
					.startObject("properties")
					.startObject(FieldEnum.browserType.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.browserVersion.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.errorCount.getEnName())
						.field("type","integer")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.errorCode.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.ylpEnterpriseAccountNo.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.endTime.getEnName())
						.field("type","date")
						.field("format","yyyy-MM-dd HH:mm:ss.SSS")
					.endObject()
					.startObject(FieldEnum.apiId.getEnName())  // 增加interfaceId
					.field("type","string")
					.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.interfaceName.getEnName())
						.field("type","string")
					.endObject()
					.startObject(FieldEnum.ip.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.level.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.mobile.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.mobileVersion.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.mobileNum.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.networkType.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.requestId.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.role.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.sysBizId.getEnName())  // 增加systemId
					.field("type","string")
					.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.systemName.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.logList.getEnName())
						.field("type","string")
					.endObject()
					.startObject(FieldEnum.usingTime.getEnName())
						.field("type","double")
					.endObject()
					.startObject(FieldEnum.startTime.getEnName())
						.field("type","date")
						.field("format","yyyy-MM-dd HH:mm:ss.SSS")
					.endObject()
					.startObject(FieldEnum.systemVersion.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.url.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.user.getEnName())
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.warnCount.getEnName())
						.field("type","integer")
						.field("index","not_analyzed")
					.endObject()
					.startObject(FieldEnum.callLocation.getEnName())
						.field("type","string")
						
					.endObject()
			.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mapping;
	}

	
}
