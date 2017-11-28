package com.ylp.common.es.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Dreyer
 * @date: 16/8/23 下午3:49
 * @description: Jedis客户端
 */
public class JedisHelper {
    private static int JEDISPOOL_MAXTOTAL = 300;
    private static int JEDISPOOL_MAXAIDLE = 60;
    private static int TIMEOUT = 5000;
    private static String CONF_FILE_NAME="config.properties";

    private static Logger logger = LoggerFactory.getLogger(JedisHelper.class);
    /**
     * 各个Redis集群的IP端口信息集合
     */
    private static Map<String, Set<HostAndPort>> map = new HashMap<String, Set<HostAndPort>>();
    /**
     * 其他Redis实例集合,用于后续的扩展
     */
    private static Map<String, JedisCluster> jedisClusterMap = new ConcurrentHashMap<String, JedisCluster>();
    /**
     * Redis核心业务集群实例
     */
    private static JedisCluster coreJedis = null;
    /**
     * Redis数据集群实例
     */
    private static JedisCluster dataJedis = null;

    /**
     * Redis集群配置信息初始化...
     */
    static {
        // step1:先获取全部的Redis集群配置
        List<String> list = PropertiesUtils.getKeySet(CONF_FILE_NAME,"redis.cluster");
        for (String str : list) {
            Set<HostAndPort> nodes = getHostAndPortSetByClusterName(str);
            map.put(str, nodes);
        }
        // step2:分别获取核心集群与数据集群
        String coreCluseterValue = PropertiesUtils.getProperty(CONF_FILE_NAME,"redis.coreCluster");
        String dataClusterValue = PropertiesUtils.getProperty(CONF_FILE_NAME,"redis.dataCluster");
        map.put("redis.coreCluster", map.get(coreCluseterValue));
        map.put("redis.dataCluster", map.get(dataClusterValue));
        // step3:初始化核心业务集群实例
        Set<HostAndPort> coreNodes = map.get("redis.coreCluster");
        coreJedis = getJedisClusterByNodes(coreNodes);
        // step4:初始化Redis数据集群实例
        Set<HostAndPort> dataNodes = map.get("redis.dataCluster");
        dataJedis = getJedisClusterByNodes(dataNodes);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    coreJedis.close();
                    dataJedis.close();
                } catch (IOException e) {
                    logger.error("关闭错误");
                }
            }
        });
    }

    /**
     * 禁止实例化
     */
    private JedisHelper() {

    }

    public static void main(String[] args) {
        JedisCluster jedisCluster = getCluster("redis.cluster1");
        JedisCluster jedisCluster1 = getCluster("redis.cluster1");
        System.out.println(jedisCluster == jedisCluster1);

        String result = coreCluster().set("userName", "Dreyer");
        System.out.println(result);

        String str = coreCluster().get("userName");
        System.out.println(str);
    }

    /**
     * 获取Redis核心业务集群客户端
     *
     * @return
     */
    public static JedisCluster coreCluster() {

        return coreJedis;
    }

    /**
     * 获取Redis数据集群客户端
     *
     * @return
     */
    public static JedisCluster dataCluster() {

        return dataJedis;
    }

    /**
     * 根据指定的clusterName获取JedisCluster实例
     *
     * @param clusterName
     * @return
     */
    public synchronized static JedisCluster getCluster(String clusterName) {
        logger.info("clusterName:" + clusterName);
        if (StringUtils.isEmpty(clusterName)) {
            throw new IllegalArgumentException("clusterName不能为空");
        }
        Set<HostAndPort> hostAndPortSet = map.get(clusterName);
        if (hostAndPortSet == null) {
            throw new IllegalArgumentException("clusterName对应的集群配置信息不存在");
        }
        // 如果已经实例化,则直接返回
        if (jedisClusterMap.get(clusterName) != null) {
            return jedisClusterMap.get(clusterName);
        }
        // 根据集群名获取集群IP端口信息
        Set<HostAndPort> nodes = map.get(clusterName);
        JedisCluster jedisCluster = getJedisClusterByNodes(nodes);
        // 实例化后放入jedisClusterMap
        jedisClusterMap.put(clusterName, jedisCluster);

        return jedisCluster;
    }

    /**
     * 根据集群节点nodes以及默认的配置信息获取JedisCluster
     *
     * @param nodes
     * @return
     */
    private static JedisCluster getJedisClusterByNodes(Set<HostAndPort> nodes) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(JEDISPOOL_MAXTOTAL);
        config.setMaxIdle(JEDISPOOL_MAXAIDLE);
        // 根据集群名获取集群IP端口信息
        JedisCluster jedisCluster = new JedisCluster(nodes, TIMEOUT, 100, config);
        return jedisCluster;
    }

    /**
     * 根据配置文件中的配置信息,获取Set<HostAndPort>
     *
     * @param clusterName
     * @return
     */
    private static Set<HostAndPort> getHostAndPortSetByClusterName(String clusterName) {
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        // 获取配置信息
        String result = PropertiesUtils.getProperty(CONF_FILE_NAME,clusterName);
        if (StringUtils.isEmpty(result)) {
            return nodes;
        }
        // 解析
        String[] arry = result.split(" ");
        String host;
        String port;
        for (String str : arry) {
            host = str.substring(0, str.indexOf(":"));
            port = str.substring(str.indexOf(":") + 1);
            HostAndPort hostAndPort = new HostAndPort(host, Integer.valueOf(port));
            nodes.add(hostAndPort);
        }
        return nodes;
    }

}

