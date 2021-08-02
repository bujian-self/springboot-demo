# springboot 项目 第八(1)次 搭建

* 使用`CaffeineCache`缓存

1. 本地缓存使用`caffeine`

   在`maven`的`pom.xml`文件中添加以下内容

```xml

<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>2.9.0</version>
</dependency>
```

2. 在`application.yml`文件中添加以下内容

```yaml
spring:
  cache:
    type: caffeine
```

3. 添加`CaffeineConfig`配置文件

   注意:`Caffeine`的`CacheNames`不支持动态编写,所以需要提前在`setCacheNames`中定义好

```java

@Slf4j
@EnableCaching
@Configuration
public class CaffeineConfig {

    @Bean(name = "caffeine")
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAllowNullValues(true);
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(100)
                        .maximumSize(1000)
                        .expireAfterWrite(50, TimeUnit.SECONDS)
        );
        cacheManager.setCacheNames(Arrays.asList("null"));
        return cacheManager;
    }

    /**
     * 用来加载数据
     * */
    @Bean(name = "Cache")
    public Cache cache() {
        return ((CaffeineCache) cacheManager().getCache("null")).getNativeCache();
    }

}
```

4. 缓存注解方法不变

5. 一个简单的调用方法

```java

@Component
public class CacheSimpleService {

    @Autowired
    private Cache cache;

    /**
     * 写入缓存,默认时间
     * @param key   键
     * @param value 值
     */
    public void saveOrUpdate(final String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 查 是否存在
     *
     * @param key 键
     * @return boolean  true 存在 false 不存在
     */
    public boolean hasKey(final String key) {
        return cache.getIfPresent(key) != null;
    }

    /**
     * 查 返回obj
     *
     * @param key 键
     * @return Object   返回obj类 不存在 返回null
     */
    public Object getObj(final String key) {
        return cache.getIfPresent(key);
    }

    /**
     * 查 返回泛型
     *
     * @param key   键
     * @return T    泛型
     */
    public <T> T get(final String key) {
        Object obj = this.getObj(key);
        return obj == null ? null : new ObjectMapper().convertValue(obj, clazz);
    }

    /**
     * 查 返回hashMap
     * @param key   键
     * @return Map<String, Object>   返回hashMap
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
        return this.get(key, JSONObject.class);
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
        try {
            cache.invalidate(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
```

##### 注意

`caffeine`只能在项目启动前完成配置,所以在项目运行中是没有办法指定缓存`Caffeine`的配置



---

### [springboot 项目 第八次 搭建](https://github.com/bujian-self/springboot-demo/blob/main/springboot-cache/HELP.md)
