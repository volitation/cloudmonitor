package com.ylp.common.es.api;

import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ylp.common.es.factory.ClientFactory;

public class BaseClient {
	
	protected static final Logger logger = LoggerFactory
			.getLogger(BaseClient.class);

	protected static TransportClient client = ClientFactory.getClient();
	
	
	public static TransportClient getClient(){
		return ClientFactory.getClient();
	}
	
	
	/**
	 * 打印 
	 */
	 public static void print(SearchHits hits) {  
	        for (SearchHit hit : hits) {  
	            Map<String, Object> map = hit.getSource();  
	            System.out.println(map);  
	        }  
	  }  

}
