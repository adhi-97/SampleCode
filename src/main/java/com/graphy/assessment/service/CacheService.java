package com.graphy.assessment.service;

public interface CacheService {
	
	public Object getFromCache(String key) throws Exception;
	public void addToCache(String key, Object value) throws Exception;
	public void invalidate(String string) throws Exception;
}
