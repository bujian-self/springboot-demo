package com.bujian.security.controller;


import com.alibaba.fastjson.JSONObject;
import com.bujian.security.bean.LoginUser;
import com.bujian.security.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {

    @Autowired
    private LoginUserService loginService;

    @PostMapping("/anon/login")
    public Object login(@RequestBody JSONObject json){
        // TODO login user Front check
        String username = json.getString("username");
        String password = json.getString("password");
        return loginService.login(username, password);
    }

    @RequestMapping("/logout")
    public Object logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @RequestBody String data){
        System.err.println("logout");
        //TODO logout
        return "logout";
    }

    @RequestMapping("/hello")
    public Object hello(Authentication authentication){
        return authentication.getPrincipal();
    }

}
