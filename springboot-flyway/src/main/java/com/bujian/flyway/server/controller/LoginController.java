package com.bujian.flyway.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.bujian.flyway.server.service.LoginUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录 控制器
 *
 * @author bujian
 * @date 2021/7/23
 */
@Api(tags = "登录模块")
@RestController
@Slf4j
public class LoginController {

    @Autowired
    LoginUserService loginService;

    @ApiImplicitParam(name = "json", value = "用户登录信息", required = true, example = "{'username':'user','password':'123456'}")
    @ApiOperation(value = "用户登录")
    @PostMapping("${swagger.pathMapping:/anon}" + "/login")
    public Object login(@RequestBody JSONObject json) {
        log.info("用户登录: {}", json);
        // TODO login user Front check
        String username = json.getString("username");
        String password = json.getString("password");
        return JSONObject.parseObject(loginService.login(username, password));
    }

    @ApiImplicitParam(name = "json", value = "退出登录时包含的信息", required = true)
    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public Object logout() {
        return loginService.logout();
    }

    @ApiOperation(value = "刷新token")
    @GetMapping("/refreshToken")
    public Object refreshToken() {
        //TODO refreshToken
        return "loginService.refreshToken()";
    }

    @ApiOperation(value = "hello")
    @GetMapping("/hello")
    public Object hello() {
        boolean admin = StpUtil.hasRole("admin");
        System.err.println(admin);
        boolean admin1 = StpUtil.hasPermission("admin");
        System.err.println(admin1);
        return "hello";
    }

}
