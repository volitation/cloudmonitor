package com.ylp.cloudmonitor.spark.test;

import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.common.enums.FieldEnum;
import com.ylp.common.enums.TypeEnum;
import com.ylp.common.es.api.ESClient;
import com.ylp.common.es.dto.LogModel;
import com.ylp.common.es.factory.ClientFactory;
import com.ylp.common.es.factory.MapFactory;
import com.ylp.common.es.util.ESUtil;

public class EsTest {
	public static void main(String[] args) throws Exception {
		SearchRequestBuilder requestBuilder =  ESClient.getClient()
                .prepareSearch().setTypes(TypeEnum.requestType.getEnName())
                .setQuery( QueryBuilders.termQuery("requestid","02f2670f0917a042bc0a16b0d5622e15a999") ).setSize(1);
	 	MultiSearchResponse sr =  ESClient.getClient().prepareMultiSearch()
                .add(requestBuilder)  
                .execute().actionGet();
	 	
	 	//超过两个响应结果进行处理

	 		SearchResponse req = sr.getResponses()[0].getResponse();

		System.out.println(req);

	}

	//生成聚合后的详情日志
			private static LogModel geneLogModel( SearchResponse req, LogModel logModel )throws Exception 	{
				
				Map<String,Object> map = req.getHits().getHits()[0].getSource();
				
				logModel.setRequestid( map.get(FieldEnum.requestId.getEnName()).toString());
				logModel.setLogTime( map.get(FieldEnum.startTime.getEnName()).toString());
				
				logModel.setSystemName( map.get(FieldEnum.systemName.getEnName()).toString());
				logModel.setLogLevel( map.get(FieldEnum.level.getEnName()).toString());
				
				logModel.setUrl( map.get(FieldEnum.url.getEnName()).toString());
				
				logModel.setInterfaceName ( map.get(FieldEnum.interfaceName.getEnName()).toString());
				logModel.setErrorCode ( map.get(FieldEnum.errorCode.getEnName()).toString());
				
				logModel.setLogList(map.get(FieldEnum.logList.getEnName()).toString());
				return logModel;
			}

}
