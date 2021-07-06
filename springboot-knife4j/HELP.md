# springboot 项目 第七次 搭建
* 升级`swagger`改为`knife4j`生成API文档 ( 兼容`swagger`)
1. 将`swagger-ui`替换成`knife4j`即可
```xml
<dependency>
  <groupId>com.github.xiaoymin</groupId>
  <artifactId>knife4j-spring-boot-starter</artifactId>
  <version>2.0.7</version>
</dependency>
```
2. 将`swagger-ui`添加的全局访问请求头去除,`knife4j`页面上自带有设置的地方

* 优化`WebMvcConfig`中`AuthorizeInterceptor`拦截器的设置
```
registry.addInterceptor(autInter)
        .addPathPatterns("/**")
        .excludePathPatterns(prefix)
        .excludePathPatterns(prefix +"/**")
        .excludePathPatterns("/error");
```

* 添加`MybatisPlusConfig`配置开启分页插件
  
  [mybatis-plus官网分页插件设置](https://mp.baomidou.com/guide/page.html)
  
  升级`mybatis-plus`版本
```xml
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>mybatis-plus-boot-starter</artifactId>
  <version>3.4.3</version>
</dependency>
```
  开启分页插件
```java
@Configuration
@MapperScan("com.baomidou.cloud.service.*.mapper*")
public class MybatisPlusConfig {
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
    return interceptor;
  }

}
```
升级`mysql`配置版本
```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.25</version>
</dependency>
```

* 整改项目减少crud开发

  编写`CrudApi`文件
```java
public class CrudApi<S extends IService<V>, V> {
  private final Class<V> clazz;
  @Autowired
  protected S baseService;
  public CrudApi() {
    clazz = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
  }
  public S getBaseService() {
    return this.baseService;
  }

  @ApiImplicitParam(name = "vo", value = "实体类", required = true)
  @ApiOperation(value = "创建对象")
  @PostMapping(value = "/")
  public Object create(@RequestBody V vo) {
    return baseService.save(vo);
  }

  @ApiImplicitParam(name = "vo", value = "实体类数据必须包含主键", required = true)
  @ApiOperation(value = "主键更新对象")
  @PutMapping(value = "/")
  public Object updateById(@RequestBody V vo) {
    return baseService.updateById(vo);
  }

  @ApiImplicitParam(name = "id", value = "主键", required = true, example = "1")
  @ApiOperation(value = "主键查询")
  @GetMapping(value = "/{id}")
  public Object select(@PathVariable(value = "id") Serializable id) {
    return baseService.getById(id);
  }

  @ApiImplicitParam(name = "ids", value = "主键", required = true, example = "1", dataType = "List")
  @ApiOperation(value = "主键删除对象")
  @DeleteMapping(value = "/{ids}")
  public Object delete(@PathVariable(value = "ids") List<Serializable> ids) {
    return baseService.removeByIds(ids);
  }

  /**
   * 包含分页查询的数据
   */
  @ApiImplicitParam(name = "json", value = "包含分页参数的实体类json", required = true)
  @ApiOperation(value = "实体类分页查询对象")
  @PostMapping(value = "/searchEntity")
  public Object searchEntity(@RequestBody JSONObject json) {
    IPage<V> iPage = new Page<>(json.getIntValue("current"), json.getIntValue("size"));
    QueryWrapper<V> queryWrapper = new QueryWrapper<>();
    if (!json.isEmpty()) {
      queryWrapper.setEntity(json.toJavaObject(clazz));
    }
    return baseService.page(iPage, queryWrapper);
  }
}
```
  `controller`层继承该文件,并将`Service`和实体类传入该层
```java
public class XXXController extends CrudApi<XXXService,XXXDo> {}
```
  `Service`接口继承`IService`文件,并将实体类传入该层
```java
public interface XXXService extends IService<XXXDo>{}
```
  `ServiceImpl`实现继承`ServiceImpl`文件,并将`DAO`和实体类传入该层
```java
public class XXXServiceImpl extends ServiceImpl<XXXMapper, XXXDo> implements XXXService  {}
```
* 最后统一修改`CrudApi`在`Swagger2`上显示的api文档
  
---
### [springboot 项目 第六次 搭建](https://github.com/lijiepersion/springboot-demo/blob/main/springboot-interceptor/HELP.md)
