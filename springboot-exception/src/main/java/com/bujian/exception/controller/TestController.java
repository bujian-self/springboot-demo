package com.bujian.exception.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第一个 测试请求
 *
 * @author bujian
 * @date 2021/6/18 09:51
 */
@RestController
@RequestMapping("/anon")
public class TestController {

    @GetMapping("/test1")
    @ResponseBody
    public Object hello1() {
        throw new NullPointerException();
    }

    @GetMapping("/test2")
    @ResponseBody
    public Object hello2() {
        throw new IllegalArgumentException("这是一个参数异常");
    }
}
