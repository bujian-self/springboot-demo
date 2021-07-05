package com.bujian.interceptor.mapper;

import com.bujian.interceptor.bean.FuzideshilianDo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest // 启动整个spring框架进行单元测试
class FuzideshilianMapperTest {

    @Autowired
    public FuzideshilianMapper testMapper;

    @Test
    void selectByBean() {
        List<FuzideshilianDo> fuzideshilianDos = testMapper.selectByBean(null);
        System.err.println(fuzideshilianDos);
    }
}
