package com.bujian.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.File;
import java.time.Duration;
import java.util.Objects;

/**
 * 缓存配置文件
 * @author bujian
 * @date 2021/7/7 18:12
 */
@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.application.name:}")
    private String appName;
    @Value("${spring.cache.redis.use-key-prefix:}")
    private Boolean useKeyPrefix;
    @Value("${spring.cache.redis.key-prefix:}")
    private String keyPrefix;
    @Value("${spring.cache.redis.time-to-live:0}")
    private Duration timeToLive;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager
                .builder(factory)
                .cacheDefaults(this.getRedisCacheConfiguration())
                .build();
    }

    /**
     * 配置序列化（解决乱码的问题）
     */
    private RedisCacheConfiguration getRedisCacheConfiguration() {
        //全局添加cacheName前缀
        return cacheConfigAddkeyPrefix(
                RedisCacheConfiguration
                .defaultCacheConfig()
                // 超时时间
                .entryTtl(timeToLive)
                // key 序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // value 序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(this.getJackson2JsonRedisSerializer()))
                .disableCachingNullValues()
        );
    }

    /**
     * 给cacheName添加前缀<br/>
     * 按需要修改
     */
    private RedisCacheConfiguration cacheConfigAddkeyPrefix(RedisCacheConfiguration redisCacheConfiguration) {
        if (!Objects.equals(true,useKeyPrefix) && StringUtils.isBlank(keyPrefix)) {
            return redisCacheConfiguration;
        }

        String keyPrefixNew =  StringUtils.isNotBlank(keyPrefix) ? keyPrefix : appName;
        if (StringUtils.isBlank(keyPrefixNew)) {
            String systemPath = System.getProperty("user.dir");
            keyPrefixNew = systemPath.substring(systemPath.lastIndexOf(File.separator)+1);
        }
        return redisCacheConfiguration.prefixCacheNameWith(keyPrefixNew + CacheKeyPrefix.SEPARATOR);
    }

    /**
     * 将需要保存的数据序列化
     */
    private Jackson2JsonRedisSerializer getJackson2JsonRedisSerializer() {
        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 解决jackson2无法反序列化LocalDateTime的问题
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        om.activateDefaultTyping( LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL );

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }
}
