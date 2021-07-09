package com.bujian.cache.utils;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest // 启动整个spring框架进行单元测试
public class RedisUtilTest {

    @Autowired
    public RedisUtil redisUtil;

    @Test
    void set() {
        redisUtil.set("key","value");
    }
}
