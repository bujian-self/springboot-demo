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
      # 开启前缀
      use-key-prefix: true
      # 前缀名称
      key-prefix: '${spring.application.name}::'
      # 是否缓存空值
      cache-null-values: false
      # 缓存超时时间
      time-to-live: 1h 
# redis 配置
  redis:
    # 默认16个分片
    database: 0
    host: 127.0.0.1
    port: 6379
    password: password
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
      // 配置redisTemplate
      RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
      // 配置连接工厂
      redisTemplate.setConnectionFactory(redisConnectionFactory);
      //redis 开启事务
      redisTemplate.setEnableTransactionSupport(true);
      // key 序列化  自己自定义的序列化
      RedisSerializer keySerializer = new MyStringSerializer(keyPrefix);
      redisTemplate.setKeySerializer(keySerializer);
      redisTemplate.setHashKeySerializer(keySerializer);
      // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
      RedisSerializer valueSerializer = this.getJackson2JsonRedisSerializer();
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

   /**
    * 有使用cache相关注解的地方才有用
    * @author bujian
    * @date 2021/7/9 17:43
    */
   private RedisCacheConfiguration getRedisCacheConfiguration() {
      return RedisCacheConfiguration
              .defaultCacheConfig()
              // 超时时间
              .entryTtl(timeToLive)
              // key 前缀  存在null字符串时 将其剔除
              .computePrefixWith(cacheName -> keyPrefix + (Objects.equals("null",cacheName.toLowerCase()) ? "" : (cacheName + "::")))
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
自定义的key值序列化方法
```java
// 字符串 key序列化
public class MyStringSerializer implements RedisSerializer<String> {

   private static final Charset CHARSET = StandardCharsets.UTF_8;
   private String keyPrefix = "";

   public MyStringSerializer(){}
   public MyStringSerializer(String keyPrefix){
      this.keyPrefix = StringUtils.isNotBlank(keyPrefix) ? keyPrefix : "";
   }

   @Override
   public String deserialize(@Nullable byte[] bytes) {
      return (bytes == null ? null : new String(bytes, CHARSET));
   }

   @Override
   public byte[] serialize(@Nullable String string) {
      return (string == null ? null : (keyPrefix+string).getBytes(CHARSET));
   }
}
// object key 序列化
public class MyObjectSerializer<T> implements RedisSerializer<T> {

   private static final Charset CHARSET = StandardCharsets.UTF_8;
   private String keyPrefix = "";

   public MyObjectSerializer(){ }
   public MyObjectSerializer(String keyPrefix){
      this.keyPrefix = StringUtils.isNotBlank(keyPrefix) ? keyPrefix : "";
   }

   @SneakyThrows
   @Override
   public T deserialize(@Nullable byte[] bytes) throws SerializationException {
      if (bytes == null || bytes.length < 1) {
         return null;
      }
      return (T) this.bytesToObject(bytes);
   }
   @SneakyThrows
   @Override
   public byte[] serialize(@Nullable T t) throws SerializationException {
      return (t == null ? null : this.objectToBytes(t));
   }

   /**
    * 字节数组转对象
    */
   private Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
      try(
              ByteArrayInputStream in = new ByteArrayInputStream(bytes);
              ObjectInputStream sIn = new ObjectInputStream(in);
      ){
         return sIn.readObject();
      }
   }
   /**
    * 对象转字节数组
    */
   private byte[] objectToBytes(Object obj) throws IOException {
      try(
              ByteArrayOutputStream out = new ByteArrayOutputStream();
              ObjectOutputStream sOut = new ObjectOutputStream(out);
      ){
         sOut.writeObject(obj);
         sOut.flush();
         byte[] bytes = out.toByteArray();
         // 返回时 batys 数字有7个长度的乱码
         return Arrays.copyOfRange(bytes,7,bytes.length);
      }
   }
}
```
 5. 编写一个简单的redis调用方法
```java
@Component
@Slf4j
public class RedisSimpleService {

   @Value("${spring.cache.redis.time-to-live:0}")
   private Duration timeout;

   @Resource
   private RedisTemplate<String, Object> redisTemplate;

   /**
    * 写入缓存,默认时间
    * @param key   键
    * @param value 值
    */
   public void saveOrUpdate(final String key, Object value) {
      this.saveOrUpdate(key, value, timeout.getSeconds());
   }

   /**
    * 写入缓存,设置时间
    * @param key   键
    * @param value 值
    * @param timeout   超时时间
    */
   public void saveOrUpdate(final String key, Object value, long timeout) {
      redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
   }

   /**
    * 重设key生存时间
    * @param key   键
    * @param timeout  时间
    * @return boolean 成功失败
    */
   public boolean setLifeTime(final String key, long timeout) {
      if (timeout > 0) {
         return this.hasKey(key) ? redisTemplate.expire(key, timeout, TimeUnit.SECONDS) : false;
      }else if (timeout == 0 || timeout == -2) {
         log.warn("当生存时间为 0 或 -2 时,该键:{} 将被删除", key);
         return this.remove(key);
      }else if (timeout == -1) {
         log.warn("当生存时间为 -1 时,该键:{} 将用不过期", key);
         return redisTemplate.persist(key);
      }else{
         throw new IllegalArgumentException("生存时间不规范");
      }
   }

   /**
    * 查 是否存在
    * @param key   键
    * @return boolean  true 存在 false 不存在
    */
   public boolean hasKey(final String key) {
      return redisTemplate.hasKey(key);
   }

   /**
    * 查剩余时间
    * @param key   键
    * @return long 秒
    */
   public long getLifeTime(final String key) {
      return redisTemplate.getExpire(key);
   }

   /**
    * 查 返回obj
    * @param key   键
    * @return Object   返回obj类 不存在 返回null
    */
   public Object getObj(final String key) {
      return this.hasKey(key) ? redisTemplate.opsForValue().get(key) : null;
   }

   /**
    * 查 返回泛型
    * @param key   键
    * @param clazz 转换类的class
    * @return T    泛型
    */
   public <T> T get(final String key, Class<T> clazz) {
      Object obj = this.getObj(key);
      return obj == null ? null : new ObjectMapper().convertValue(obj, clazz);
   }

   /**
    * 查 返回hashMap
    * @param key   键
    * @return Map<String,Object>   返回hashMap
    */
   public HashMap<String, Object> getMap(final String key) {
      return this.get(key, HashMap.class);
   }

   /**
    * 查 返回JSONObject
    * @param key   键
    * @return com.alibaba.fastjson.JSONObject
    */
   public JSONObject getJson(final String key) {
      return this.get(key,JSONObject.class);
   }

   /**
    * 查 返回List
    * @param key   键
    * @param clazz 转换类的class
    * @return java.util.List<T>
    */
   public <T> List<T> getList(final String key, Class<T> clazz) {
      Object obj = this.getObj(key);
      return obj == null ? new ArrayList<>() : JSONObject.parseArray(JSON.toJSONString(obj), clazz);
   }

   /**
    * 删除缓存
    * @param key   键
    * @return boolean  true 成功 false 失败
    */
   public boolean remove(String key) {
      return this.hasKey(key) ? redisTemplate.delete(key) : true;
   }

}
```
 修改 FuzideshilianServiceImpl 改为使用 redis 手动存储
```
/**
 * 一般 先取后存
 */
@Override
public List<FuzideshilianDo> selectByBean(FuzideshilianDo bean) {
   log.info("FuzideshilianDo根据实体类获取信息: {}", bean);
   String key = "FuzideshilianDo::" + JSONObject.toJSONString(bean);
   if (bean != null) {
      log.info("从缓存中获取");
      List<FuzideshilianDo> fuzideshilianDos = redisService.getList(key, FuzideshilianDo.class);
      if (fuzideshilianDos.size() > 0) {
         return fuzideshilianDos;
      }
      log.info("缓存中未获取到信息");
   }
   log.info("从数据库获取");
   List<FuzideshilianDo> fuzideshilianDos = fuzideshilianMapper.selectByBean(bean);
   if (fuzideshilianDos.size() > 0) {
      synchronized (this) {
         if (bean != null && !redisService.hasKey(key)) {
            redisService.saveOrUpdate(key, fuzideshilianDos);
            log.info("加入缓存中成功");
         }
      }
   }
   return fuzideshilianDos;
}

/**
 * 先存后取
 */
public List<FuzideshilianDo> selectByBean2(FuzideshilianDo bean) {
   log.info("FuzideshilianDo根据实体类获取信息: {}", bean);
   String key = "FuzideshilianDo::" + JSONObject.toJSONString(bean);
   if (bean != null && !redisService.hasKey(key)) {
      log.info("从数据库获取");
      List<FuzideshilianDo> fuzideshilianDos = fuzideshilianMapper.selectByBean(bean);
      if (fuzideshilianDos.size() > 0) {
         synchronized (this) {
            if (!redisService.hasKey(key)) {
               redisService.saveOrUpdate(key, fuzideshilianDos);
               log.info("加入缓存中成功");
            }
         }
      }
   }
   log.info("从缓存中获取");
   return redisService.getList(key, FuzideshilianDo.class);
}
```


---
### [springboot 项目 第七次 搭建](https://github.com/lijiepersion/springboot-demo/blob/main/springboot-knife4j/HELP.md)
