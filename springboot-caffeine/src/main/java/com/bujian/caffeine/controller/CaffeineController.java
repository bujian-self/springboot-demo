package com.bujian.caffeine.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/anon")
@RestController
@Slf4j
public class CaffeineController {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private Cache cache;

    @GetMapping("/test")
    public Object test(){
        JSONObject json = new JSONObject();
        json.put("default", cache.asMap());
        for (String cacheName : cacheManager.getCacheNames()) {
            CaffeineCache caffeineCache = (CaffeineCache ) cacheManager.getCache(cacheName);
            json.put(cacheName, caffeineCache.getNativeCache().asMap());
        }
        return json;
    }
}
