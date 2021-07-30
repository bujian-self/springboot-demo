package com.bujian.satoken.service;

import com.bujian.satoken.bean.FuzideshilianDo;
import com.bujian.satoken.service.impl.RedisSimpleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest // 启动整个spring框架进行单元测试
class RedisSimpleServiceImplTest {

    @Autowired
    RedisSimpleServiceImpl testSer;

    @Test
    void saveOrUpdate() {
        testSer.saveOrUpdate("嘿嘿","哈哈");
    }

    @Test
    void testSaveOrUpdate() {
        testSer.saveOrUpdate("嘿嘿2","哈哈2", 3000);
    }

    @Test
    void setLifeTime() {
        System.err.println(testSer.setLifeTime("嘿嘿2", -2));
    }

    @Test
    void hasKey() {
        System.err.println(testSer.hasKey("嘿嘿2"));
    }

    @Test
    void getLifeTime() {
        System.err.println(testSer.getLifeTime("嘿嘿2"));
    }

    @Test
    void getObj() {
        System.err.println(testSer.getObj("嘿嘿2"));
    }

    @Test
    void get() {
        System.err.println(testSer.get("嘿嘿2", String.class));
    }

    @Test
    void getMap() {
        System.err.println(testSer.getMap("嘿嘿2"));
    }

    @Test
    void getJson() {
        System.err.println(testSer.getJson("嘿嘿2"));
    }

    @Test
    void getList() {
        System.err.println(testSer.getList("嘿嘿2", FuzideshilianDo.class));
    }

    @Test
    void remove() {
        System.err.println(testSer.remove("嘿嘿2"));
    }
}
