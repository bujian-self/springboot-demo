package com.bujian.security.service.impl;

import com.bujian.security.bean.LoginUser;
import com.bujian.security.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * security 用户登录
 * @author bujian
 * @date 2021/7/23
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService, LoginUserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public LoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询 用户
        LoginUser user = new LoginUser();
        user.setUsername(username);
        user.setPassword("$2a$10$LWAq6mF/6g1sVY4pQ413oukQfEL6uJBZ6h8re7SnsMSOUmQN7YVPK");
        user.setRoles("admin","edit");
        // 验证 用户
        // 返回 登录 用户
        return user;
    }

    @Override
    public String login(String username, String password) {
        // 用户 认证
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        // 获取 登录用户信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        // 返回
        return loginUser.getUsername();
    }
}
