package com.ylp.cloudmonitor.spark.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.cloudmonitor.spark.model.RowLog;
import com.ylp.common.enums.FieldEnum;
import com.ylp.common.es.factory.MapFactory;

import net.sf.json.JSONObject;

public class test01 {
	public static void main(String[] args) {
		
	    String s ="v1|J|2016-12-08 14:10:13,588| INFO|172.16.8.81|TraceService:97| |request started:172.16.10.1|Chrome|54.0.2840.98|MAC OS X|null|null|null|null|null|";
		String[] data =s.split("\\|");
		String detail = "";
		String requestid =data[6];
		if(data.length>7){
			for(int i =7;i<data.length;i++)
			detail=detail  +data[i]+ "|";
		}
		System.out.println(requestid.length());
		
	String s1 ="http://dev-pay-api.vipzhuang.cn/income/incomeSource?beginTime=2016-12-05+00%3A00%3A00&endTime=2016-12-11+23%3A59%3A59";
;
	String a =s1.split("/")[3];
	System.out.println("test : " + a);
	
	String time ="2016-12-08 14:10:13,588";
	String time1 =time.replace("-", "").replace(" ", "").replace(":", "").replace(",", "");
	System.out.println(time1);
	
	String url ="http://dev-pay-api.vipzhuang.cn/income/incomeSource?beginTime=2016-12-05+00%3A00%3A00&endTime=2016-12-11+23%3A59%3A59";
	String url2 ="http://test-user-api.vipzhuang.cn/authority/menu/role/list";
	String cl = url.split("/")[2];
	if(url.contains("?")){
		String an =url.split("\\?")[0].replace("http://"+cl+"/", "");
    System.out.println(an);
	}else{
		String an =url.replace("http://"+cl+"/", "");
		System.out.println(an);
	}
	
	System.out.println("requestid"+data[6]);
	RowLog log = new RowLog();
	log.setVersion(data[0]);
	log.setLanguage( data[1]);
	log.setTime(data[2] );
	log.setLevel( data[3]);
	log.setHost(data[4] );
	log.setCodeLocation(data[5]);
	log.setRequestId( data[6]);
	log.setDetail( data[7] );
		System.out.println("loggetrequestidlength"+log.getRequestId().length());
		
	    JSONObject member1 = new JSONObject();  
	    member1.put("loginname", null);  
	    member1.put("password", "userpass");  
	    member1.put("email","10371443@qq.com");  
	    member1.put("sign_date", "2007-06-12"); 
	    System.out.println(member1);
	    System.out.println(member1.toString());
	    
	    List<Map<String,Object>> esList = new ArrayList<Map<String,Object>>();
	    for(int i=1;i<5;i++){
	    	Map<String,Object> esMap = MapFactory.create(FieldEnum.callLocation.getEnName(), i+"i")
					.put(FieldEnum.version.getEnName(), i+"i")
					.put(FieldEnum.language.getEnName(), i+"i")
					.put(FieldEnum.time.getEnName(), i+"i")
					.getData();
					esList.add(esMap);
	    }
		System.out.println(esList);
		RequestLogs r= new RequestLogs();
		for (int i=0;i<10;i++){
			r.add("ERROR");
			System.out.println("ec : "+r.getErrorCount());
		}
		
		System.out.println("(ProtocolFilterWrapper.java:91)\n\tat com.alibaba.dubbo.rpc.protocol.dubbo");
	}
	

	
}
