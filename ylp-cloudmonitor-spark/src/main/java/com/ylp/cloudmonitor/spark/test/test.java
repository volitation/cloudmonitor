package com.ylp.cloudmonitor.spark.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.cloudmonitor.spark.model.util.RequestUtil;
import com.ylp.cloudmonitor.spark.util.DataUtil;
import com.ylp.cloudmonitor.spark.util.TimeUtil;
import com.ylp.common.entity.RequestCountInfo;
import com.ylp.common.enums.LogEnum;
import com.ylp.common.es.constant.RedisKey;
import com.ylp.common.es.util.JedisHelper;
import com.ylp.common.redis.dao.factory.RedisDAOFactory;
import com.ylp.common.redis.dao.impl.SystemRequestCountDAOImpl;

import net.sf.json.JSONObject;
import redis.clients.jedis.JedisCluster;

public class test {
	public static void main(String[] args) {

		String v1 = "v1|J|2017-03-14 22:12:08,499|ERROR|172.16.8.91|UserLoginServiceImpl:166|yangjaijie20170314|?存.澶辫触:org.jose4j.jwt.consumer.InvalidJwtException: JWT (claims->{\"iss\":\"test\",\"aud\":\"test\",\"exp\":1483363756,\"jti\":\"6UJ4SFQjW6cfCvIaT-4YfQ\",\"iat\":1483360156,\"nbf\":1483360096,\"sub\":\"subject\",\"codeNum\":\"019119\",\"ylpEnterpriseRole\":\"BUSINESS\",\"userId\":\"100055\",\"userAccountNo\":\"88882016011610000210\",\"userName\":\"hfsj\",\"merchantId\":\"10064\",\"userRole\":\"ADMIN\",\"isBindingErp\":\"\",\"masterAcountNo\":\"\",\"openId\":\"oCP59tw0pBUr0k7EASUkeYSK3fg8\",\"ylpEnterpriseAcountNo\":\"E8882016011610006363\",\"audience\":\"test\"}) rejected due to invalid claims. Additional details: [The JWT is no longer valid - the evaluation time NumericDate{1483366328 -> 2017-1-2 涓..10?12?.8绉. is on or after the Expiration Time (exp=NumericDate{1483363756 -> 2017-1-2 涓..09??9?.6绉.) claim value (even when providing 30 seconds of leeway to account for clock skew).]\\n	at org.jose4j.jwt.consumer.JwtConsumer.validate(JwtConsumer.java:376)\\n	at org.jose4j.jwt.consumer.JwtConsumer.processContext(JwtConsumer.java:238)\\n	at org.jose4j.jwt.consumer.JwtConsumer.process(JwtConsumer.java:345)\\n	at org.jose4j.jwt.consumer.JwtConsumer.processToClaims(JwtConsumer.java:128)\\n	at com.ylp.common.tools.utils.jwt.JwtUtils.checkToken(JwtUtils.java:188)\\n	at com.ylp.facade.user.service.impl.UserLoginServiceImpl.refreshToken(UserLoginServiceImpl.java:151)\\n	at com.ylp.facade.user.service.impl.UserLoginServiceImpl$$FastClassByCGLIB$$6784a93f.invoke(<generated>)\\n	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204)\\n	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:698)\\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:150)\\n	at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java:96)\\n	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:260)\\n	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:94)\\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:172)\\n	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:91)\\n	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:172)\\n	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:631)\\n	at com.ylp.facade.user.service.impl.UserLoginServiceImpl$$EnhancerByCGLIB$$ca3aeabc.refreshToken(<generated>)\\n    -at com.alibaba.dubbo.common.bytecode.Wrapper9.invokeMethod(Wrapper9.java)\\n	at com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory$1.doInvoke(JavassistProxyFactory.java:46)\\n -at com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker.invoke(AbstractProxyInvoker.java:72)\\n	at com.alibaba.dubbo.rpc.protocol.InvokerWrapper.invoke(InvokerWrapper.java:53)\\n	at com.alibaba.dubbo.rpc.filter.ExceptionFilter.invoke(ExceptionFilter.java:64)\\n	at com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:91)\\n	at com.alibaba.dubbo.rpc.filter.TimeoutFilter.invoke(TimeoutFilter.java:42)\\n	at com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:91)\\n	at com.alibaba.dubbo.monitor.support.MonitorFilter.invoke(MonitorFilter.java:75)\\n	at com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:91)\\n	at com.alibaba.dubbo.rpc.protocol.dubbo.filter.TraceFilter.invoke(TraceFilter.java:78)\\n	at com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:91)\\n  -at com.alibaba.dubbo.rpc.filter.ContextFilter.invoke(ContextFilter.java:60)\\n	at com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:91)\\n	at com.alibaba.dubbo.rpc.filter.GenericFilter.invoke(GenericFilter.java:112)\\n	at com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:91)\\n	at com.alibaba.dubbo.rpc.filter.ClassLoaderFilter.invoke(ClassLoaderFilter.java:38)\\n	at com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:91)\\n  -at com.alibaba.dubbo.rpc.filter.EchoFilter.invoke(EchoFilter.java:38)\\n	at com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:91)\\n	at com.ylp.cloudmonitor.client.dubbo.KeepAttachmentFilter.invoke(KeepAttachmentFilter.java:63)\\n	at com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:91)\\n	at com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol$1.reply(DubboProtocol.java:108)\\n	at com.alibaba.dubbo.remoting.exchange.support.header.HeaderExchangeHandler.handleRequest(HeaderExchangeHandler.java:84)\\n	at com.alibaba.dubbo.remoting.exchange.support.header.HeaderExchangeHandler.received(HeaderExchangeHandler.java:170)\\n	at com.alibaba.dubbo.remoting.transport.DecodeHandler.received(DecodeHandler.java:52)\\n	at com.alibaba.dubbo.remoting.transport.dispatcher.ChannelEventRunnable.run(ChannelEventRunnable.java:82)\\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)\\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)\\n	at java.lang.Thread.run(Thread.java:744)\\n";
		String[] data = v1.split("\\|");
		RowLog log = new RowLog();
		log.setVersion(data[0]);
		log.setLanguage(data[1]);
		log.setTime(data[2]);
		log.setLevel(data[3]);
		log.setHost(data[4]);
		log.setCodeLocation(data[5]);
		log.setRequestId(data[6]);
		String detail = "";
		if (data.length > 7) {
			for (int i = 7; i < data.length; i++)
				detail = detail + data[i] + "|";
		}
		log.setDetail(data[7]);
		
		List<RowLog> list =new ArrayList<RowLog>();
		list.add(log);
		System.out.println(RequestUtil.sort(list));
	}
}
