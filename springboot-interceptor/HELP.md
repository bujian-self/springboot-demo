# springboot 项目 第六次 搭建
* 使用`WebMvcConfg`添加`interceptor`拦截器
```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private String prefix = "/anon";
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
        registry.addInterceptor(autInter).excludePathPatterns(prefix + "/**");
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
        // TODO 授权 校验
        return true;
    }
}
```
* 更新修改`SwaggerConfig`配置  
1 添加 SwaggerUI资源 新的访问路径
```
private String prefix = "/anon";
@Bean
public SimpleUrlHandlerMapping swaggerUrlHandlerMapping(ServletContext servletContext, @Value("${swagger.mapping.order:10}") int order) throws Exception {
    SimpleUrlHandlerMapping urlHandlerMapping = new SimpleUrlHandlerMapping();
    Map<String, ResourceHttpRequestHandler> urlMap = new HashMap<>(16);
    {
        PathResourceResolver pathResourceResolver = new PathResourceResolver();
        pathResourceResolver.setAllowedLocations(new ClassPathResource("META-INF/resources/webjars/"));
        pathResourceResolver.setUrlPathHelper(new UrlPathHelper());

        ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
        resourceHttpRequestHandler.setLocations(Arrays.asList(new ClassPathResource("META-INF/resources/webjars/")));
        resourceHttpRequestHandler.setResourceResolvers(Arrays.asList(pathResourceResolver));
        resourceHttpRequestHandler.setServletContext(servletContext);
        resourceHttpRequestHandler.afterPropertiesSet();
        //设置新的路径
        urlMap.put(prefix + "/webjars/**", resourceHttpRequestHandler);
    }
    {
        PathResourceResolver pathResourceResolver = new PathResourceResolver();
        pathResourceResolver.setAllowedLocations(new ClassPathResource("META-INF/resources/"));
        pathResourceResolver.setUrlPathHelper(new UrlPathHelper());

        ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
        resourceHttpRequestHandler.setLocations(Arrays.asList(new ClassPathResource("META-INF/resources/")));
        resourceHttpRequestHandler.setResourceResolvers(Arrays.asList(pathResourceResolver));
        resourceHttpRequestHandler.setServletContext(servletContext);
        resourceHttpRequestHandler.afterPropertiesSet();
        //设置新的路径
        urlMap.put(prefix + "/**", resourceHttpRequestHandler);
    }
    urlHandlerMapping.setUrlMap(urlMap);
    //调整DispatcherServlet关于SimpleUrlHandlerMapping的排序
    urlHandlerMapping.setOrder(order);
    return urlHandlerMapping;
}

/**
 * 重写 SwaggerUI 接口访问
 */
@Controller
@ApiIgnore
@RequestMapping(value = "${server.anonymous:/anon}")
public static class SwaggerResourceController implements InitializingBean {

    @Autowired
    private ApiResourceController apiResourceController;

    @Autowired
    private Environment environment;

    @Autowired
    private DocumentationCache documentationCache;

    @Autowired
    private ServiceModelToSwagger2Mapper mapper;

    @Autowired
    private JsonSerializer jsonSerializer;

    private Swagger2Controller swagger2Controller;

    @Override
    public void afterPropertiesSet() {
        swagger2Controller = new Swagger2Controller(environment, documentationCache, mapper, jsonSerializer);
    }

    @RequestMapping("/swagger-resources/configuration/security")
    @ResponseBody
    public ResponseEntity<SecurityConfiguration> securityConfiguration() {
        return apiResourceController.securityConfiguration();
    }

    @RequestMapping("/swagger-resources/configuration/ui")
    @ResponseBody
    public ResponseEntity<UiConfiguration> uiConfiguration() {
        return apiResourceController.uiConfiguration();
    }

    @RequestMapping("/swagger-resources")
    @ResponseBody
    public ResponseEntity<List<SwaggerResource>> swaggerResources() {
        return apiResourceController.swaggerResources();
    }

    @RequestMapping(value = "/v2/api-docs", method = RequestMethod.GET, produces = {"application/json", "application/hal+json"})
    @ResponseBody
    public ResponseEntity<Json> getDocumentation(
            @RequestParam(value = "group", required = false) String swaggerGroup,
            HttpServletRequest servletRequest) {
        return swagger2Controller.getDocumentation(swaggerGroup, servletRequest);
    }
}
```
2 对 SwaggerUI 添加全局访问的请求头
```
 在 build 后面添加 全局token
@Bean
public Docket docket() {
    return new Docket(DocumentationType.SWAGGER_2)
    .apiInfo(apiInfo())
    .select()
    // 指定 @RestController 注解的类
    .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
    .paths(PathSelectors.any())
    .build()
    // 全局token
    .securitySchemes(securitySchemes())
    .securityContexts(securityContexts());
}

  设置请求头和请求的展示
private List<SecurityContext> securityContexts() {
    List<SecurityContext> securityContextList = new ArrayList<>();
    List<SecurityReference> securityReferenceList = new ArrayList<>();
    securityReferenceList.add(new SecurityReference(header, scopes()));
    securityContextList.add(SecurityContext
            .builder()
            .securityReferences(securityReferenceList)
            .forPaths(PathSelectors.any())
            .build()
    );
    return securityContextList;
}
private AuthorizationScope[] scopes() {
    return new AuthorizationScope[]{new AuthorizationScope("global", "accessAnything")};
}
private List<ApiKey> securitySchemes() {
    List<ApiKey> apiKeyList= new ArrayList();
    apiKeyList.add(new ApiKey(header, header, "header"));
    return apiKeyList;
}
```

---
### [springboot 项目 第五次 搭建](https://github.com/lijiepersion/springboot-demo/blob/main/springboot-logger/HELP.md)
