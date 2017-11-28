package com.ylp.common.es.factory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ylp.common.es.constant.ServerConstant;

public class ClientFactory {

	private static final Logger logger = LoggerFactory
			.getLogger(ClientFactory.class);

	private ClientFactory() {
	}

	private static TransportClient client = null;

	public static TransportClient getClient() {
		if (client != null) {
			return client;
		} else {
			synchronized (ClientFactory.class) {
				logger.info("-> client为null,重新初始化");
				try {
					Settings setting = Settings.builder().put("client.transport.sniff", true)
							.put("cluster.name", ServerConstant.ES_CLUSTER_NAME)
							.build();

					client = TransportClient.builder().settings(setting).build()
							.addTransportAddresses(
									new InetSocketTransportAddress(
											InetAddress
													.getByName(ServerConstant.ES_HOST1), ServerConstant.ES_PORT1),
									new InetSocketTransportAddress(
											InetAddress
													.getByName(ServerConstant.ES_HOST2), ServerConstant.ES_PORT2),
									new InetSocketTransportAddress(
											InetAddress
													.getByName(ServerConstant.ES_HOST3), ServerConstant.ES_PORT3));

					return client;
				} catch (UnknownHostException e) {
					logger.error("-->初始化elasticSearch失败[{}]", e);
					client.close();
					client = null;
				}
				return null;

			}
		}

	}

}
