package com.bujian.cache.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 缓存配置文件
 * @author bujian
 * @date 2021/7/7 18:12
 */
@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public ConcurrentMapCacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        //cacheManager.setStoreByValue(true); //true表示缓存一份副本，否则缓存引用
        return cacheManager;
    }

    @Bean(name = "crudKeyGen")
    public KeyGenerator crudKeyGen() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                // 获取目标方法的泛型入参 第二个参数是实体类的参数
                Type[] actualTypeArguments = ((ParameterizedType) target.getClass().getGenericSuperclass()).getActualTypeArguments();
                String name = ((Class) actualTypeArguments[actualTypeArguments.length - 1]).getSimpleName();
                //PRO:USER:UID:18 命名规范
                return name  + ":" + params[0];
            }
        };
    }
}
