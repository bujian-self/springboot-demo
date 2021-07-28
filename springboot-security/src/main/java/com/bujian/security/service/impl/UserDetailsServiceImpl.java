package com.bujian.security.service.impl;

import com.bujian.security.bean.LoginUser;
import com.bujian.security.service.LoginUserService;
import com.bujian.security.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * security 用户登录
 * @author bujian
 * @date 2021/7/23
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService, LoginUserService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @Override
    public LoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
        Assert.isTrue(StringUtils.isNotBlank(username) , "登录账户不能为空");
        // TODO 数据库 查询 用户 逻辑
        LoginUser user = null;
        if (Objects.equals("admin",username)) {
            user = new LoginUser();
            user.setUsername(username);
            user.setPassword("$2a$10$LWAq6mF/6g1sVY4pQ413oukQfEL6uJBZ6h8re7SnsMSOUmQN7YVPK");
            user.setRoles("admin","edit");
        }
        // 验证 用户
        Assert.isTrue(user != null , "用户不存在");
        // 返回 登录 用户
        return user;
    }

    @Override
    public String login(String username, String password) {
        // 用户 认证
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        // 获取 登录用户信息 创建 token 返回
        return tokenService.creatToken((LoginUser) authenticate.getPrincipal());
    }

    @Override
    public String refreshToken() {
        return tokenService.refreshToken();
    }

    @Override
    public String logout() {
        if (tokenService.removeToken()) {
            return "删除成功";
        }
        return "删除失败";
    }

}
