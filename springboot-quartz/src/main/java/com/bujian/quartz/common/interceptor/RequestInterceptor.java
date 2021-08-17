package com.bujian.quartz.common.interceptor;

import com.bujian.quartz.common.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 所有请求拦截器
 * 用于记录请求
 *
 * @author bujian
 * @date 2021/7/5 18:00
 */
@Configuration
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("接收到请求: {}", request.getRequestURI());
        ThreadLocalUtil.put("start", System.currentTimeMillis());
        ThreadLocalUtil.put("url", request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("请求响应完成,耗时: {}ms, 请求路径: {}", System.currentTimeMillis() - (Long) ThreadLocalUtil.get("start"), ThreadLocalUtil.get("url"));
        ThreadLocalUtil.removeAll();
    }
}
