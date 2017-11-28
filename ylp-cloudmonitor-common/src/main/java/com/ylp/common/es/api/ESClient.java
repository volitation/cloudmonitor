package com.ylp.common.es.api;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ylp.common.enums.IndexEnum;
import com.ylp.common.enums.TypeEnum;


/**
 * elasticSearch操作客户端
 * 
 * @author chenyw Dec 9, 2016 11:41:46 AM
 *
 */
public class ESClient extends BaseClient {

	private static final Logger logger = LoggerFactory
			.getLogger(ESClient.class);


	
	
	
	
	
	/**
	 * 操作支付索引库,插入一条文档
	 * 
	 * @param typeEnum
	 *            索引库里面的类型
	 * @param source
	 *            索引数据
	 */
	public static IndexResponse preparePayIndex(TypeEnum typeEnum,
			Map<String, Object> doc) throws Exception {
		if (null == doc || doc.isEmpty())
			throw new NullPointerException("payIndex的source参数为空");

		try {
		
			IndexResponse response = client
					.prepareIndex(IndexEnum.payIndex.getEnName(),
							typeEnum.getEnName()).setSource(doc).get();
			
			logger.info(" -> 插入id [{}], _source [{}]", response.getId(),
					response.toString());
			return response;

		} catch (Exception e) {
			logger.error("-> error [{}]", e);
			return null;
		}
	}
	/**
	 * 操作支付索引库,插入一条文档
	 * 
	 * @param typeEnum
	 *            索引库里面的类型
	 * @param source
	 *            索引数据
	 */
	public static IndexResponse prepareAccountIndex(TypeEnum typeEnum,
			Map<String, Object> doc) throws Exception {
		if (null == doc || doc.isEmpty())
			throw new NullPointerException("payIndex的source参数为空");

		try {
		
			IndexResponse response = client
					.prepareIndex(IndexEnum.accountIndex.getEnName(),
							typeEnum.getEnName()).setSource(doc).get();
			
			logger.info(" -> 插入id [{}], _source [{}]", response.getId(),
					response.toString());
			return response;

		} catch (Exception e) {
			logger.error("-> error [{}]", e);
			return null;
		}
	}

	/**
	 * 操作支付索引库，批量插入文档
	 * 
	 * @param typeEnum
	 *            索引库里面的类型
	 * @param source
	 *            索引数据
	 */
	public static BulkResponse preparePayBulkIndex(TypeEnum typeEnum,
			List<Map<String, Object>> listDoc) throws Exception {
		if (null == listDoc || listDoc.isEmpty())
			throw new NullPointerException("payIndex的source参数为空");

		BulkRequestBuilder bulkRequest = client.prepareBulk();
		Iterator<Map<String, Object>> car = listDoc.iterator();

		try {
			while (car.hasNext()) {
				Map<String, Object> doc = (Map<String, Object>) car.next();

				bulkRequest.add(client.prepareIndex(
						IndexEnum.payIndex.getEnName(), typeEnum.getEnName())
						.setSource(doc));
			}

			BulkResponse bulkResponse = bulkRequest.execute().actionGet();

			if (bulkResponse.hasFailures()) {
				logger.info(" -> 插入失败信息 [{}]",
						bulkResponse.buildFailureMessage());
			}

			return bulkResponse;
		} catch (Exception e) {
			logger.error("-> error [{}]", e);
			return null;
		}
	}

	/**
	 * 操作账户索引库，批量插入文档
	 * 
	 * @param typeEnum
	 *            索引库里面的类型
	 * @param source
	 *            索引数据
	 */
	public static BulkResponse prepareAccountBulkIndex(TypeEnum typeEnum,
			List<Map<String, Object>> listDoc) throws Exception {
		if (null == listDoc || listDoc.isEmpty())
			throw new NullPointerException("payIndex的source参数为空");

		BulkRequestBuilder bulkRequest = client.prepareBulk();
		Iterator<Map<String, Object>> car = listDoc.iterator();

		try {
			while (car.hasNext()) {
				Map<String, Object> doc = (Map<String, Object>) car.next();

				bulkRequest.add(client.prepareIndex(
						IndexEnum.accountIndex.getEnName(), typeEnum.getEnName())
						.setSource(doc));
			}

			BulkResponse bulkResponse = bulkRequest.execute().actionGet();

			if (bulkResponse.hasFailures()) {
				logger.info(" -> 插入失败信息 [{}]",
						bulkResponse.buildFailureMessage());
			}

			return bulkResponse;
		} catch (Exception e) {
			logger.error("-> error [{}]", e);
			return null;
		}
	}

}
