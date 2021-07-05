# springboot 项目 第六次 搭建
* 使用`WebMvcConfg`添加`interceptor`拦截器
```java
@Configuration
public class WebMvcConfg implements WebMvcConfigurer {
  // 引入的2个拦截器
  @Autowired
  private RequestInterceptor reqInter;
  @Autowired
  private AuthorizeInterceptor autInter;

  /**
   * 添加拦截器拦截的路径
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(reqInter).addPathPatterns("/**");
    registry.addInterceptor(autInter).addPathPatterns(prefix +"/**");
  }
}
```
* `RequestInterceptor`请求拦截器
```java
@Configuration
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    log.info("接收到请求");
    // TODO
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    log.info("请求响应完成");
    // TODO
  }
}
```
* `AuthorizeInterceptor`授权拦截器
```java
@Configuration
@Slf4j
public class AuthorizeInterceptor implements HandlerInterceptor {

  // 请求头文件
  private String header = "Authorization";

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
}
```

---
### [springboot 项目 第五次 搭建](https://github.com/lijiepersion/springboot-demo/blob/main/springboot-logger/HELP.md)
