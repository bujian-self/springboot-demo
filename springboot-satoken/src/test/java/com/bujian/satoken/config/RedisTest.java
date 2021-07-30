package com.bujian.satoken.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest// 启动整个spring框架进行单元测试
public class RedisTest {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Value("${spring.cache.redis.time-to-live:0}")
    private Duration timeToLive;

    @Test
    public void redisSet() {
        redisTemplate.opsForValue().set("Set value for key","1");
        redisTemplate.opsForValue().set("opsForValueSet2","1",timeToLive.getSeconds(), TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("opsForValueSet3","1", timeToLive);
        redisTemplate.opsForValue().set("opsForValueSet4","1",60L);
    }
}
