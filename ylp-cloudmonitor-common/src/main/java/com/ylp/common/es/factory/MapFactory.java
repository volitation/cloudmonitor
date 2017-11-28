package com.ylp.common.es.factory;

import java.util.HashMap;
import java.util.Map;

public class MapFactory {
	
	private Map<String, Object> data = new HashMap<String, Object>();
	private MapFactory() {}
	
	public static MapFactory create() {
		return new MapFactory();
	}
	
	
	public static MapFactory create(String key, String value) {
		return create().put(key, value);
	}
	
	public MapFactory put(String key, String value) {
		data.put(key, value);
		return this;
	}
	
	
	public Map<String, Object> getData() {
		return data;
	}
}
