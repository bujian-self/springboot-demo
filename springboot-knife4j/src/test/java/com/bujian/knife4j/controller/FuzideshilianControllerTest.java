package com.bujian.knife4j.controller;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest// 启动整个spring框架进行单元测试
class FuzideshilianControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach //测试 前执行
    public void setupMockMvc(){
        //初始化 所有MockMvc对象
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void findInfoById() throws Exception {
        System.err.println(
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/fuzideshilian/findInfoById/1")
                ).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn().getResponse().getContentAsString()//将相应的数据转换为字符串
        );
    }

    @Test
    void findInfo() throws Exception {
        System.err.println(
                mockMvc.perform(
                        MockMvcRequestBuilders.post("/fuzideshilian/findInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONObject.toJSONString(JSONObject.parse(
                                "{'question':'李白','createTime':'2019-12-18 01:40:53'}"
                        )))
                ).andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn().getResponse().getContentAsString()//将相应的数据转换为字符串
        );
    }
}
