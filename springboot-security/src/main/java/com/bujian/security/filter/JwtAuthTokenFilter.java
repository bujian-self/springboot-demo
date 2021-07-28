package com.bujian.security.filter;

import com.bujian.security.bean.LoginUser;
import com.bujian.security.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * 登录 过滤器
 * @author bujian
 * @date 2021/7/23
 */
@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 获取 token
        String token = tokenService.getToken();
        if (StringUtils.isBlank(token)) {
            chain.doFilter(request, response);
            return;
        }
        // 解密 token 获取 登录用户信息
        LoginUser loginUser = tokenService.parseToken();
        if (loginUser == null) {
            chain.doFilter(request, response);
            return;
        }
        // 校验 token 是否合法
        tokenService.verifyToken();
        // 将 用户 交给 Security 管理
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);
    }
}
