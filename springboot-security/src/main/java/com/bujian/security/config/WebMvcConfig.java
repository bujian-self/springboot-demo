package com.bujian.security.config;

import com.bujian.security.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 构建mvc配置 添加了1个拦截器
 * @author bujian
 * @date 2021/7/5 18:01
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestInterceptor reqInter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(reqInter).addPathPatterns("/**");
    }
}
