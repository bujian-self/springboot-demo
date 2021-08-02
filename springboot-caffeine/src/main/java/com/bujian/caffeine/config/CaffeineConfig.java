package com.bujian.caffeine.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableCaching
@Configuration
public class CaffeineConfig {

    @Bean(name = "caffeine")
    @Primary
    public CacheManager cacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAllowNullValues(true);
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(100)
                        .maximumSize(1000)
                        .expireAfterWrite(50, TimeUnit.SECONDS)
        );
        cacheManager.setCacheNames(Arrays.asList("null"));
        return cacheManager;
    }

    /**
     * 用来加载数据
     * */
    @Bean(name = "Cache")
    public Cache cache(){
        return ((CaffeineCache)cacheManager().getCache("null")).getNativeCache();
    }

}
