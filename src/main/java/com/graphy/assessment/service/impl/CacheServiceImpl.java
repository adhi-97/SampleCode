package com.graphy.assessment.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.graphy.assessment.service.CacheService;
import io.micrometer.core.instrument.Metrics;

@Service
public class CacheServiceImpl implements CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    @Autowired
    public CacheServiceImpl(RedisTemplate<String, Object> redisTemplate, CacheManager cacheManager) {
        this.redisTemplate = redisTemplate;
        this.cacheManager = cacheManager;
    }

    @Override
    public Object getFromCache(String key) throws Exception {
        logger.info("Attempting to retrieve key: {} from cache", key);
        if (cacheManager == null) {
            logger.error("CacheManager is null");
            return null;
        }
        Cache cache = cacheManager.getCache("products");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                Object value = valueWrapper.get();
                logger.info("Cache hit for key: {}", key);
                incrementMetric("cache.hit");
                return value;
            }
        }
        logger.info("Cache miss for key: {}", key);
        incrementMetric("cache.miss");
        return null;
    }

    @Override
    public void addToCache(String key, Object value) throws Exception {
        logger.info("Adding key: {} to cache with value: {}", key, value);
        if (cacheManager == null) {
            logger.error("CacheManager is null");
            return;
        }
        Cache cache = cacheManager.getCache("products");
        if (cache != null) {
            cache.put(key, value);
            logger.info("Added key: {} to cache", key);
        } else {
            logger.error("Cache 'products' not found");
        }
    }

    private void incrementMetric(String metricName) {
        Metrics.counter(metricName).increment();
    }

    @Override
    public void invalidate(String key) throws Exception {
        logger.info("Invalidating cache for key: {}", key);
        if (cacheManager == null) {
            logger.error("CacheManager is null");
            return;
        }
        Cache cache = cacheManager.getCache("products");
        if (cache != null) {
            cache.evict(key);
            logger.info("Invalidated cache for key: {}", key);
        } else {
            logger.error("Cache 'products' not found");
        }
    }
}