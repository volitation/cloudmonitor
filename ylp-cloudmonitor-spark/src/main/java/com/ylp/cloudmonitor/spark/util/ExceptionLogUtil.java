package com.ylp.cloudmonitor.spark.util;

import com.ylp.cloudmonitor.spark.model.RequestLogs;
import com.ylp.common.entity.ExceptionLogInfo;
import com.ylp.common.enums.LogEnum;
import com.ylp.common.enums.RedisEnum;

public class ExceptionLogUtil {
	
	public static ExceptionLogInfo getExceptionLogInfo(RequestLogs rgroup){
				// 将error和warn级别的日志的相关信息储存到一个list中
				String requestID = rgroup.getRequestId();
				String time = rgroup.getStratTime();
				String cl = rgroup.getCl();
				String an = rgroup.getAn();
				String le = rgroup.getLevel();
				String usingTime = rgroup.getUseTime();
				String ld = rgroup.getLogList();
				Integer errorCode = ErrorCodeUtil.getErrorCode(le);
				if (null == cl) {
					cl = "其他服务";
				}
				if (null == an) {
					an = "nullvalue";
				}
				if (null == errorCode) {
					errorCode = 0;
				}
				if (null == usingTime) {
					usingTime = "0";
				}
				if (null == ld) {
					ld = "nullvalue";
				}
				ExceptionLogInfo info =new ExceptionLogInfo();
				info.setApiName(an);
				info.setEco(String.valueOf(errorCode));
				info.setLogDetail(ld);
				info.setLogTime(time);
				info.setLogType(le);
				info.setRequestId(requestID);
				info.setSystemName(cl);
				info.setUsingTime(usingTime);
		return info;
		
	}

}
