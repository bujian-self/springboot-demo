package com.bujian.cache.interceptor;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户登录权限拦截器
 * @author bujian
 * @date 2021/7/5 17:59
 */
@Configuration
@Slf4j
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Value("${server.interface.header:Authorization}")
    private String header;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String auth = request.getHeader(header);
        if (StringUtils.isBlank(auth) || !vaildAuth(auth)) {
            response.setContentType("application/json; charset="+request.getCharacterEncoding());
            response.getWriter().write(JSONObject.toJSONString(JSONObject.parseObject("{'time':'"+ LocalDateTime.now().toString().replace("T"," ")+"','status':'fail','message':'权限不足'}")));
            return false;
        }
        return true;
    }

    /**
     * 校验用户有效性
     * @author bujian
     * @date 2021/7/5 17:40
     * @param auth  用户header参数
     * @return boolean true 合法的 false 不合法的
     */
    private boolean vaildAuth(String auth) {
        // 解密
        String deAuth = auth;
        log.info("登录用户: {}", deAuth);
        // 判定逻辑
        String hasAuth = "admin";
        if (Objects.equals(hasAuth,deAuth)) {
            return true;
        }
        return false;
    }
}
