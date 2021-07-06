package com.bujian.knife4j.service;

import com.bujian.knife4j.bean.FuzideshilianDo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest // 启动整个spring框架进行单元测试
class FuzideshilianServiceTest {

    @Autowired
    public FuzideshilianService testService;

    @Test
    void selectByBean() {
        FuzideshilianDo fuzideshilianDo = testService.getById(-1);
        System.err.println(fuzideshilianDo);
    }

    @Test
    void selectById() {
        FuzideshilianDo bean = new FuzideshilianDo();
        bean.setId(1);
        List<FuzideshilianDo> fuzideshilianDos = testService.selectByBean(bean);
        System.err.println(fuzideshilianDos);
    }
}
