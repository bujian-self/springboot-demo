package com.bujian.start.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第一个 测试请求
 * @author lijie
 * @date 2021/6/18 09:51
 */
@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String hello(){
        return "welcome to springboot demo";
    }
}
