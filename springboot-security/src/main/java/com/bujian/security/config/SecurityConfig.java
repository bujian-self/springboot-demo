package com.bujian.security.config;

import com.bujian.security.filter.AuthTokenFilter;
//import com.bujian.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)//开启权限注解,默认是关闭的
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * token认证过滤器
     */
    @Autowired
    private AuthTokenFilter authTokenFilter;
    /**
     * 跨域过滤器
     */
    @Autowired
    private CorsFilter corsFilter;
    /**
     * 允许的匿名路径
     */
    @Value("${server.interface.anonymous:/anon}")
    private String anonUrl;
    /**
     * 强散列哈希加密实现
     */
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CSRF禁用，因为不使用session
        http.csrf().disable();
        // 禁用X-Frame-Options 防止iframe嵌入 ??
        http.headers().frameOptions().disable();
        // 基于token，所以不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 过滤请求 允许匿名访问的路径
        http.authorizeRequests().antMatchers(anonUrl+"/**").anonymous()
                .antMatchers(HttpMethod.GET, "/*.html", "/**/*.html", "/**/*.css", "/**/*.js")
                // 除上面外的所有请求全部需要鉴权认证
                .permitAll().anyRequest().authenticated();
        // 认证失败处理类，前后端分离前端不会302重定向，直接返回403异常
        http.exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());
        // 关闭退出操作，自己从controller里面写退出逻辑
        http.logout().clearAuthentication(true).invalidateHttpSession(true).disable();
        // 添加JWT filter
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加CORS filter
        http.addFilterBefore(corsFilter, AuthTokenFilter.class);
        http.addFilterBefore(corsFilter, LogoutFilter.class);
    }
}
