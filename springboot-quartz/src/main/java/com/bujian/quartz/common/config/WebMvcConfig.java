package com.bujian.quartz.common.config;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.bujian.quartz.common.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 构建mvc配置 添加了2个拦截器
 *
 * @author bujian
 * @date 2021/7/5 18:01
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 设置请求的统一前缀
     */
    @Value("${swagger.pathMapping:/anon}")
    private String pathMapping;

    @Autowired
    private RequestInterceptor reqInter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(reqInter).addPathPatterns("/**");
        // 注册注解拦截器
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**").excludePathPatterns("");

        // 设置sa路由
        SaRouteInterceptor saRouteInterceptor = new SaRouteInterceptor((request, response, handler) -> {
            if (!request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
                // 检验当前会话是否已经登录, 如果未登录，则抛出异常：`NotLoginException`
                StpUtil.checkLogin();
            }
        });
        // 注册Sa-Token的路由拦截器，并排除登录接口或其他可匿名访问的接口地址 (与注解拦截器无关)
        registry.addInterceptor(saRouteInterceptor).addPathPatterns("/**").excludePathPatterns(this.pathMapping + "/**", "/error");
    }
}
