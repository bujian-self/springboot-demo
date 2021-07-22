package com.bujian.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {
    @GetMapping("/anon/login")
    public Object login(){
        //TODO login
        return "login";
    }
    @RequestMapping("/logout")
    public Object logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @RequestBody String data){
        System.err.println("logout");
        //TODO logout
        return "logout";
    }
}
