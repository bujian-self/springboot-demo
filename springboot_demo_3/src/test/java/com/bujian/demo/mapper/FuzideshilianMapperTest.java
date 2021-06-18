package com.bujian.demo.mapper;

import com.bujian.demo.bean.FuzideshilianDo;
import com.bujian.demo.service.FuzideshilianService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
