package com.bujian.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Objects;

/**
 * 缓存配置文件
 * 该类初始化的前提：Redis自动配置
 * 该类只会上下文不存在CacheManager Bean的情况下初始化
 * @author bujian
 * @date 2021/7/7 18:12
 */
@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.cache.redis.key-prefix:}")
    private String keyPrefix;
    @Value("${spring.cache.redis.time-to-live:0}")
    private Duration timeToLive;

    /**
     * redisTemplate 模板
     * 方便手动调用,比较灵活
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 配置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //redis 开启事务
        redisTemplate.setEnableTransactionSupport(true);
        // key 序列化
        RedisSerializer keySerializer = new MyStringSerializer(keyPrefix);
//        RedisSerializer keySerializer = new MyObjectSerializer(keyPrefix);
//        RedisSerializer keySerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        RedisSerializer valueSerializer = this.getJackson2JsonRedisSerializer();
        // value 序列化
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    /**
     * 将需要保存的数据序列化
     */
    private Jackson2JsonRedisSerializer getJackson2JsonRedisSerializer() {
        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 解决jackson2无法反序列化LocalDateTime的问题
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.activateDefaultTyping( LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL );

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
                .cacheDefaults(this.getRedisCacheConfiguration())
                .build();
    }

    /**
     * 有使用cache相关注解的地方才有用
     * @author bujian
     * @date 2021/7/9 17:43
     */
    private RedisCacheConfiguration getRedisCacheConfiguration() {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                // 超时时间
                .entryTtl(timeToLive)
                // key 前缀
                .computePrefixWith(cacheName -> keyPrefix + (Objects.equals("null",cacheName.toLowerCase()) ? "" : (cacheName + "::")))
                // key 序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // value 序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(getJackson2JsonRedisSerializer()))
                // 不缓存 null 值
                .disableCachingNullValues()
            ;
    }
}
