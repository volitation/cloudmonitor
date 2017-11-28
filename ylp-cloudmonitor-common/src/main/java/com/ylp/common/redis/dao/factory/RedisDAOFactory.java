package com.ylp.common.redis.dao.factory;

import com.ylp.common.redis.dao.APIListDAO;
import com.ylp.common.redis.dao.APIRequestCountDAO;
import com.ylp.common.redis.dao.LastExceptionLogsDAO;
import com.ylp.common.redis.dao.PlatfromExceptionCountDAO;
import com.ylp.common.redis.dao.PlatfromRequestCountDAO;
import com.ylp.common.redis.dao.SystemListDAO;
import com.ylp.common.redis.dao.SystemRequestCountDAO;
import com.ylp.common.redis.dao.SystemUrlDAO;
import com.ylp.common.redis.dao.impl.APIListDAOImpl;
import com.ylp.common.redis.dao.impl.APIRequestCountDAOImpl;
import com.ylp.common.redis.dao.impl.LastExceptionLogsDAOImpl;
import com.ylp.common.redis.dao.impl.PlatfromExceptionCountDAOImpl;
import com.ylp.common.redis.dao.impl.PlatfromRequestCountDAOImpl;
import com.ylp.common.redis.dao.impl.SystemListDAOImpl;
import com.ylp.common.redis.dao.impl.SystemRequestCountDAOImpl;
import com.ylp.common.redis.dao.impl.SystemUrlDAOImpl;

public class RedisDAOFactory {
	
	public static SystemListDAO getSystemListDAO(){
		return new SystemListDAOImpl();
		
	}

	public static APIListDAO getAPIListDAO(){
		return new APIListDAOImpl();
		
	}
	
	public static  LastExceptionLogsDAO getLastExceptionLogsDAO(){
		return new  LastExceptionLogsDAOImpl();
		
	}
	
	
	public static  PlatfromExceptionCountDAO getPlatfromExceptionCountDAO(){
		return new  PlatfromExceptionCountDAOImpl();
		
	}
	
	public static  PlatfromRequestCountDAO getPlatfromRequestCountDAO(){
		return new  PlatfromRequestCountDAOImpl();
		
	}
	
	public static  SystemRequestCountDAO getSystemRequestCountDAO(){
		return new  SystemRequestCountDAOImpl();
		
	}
	
	public static  APIRequestCountDAO getAPIRequestCountDAO(){
		return new  APIRequestCountDAOImpl();
		
	}
	
	public static  SystemUrlDAO getSystemUrlDAO(){
		return new  SystemUrlDAOImpl();
		
	}
}
