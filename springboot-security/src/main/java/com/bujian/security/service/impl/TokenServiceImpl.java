package com.bujian.security.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bujian.security.bean.LoginUser;
import com.bujian.security.service.CacheService;
import com.bujian.security.service.TokenService;
import com.bujian.security.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * security 用户登录
 * @author bujian
 * @date 2021/7/23
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${token.expireTime:10m}")
    private Duration expireTime;
    @Autowired
    private CacheService cacheService;

    private String tokenKey = "loginUser::";

    @Override
    public String creatToken(LoginUser loginUser) {
        Assert.isTrue(loginUser!=null,"用户不能为空");
        // 去除登录用户密码 保证安全
        loginUser.setPassword(null);
        Assert.isTrue(loginUser.getUsername()!=null && loginUser.getRoles()!=null,"用户不能为空");
        // 创建 token
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("username",loginUser.getUsername());
        claims.put("roles",loginUser.getRoles());
        return jwtTokenUtil.creatJwt(claims);
    }

    @Override
    public boolean removeToken() {
        String token = jwtTokenUtil.getToken();
        Claims claims = jwtTokenUtil.decryptJwt(token);
        // 存储 token黑名单 到 rides
        JSONObject tokenJson = cacheService.getJson(tokenKey + claims.get("username"));
        if (tokenJson != null) {
            for (Map.Entry<String, Object> entity : tokenJson.entrySet()) {
                // 移除超时的token
                if (((long) entity.getValue()) < System.currentTimeMillis()){
                    tokenJson.remove(entity.getKey());
                }
            }
        }else{
            tokenJson = new JSONObject();
        }
        tokenJson.put(token,claims.getExpiration().getTime());
        cacheService.saveOrUpdate(tokenKey + claims.get("username"), tokenJson, expireTime.getSeconds());
        return true;
    }

    @Override
    public String refreshToken() {
        // 先删除旧 token
        this.removeToken();
        // 刷新 token
        return jwtTokenUtil.refreshToken();
    }

    @Override
    public String getToken() {
        return jwtTokenUtil.getToken();
    }

    @Override
    public LoginUser parseToken() {
        String token = jwtTokenUtil.getToken();
        Claims claims = jwtTokenUtil.decryptJwt(token);
        return jwtTokenUtil.claims2JavaObject(claims, LoginUser.class);
    }

    @Override
    public boolean verifyToken() {
        String token = jwtTokenUtil.getToken();
        // 自定义唯一码
        Claims claims = jwtTokenUtil.decryptJwt(token);
        // 保存的key
        JSONObject tokenJson = cacheService.getJson(tokenKey + claims.get("username"));
        if (tokenJson == null) {
            return true;
        }
        Long end = tokenJson.getLong(token);
        if (end == null) {
            return true;
        }
        if (end< System.currentTimeMillis()) {
            return true;
        }
        throw new IllegalArgumentException("登录超时,请重新登录");
    }
}
