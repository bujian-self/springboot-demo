# springboot 项目 第十一次 搭建

* 添加全局一个简单的全局异常处理`GlobalExceptionHandler`

1. yaml配置文件 添加`Sa-Token`配置(其他修改请对比文件查看)

```java

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
```

2. 在`application.yml`添加

```yml
#出现错误时, 直接抛出异常
spring:
  mvc:
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false
```

其他

+ 修改部分项目结构

---

### [springboot 项目 第十次 搭建](https://github.com/bujian-self/springboot-demo/blob/main/springboot-satoken/HELP.md)
