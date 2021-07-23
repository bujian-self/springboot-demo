package com.bujian.security.controller;


import com.alibaba.fastjson.JSONObject;
import com.bujian.security.service.LoginUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录 控制器
 * @author bujian
 * @date 2021/7/23
 */
@Api(tags="登录模块")
@RestController
@Slf4j
public class LoginController {

    @Autowired
    private LoginUserService loginService;

    @ApiImplicitParam(name = "json", value = "用户登录信息", required = true, example = "{'username':'user','password':'123456'}")
    @ApiOperation(value="用户登录")
    @PostMapping("/anon/login")
    public Object login(@RequestBody JSONObject json){
        log.info("用户登录: {}", json);
        // TODO login user Front check
        String username = json.getString("username");
        String password = json.getString("password");
        return loginService.login(username, password);
    }

    @ApiImplicitParam(name = "json", value = "退出登录时包含的信息", required = true)
    @ApiOperation(value="退出登录")
    @RequestMapping("/logout")
    public Object logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @RequestBody JSONObject json){
        System.err.println("logout");
        //TODO logout
        return "logout";
    }

    @ApiOperation(value="hello")
    @RequestMapping("/hello")
    public Object hello(Authentication authentication){
        return authentication.getPrincipal();
    }

}
