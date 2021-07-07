# springboot 项目 第八次 搭建
* 添加`cache`缓存
1. 添加`CacheConfig`配置文件
```java

@EnableCaching
@Configuration
public class CacheConfig {

  @Bean
  public ConcurrentMapCacheManager cacheManager() {
    return new ConcurrentMapCacheManager();
  }

  /**
   * 自定义key生成规则
   */
  @Bean(name = "CrudKeyGen")
  public KeyGenerator CrudKeyGen() {
    return new KeyGenerator() {
      @Override
      public Object generate(Object target, Method method, Object... params) {
        // 获取目标方法的泛型入参 第二个参数是实体类的参数
        Type[] actualTypeArguments = ((ParameterizedType) target.getClass().getGenericSuperclass()).getActualTypeArguments();
        String name = ((Class) actualTypeArguments[actualTypeArguments.length - 1]).getSimpleName();
        //PRO:USER:UID:18 命名规范
        return name  + ":" + params[0];
      }
    };
  }
}
```
2. 在需要缓存的地方添加缓存相关的注解

 [cache注解](https://blog.csdn.net/qq_32448349/article/details/101696892)

---
### [springboot 项目 第七次 搭建](https://github.com/lijiepersion/springboot-demo/blob/main/springboot-knife4j/HELP.md)
