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

3. 升级本地缓存为`redis`[教程](https://www.runoob.com/redis/redis-tutorial.html)
   
   在`maven`的`pom.xml`文件中添加以下内容
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
   在`application.yml`文件中添加以下内容
```yaml
spring:
  cache:
    type: redis
    redis:
      use-key-prefix: true # 开启前缀
      key-prefix: ${spring.application.name} # 前缀名称
      cache-null-values: false #是否缓存空值
      time-to-live: 1h #缓存超时时间
# redis 配置
  redis:
    # 默认16个分片
    database: 0
    host: redis-18419.c259.us-central1-2.gce.cloud.redislabs.com
    port: 18419
    password: eJwEgP1ufA81If7BYirHysAqZako3kxZ
    timeout: 10000
```
4. 修改配置文件`CacheConfig`为`RedisConfig`
```java
@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

   @Value("${spring.cache.redis.key-prefix:}")
   private String keyPrefix;
   @Value("${spring.cache.redis.time-to-live:0}")
   private Duration timeToLive;

   /**
    * redisTemplate 模板
    * 方便手动调用,比较灵活
    */
   @Bean(name = "redisTemplate")
   public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
      StringRedisSerializer keySerializer = new StringRedisSerializer();
      // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
      Jackson2JsonRedisSerializer valueSerializer = this.getJackson2JsonRedisSerializer();

      RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
      // 配置连接工厂
      redisTemplate.setConnectionFactory(redisConnectionFactory);
      // key 序列化
      redisTemplate.setKeySerializer(keySerializer);
      redisTemplate.setHashKeySerializer(keySerializer);
      // value 序列化
      redisTemplate.setValueSerializer(valueSerializer);
      redisTemplate.setHashValueSerializer(valueSerializer);
      redisTemplate.afterPropertiesSet();
      return redisTemplate;
   }
   /**
    * 将需要保存的数据序列化
    */
   private Jackson2JsonRedisSerializer getJackson2JsonRedisSerializer() {
      //解决查询缓存转换异常的问题
      ObjectMapper om = new ObjectMapper();
      // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
      om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
      // 解决jackson2无法反序列化LocalDateTime的问题
      om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      om.registerModule(new JavaTimeModule());
      // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
      om.activateDefaultTyping( LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL );

      Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
      jackson2JsonRedisSerializer.setObjectMapper(om);
      return jackson2JsonRedisSerializer;
   }

   @Bean
   public CacheManager cacheManager(RedisConnectionFactory factory) {
      return RedisCacheManager
              .builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
              .cacheDefaults(this.getRedisCacheConfiguration())
              .build();
   }

   private RedisCacheConfiguration getRedisCacheConfiguration() {
      return RedisCacheConfiguration
              .defaultCacheConfig()
              // 超时时间
              .entryTtl(timeToLive)
              // key 前缀
              .computePrefixWith(cacheName -> keyPrefix + cacheName + "::")
              // key 序列化
              .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
              // value 序列化
              .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(getJackson2JsonRedisSerializer()))
              // 不缓存 null 值
              .disableCachingNullValues()
              ;
   }
}
```
 
---
### [springboot 项目 第七次 搭建](https://github.com/lijiepersion/springboot-demo/blob/main/springboot-knife4j/HELP.md)
