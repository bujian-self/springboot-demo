package com.bujian.security.utils;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * jwt 工具类
 * @author bujian
 * @date 2021/7/25
 */
@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = 1L;

    @Value("${token.header:Authorization}")
    private String header;
    @Value("${token.secret:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ}")
    private String secret;
    @Value("${token.expireTime:10m}")
    private Duration expireTime;
    private SignatureAlgorithm hs256 = SignatureAlgorithm.HS256;

    private Key getKey() {
        if (secret.length() < 44) {
            // hs256 要求数据格式长度 最少 44字符
            secret = String.join("", Collections.nCopies(44/ secret.length()+1, secret));
        }
        return new SecretKeySpec(DatatypeConverter.parseBase64Binary(secret), hs256.getJcaName());
    }

    /**
     * 快速创建 jwt 默认时间
     */
    public String creatJwt(Map<String, Object> claims) {
        Date end = null;
        if (expireTime != null && expireTime.getSeconds() > 0) {
            end = new Date(System.currentTimeMillis() + expireTime.toMillis());
        }
        return this.creatJwt(claims,end);
    }

    /**
     * 快速创建 指定过期时间
     */
    public String creatJwt(Map<String, Object> claims, Date expiration) {
        return this.creatJwt(claims, null, null, null, null, expiration);
    }

    /**
     * 创建 生成 完整jwt
     */
    public String creatJwt(Map<String, Object> claims, String id, String subject, String issuer, String audience, Date expiration) {
        return Jwts.builder().setId(id).setSubject(subject).setIssuer(issuer).setIssuedAt(new Date(System.currentTimeMillis()))
                .setAudience(audience).setExpiration(expiration).addClaims(claims).signWith(hs256, getKey()).compact();
    }

    /**
     * token是否过期
     */
    public boolean isTimout(String token) {
        try {
            Claims claims = decryptJwt(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取 token
     */
    public String getToken() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return request.getHeader(this.header);
    }

    /**
     * 解密 jwt
     */
    public Claims decryptJwt(String token) {
        try {
            return Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultClaims();
    }

    /**
     * Claims 转 对象
     */
    public <T> T claims2JavaObject(Claims claims, Class<T> clazz){
        Assert.isTrue(claims!=null , "未获取到 claims");
        Assert.isTrue(clazz!=null , "Class 不能为空");
        return claims.size() < 1 ? null : JSONObject.parseObject(JSONObject.toJSONString(claims), clazz);
    }

    /**
     * 刷新 jwt 时间
     */
    public String refreshToken() {
        String token = this.getToken();
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return this.refreshJwt(token);
    }

    /**
     * 刷新 jwt 时间
     */
    public String refreshJwt(String token) {
        Claims claims = decryptJwt(token);
        Date end = null;
        if (expireTime != null && expireTime.getSeconds() > 0) {
            end = new Date(System.currentTimeMillis() + expireTime.toMillis());
        }
        return Jwts.builder().setClaims(claims).setExpiration(end).signWith(hs256, getKey()).compact();
    }
}
