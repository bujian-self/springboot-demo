package com.bujian.quartz.common.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * 全局异常信息</br>
 * ControllerAdvice 注解拦截所有@Controller接口</ br>
 *
 * @author bujian
 * @date 2021/8/5
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public HashMap<String, Object> exceptionHnndler(HttpServletRequest req, HttpServletResponse resp, Exception e) {
        e.printStackTrace();
        HashMap<String, Object> map = new HashMap<>(4);
        map.put("timestamp", LocalDateTime.now() + "+00:00");
        map.put("status", 500);
        map.put("error", StringUtils.isNoneBlank(e.getMessage()) ? e.getMessage() : StringUtils.substringAfterLast(e.toString(), "."));
        map.put("path", req.getRequestURI());
        return map;
    }
}
