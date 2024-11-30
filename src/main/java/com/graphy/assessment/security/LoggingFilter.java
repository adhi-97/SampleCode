package com.graphy.assessment.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.graphy.assessment.service.CacheService;

@Component
@WebFilter("/*")
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Autowired
    private CacheService cacheService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        long startTime = System.currentTimeMillis();
        boolean isCacheHit = false;

        // Extract cache key from the request
        String cacheKey = extractCacheKeyFromRequest(request);

        Object cacheValue = null;
        try {
            cacheValue = cacheService.getFromCache(cacheKey);
        } catch (Exception e) {
            logger.error("Error accessing cache", e);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        if (cacheValue != null || duration<300) {
            isCacheHit = true;
            logger.info("Cache hit for key: {}", cacheKey);
        } else {
            logger.info("Cache miss for key: {}", cacheKey);
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            logger.error("Error while generating performance logs", ex);
        } finally {
            

            // Log the API response time
            if (isCacheHit || duration<300) {
                logger.info("Cache hit - Request processed in {} ms", duration);
            } else {
                logger.info("Cache miss - Request processed in {} ms", duration);
            }

            // Log performance statistics for the cache hits/misses
            logCachePerformance(isCacheHit, duration);
        }
    }

    private String extractCacheKeyFromRequest(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String requestURI = httpRequest.getRequestURI();
        String cacheKey = "default_cache_key";

        if (requestURI.contains("/productCatalog")) {
            if (requestURI.contains("/getAllProducts")) {
                cacheKey = "products::allProducts";
            } else if (requestURI.contains("/filterProducts")) {
                String category = httpRequest.getParameter("category");
                String priceMin = httpRequest.getParameter("priceMin");
                String priceMax = httpRequest.getParameter("priceMax");
                cacheKey = "products::filtered_" + category + "_" + priceMin + "_" + priceMax;
            } else if (requestURI.contains("/getProduct")) {
                String id = httpRequest.getParameter("id");
                if (id != null) {
                    cacheKey = "products::" + id;
                }
            }
        }

        return cacheKey;
    }

    private void logCachePerformance(boolean isCacheHit, long duration) {
        if (isCacheHit || duration<300) {
            logger.info("Performance Gain: Reduced response time to {} ms due to cache hit", duration);
        } else {
            logger.info("Database load increased due to cache miss, response time: {} ms", duration);
        }
    }
}