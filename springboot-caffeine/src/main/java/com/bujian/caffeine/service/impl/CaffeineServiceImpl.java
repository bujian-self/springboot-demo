package com.bujian.caffeine.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bujian.caffeine.service.CacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class CaffeineServiceImpl implements CacheService {

    @Autowired
    private Cache cache;

    /**
     * 写入缓存,默认时间
     *
     * @param key   键
     * @param value 值
     */
    @Override
    public void saveOrUpdate(final String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 写入缓存,设置时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 超时时间
     */
    @Override
    public void saveOrUpdate(final String key, Object value, long timeout) {
        cache.put(key, value);
    }

    /**
     * 重设key生存时间
     *
     * @param key     键
     * @param timeout 时间
     * @return boolean 成功失败
     */
    @Override
    public boolean setLifeTime(final String key, long timeout) {
        Object val = cache.getIfPresent(key);
        if (val == null) {
            return false;
        }
        cache.invalidate(key);
        cache.put(key, val);
        return true;
    }

    /**
     * 查 是否存在
     *
     * @param key 键
     * @return boolean  true 存在 false 不存在
     */
    @Override
    public boolean hasKey(final String key) {
        return cache.getIfPresent(key) != null;
    }

    /**
     * 查剩余时间
     *
     * @param key 键
     * @return long 秒
     */
    @Override
    public long getLifeTime(final String key) {
        return 0;
    }

    /**
     * 查 返回obj
     *
     * @param key 键
     * @return Object   返回obj类 不存在 返回null
     */
    @Override
    public Object getObj(final String key) {
        return cache.getIfPresent(key);
    }

    /**
     * 查 返回泛型
     *
     * @param key   键
     * @param clazz 转换类的class
     * @return T    泛型
     */
    @Override
    public <T> T get(final String key, Class<T> clazz) {
        Object obj = getObj(key);
        return obj == null ? null : new ObjectMapper().convertValue(obj, clazz);
    }

    /**
     * 查 返回hashMap
     *
     * @param key 键
     * @return Map<String, Object>   返回hashMap
     */
    @Override
    public HashMap<String, Object> getMap(final String key) {
        return null;
    }

    /**
     * 查 返回JSONObject
     *
     * @param key 键
     * @return com.alibaba.fastjson.JSONObject
     */
    @Override
    public JSONObject getJson(final String key) {
        return null;
    }

    /**
     * 查 返回List
     *
     * @param key   键
     * @param clazz 转换类的class
     * @return java.util.List<T>
     */
    @Override
    public <T> List<T> getList(final String key, Class<T> clazz) {
        Object ifPresent = cache.getIfPresent(key);
        return ifPresent == null ? new ArrayList<>() : JSONObject.parseArray(JSONObject.toJSONString(ifPresent), clazz);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return boolean  true 成功 false 失败
     */
    @Override
    public boolean remove(String key) {
        cache.invalidate(key);
        return false;
    }

}
