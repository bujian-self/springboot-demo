package com.bujian.cache.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis 常用方法封装
 * @author bujian
 * @date 2021/7/9 17:39
 */
@Component
@Slf4j
public class RedisSimpleService {

    @Value("${spring.cache.redis.time-to-live:0}")
    private Duration timeout;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 写入缓存,默认时间
     * @param key   键
     * @param value 值
     */
    public void saveOrUpdate(final String key, Object value) {
        this.saveOrUpdate(key, value, timeout.getSeconds());
    }

    /**
     * 写入缓存,设置时间
     * @param key   键
     * @param value 值
     * @param timeout   超时时间
     */
    public void saveOrUpdate(final String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 重设key生存时间
     * @param key   键
     * @param timeout  时间
     * @return boolean 成功失败
     */
    public boolean setLifeTime(final String key, long timeout) {
        if (timeout > 0) {
            return this.hasKey(key) ? redisTemplate.expire(key, timeout, TimeUnit.SECONDS) : false;
        }else if (timeout == 0 || timeout == -2) {
            log.warn("当生存时间为 0 或 -2 时,该键:{} 将被删除", key);
            return this.remove(key);
        }else if (timeout == -1) {
            log.warn("当生存时间为 -1 时,该键:{} 将用不过期", key);
            return redisTemplate.persist(key);
        }else{
            throw new IllegalArgumentException("生存时间不规范");
        }
    }

    /**
     * 查 是否存在
     * @param key   键
     * @return boolean  true 存在 false 不存在
     */
    public boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 查剩余时间
     * @param key   键
     * @return long 秒
     */
    public long getLifeTime(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 查 返回obj
     * @param key   键
     * @return Object   返回obj类 不存在 返回null
     */
    public Object getObj(final String key) {
        return this.hasKey(key) ? redisTemplate.opsForValue().get(key) : null;
    }

    /**
     * 查 返回泛型
     * @param key   键
     * @param clazz 转换类的class
     * @return T    泛型
     */
    public <T> T get(final String key, Class<T> clazz) {
        Object obj = this.getObj(key);
        return obj == null ? null : new ObjectMapper().convertValue(obj, clazz);
    }

    /**
     * 查 返回hashMap
     * @param key   键
     * @return Map<String,Object>   返回hashMap
     */
    public HashMap<String, Object> getMap(final String key) {
        return this.get(key, HashMap.class);
    }

    /**
     * 查 返回JSONObject
     * @param key   键
     * @return com.alibaba.fastjson.JSONObject
     */
    public JSONObject getJson(final String key) {
        return this.get(key,JSONObject.class);
    }

    /**
     * 查 返回List
     * @param key   键
     * @param clazz 转换类的class
     * @return java.util.List<T>
     */
    public <T> List<T> getList(final String key, Class<T> clazz) {
        Object obj = this.getObj(key);
        return obj == null ? new ArrayList<>() : JSONObject.parseArray(JSON.toJSONString(obj), clazz);
    }

    /**
     * 删除缓存
     * @param key   键
     * @return boolean  true 成功 false 失败
     */
    public boolean remove(String key) {
        return this.hasKey(key) ? redisTemplate.delete(key) : true;
    }

}
