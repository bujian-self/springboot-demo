package com.bujian.satoken.service;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * cache 常用方法封装
 * @author bujian
 * @date 2021/7/21
 */
public interface CacheService {

    /**
     * 写入缓存,默认时间
     * @param key   键
     * @param value 值
     */
    public void saveOrUpdate(final String key, Object value);

    /**
     * 写入缓存,设置时间
     * @param key   键
     * @param value 值
     * @param timeout   超时时间
     */
    public void saveOrUpdate(final String key, Object value, long timeout);

    /**
     * 重设key生存时间
     * @param key   键
     * @param timeout  时间
     * @return boolean 成功失败
     */
    public boolean setLifeTime(final String key, long timeout);

    /**
     * 查 是否存在
     * @param key   键
     * @return boolean  true 存在 false 不存在
     */
    public boolean hasKey(final String key);

    /**
     * 查剩余时间
     * @param key   键
     * @return long 秒
     */
    public long getLifeTime(final String key);

    /**
     * 查 返回obj
     * @param key   键
     * @return Object   返回obj类 不存在 返回null
     */
    public Object getObj(final String key);

    /**
     * 查 返回泛型
     * @param key   键
     * @param clazz 转换类的class
     * @return T    泛型
     */
    public <T> T get(final String key, Class<T> clazz) ;

    /**
     * 查 返回hashMap
     * @param key   键
     * @return Map<String,Object>   返回hashMap
     */
    public HashMap<String, Object> getMap(final String key);

    /**
     * 查 返回JSONObject
     * @param key   键
     * @return com.alibaba.fastjson.JSONObject
     */
    public JSONObject getJson(final String key);

    /**
     * 查 返回List
     * @param key   键
     * @param clazz 转换类的class
     * @return java.util.List<T>
     */
    public <T> List<T> getList(final String key, Class<T> clazz) ;
    /**
     * 删除缓存
     * @param key   键
     * @return boolean  true 成功 false 失败
     */
    public boolean remove(String key);

}
