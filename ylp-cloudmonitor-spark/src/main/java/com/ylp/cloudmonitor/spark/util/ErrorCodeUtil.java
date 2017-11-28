package com.ylp.cloudmonitor.spark.util;

import com.ylp.common.enums.LogEnum;

public class ErrorCodeUtil {
public static Integer getErrorCode(String level){
	if (level.equals(LogEnum.error.getEnName())) {
		return 400;
	} else if (level.equals(LogEnum.warn.getEnName())) {
		return 200;
	}else {
		return 0;
	}
}
}
