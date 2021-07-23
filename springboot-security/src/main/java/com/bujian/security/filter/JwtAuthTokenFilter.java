package com.bujian.security.filter;

import com.bujian.security.bean.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Value("${token.header}")
    private String header;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 获取 token
        String token = request.getHeader(this.header);
        // 解密 token 获取 登录用户信息
        LoginUser loginUser = parseToken(token);
        if (loginUser != null) {
            // 校验 token 是否合法
            verifyToken(token);
            // 将 用户 交给 Security 管理
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

    // 验证/更新 token
    private void verifyToken(String token) {
    }

    private LoginUser parseToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (!Objects.equals("token", token)) {
            return null;
        }
        // 解密获得的用户信息
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("admin");
        loginUser.setPassword("$2a$10$LWAq6mF/6g1sVY4pQ413oukQfEL6uJBZ6h8re7SnsMSOUmQN7YVPK");
        loginUser.setRoles("admin");
        return loginUser;
    }

}
