package com.bujian.interceptor.config;

import com.bujian.interceptor.interceptor.AuthorizeInterceptor;
import com.bujian.interceptor.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 构建mvc配置 添加了2个拦截器
 * @author lijie
 * @date 2021/7/5 18:01
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${server.interface.anonymous:/anon}")
    private String prefix;

    @Autowired
    private RequestInterceptor reqInter;
    @Autowired
    private AuthorizeInterceptor autInter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(reqInter).addPathPatterns("/**");
        registry.addInterceptor(autInter).excludePathPatterns(prefix +"/**");
    }
}
