# springboot 项目 第十次 搭建

* 使用集成的`Sa-Token`做验证

1. yaml配置文件 添加`Sa-Token`配置(其他修改请对比文件查看)

```yaml
sa-token:
  # token名称 (同时也是cookie名称)
  token-name: satoken
  # token有效期，单位s 默认30天, -1代表永不过期
  timeout: 2592000
  # token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
  activity-timeout: -1
  # 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
  is-concurrent: true
  # 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
  is-share: false
  # token风格
  token-style: uuid
  # 是否输出操作日志
  is-log: false
```

2. 在`maven`的`pom.xml`引入jar包(其他修改请对比文件查看)

```xml

<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>1.24.0</version>
</dependency>
```

3. 修改`WebMvcConfig`配置文件

```java

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 设置请求的统一前缀
     */
    @Value("${swagger.pathMapping:/anon}")
    private String pathMapping;

    @Autowired
    private RequestInterceptor reqInter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(reqInter).addPathPatterns("/**");
        // 注册注解拦截器
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**").excludePathPatterns("");

        // 设置sa路由
        SaRouteInterceptor saRouteInterceptor = new SaRouteInterceptor((request, response, handler) -> {
            if (!request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
                // 检验当前会话是否已经登录, 如果未登录，则抛出异常：`NotLoginException`
                StpUtil.checkLogin();
            }
        });
        // 注册Sa-Token的路由拦截器，并排除登录接口或其他可匿名访问的接口地址 (与注解拦截器无关)
        registry.addInterceptor(saRouteInterceptor).addPathPatterns("/**").excludePathPatterns(this.pathMapping + "/**", "/error");
    }
}
```

4. 添加`StpInterfaceImpl.java`自定义权限验证接口扩展

```java

@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        System.err.println("getPermissionList");
        return Arrays.asList("role", "edit");
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        System.err.println("getRoleList");
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        return Arrays.asList("admin", "super-admin");
    }

}
```

5. 添加`SaTokenDaoRedisJackson.java`Sa-Token持久层接口(不需要redis,可以不用)

```java

@Component
public class SaTokenDaoRedisJackson implements SaTokenDao {

    /**
     * ObjectMapper对象 (以public作用域暴露出此对象，方便开发者二次更改配置)
     */
    public ObjectMapper objectMapper;

    /**
     * String专用
     */
    public StringRedisTemplate stringRedisTemplate;

    /**
     * Object专用
     */
    public RedisTemplate<String, Object> objectRedisTemplate;

    /**
     * 标记：是否已初始化成功
     */
    public boolean isInit;

    @Value("${spring.cache.redis.key-prefix:}")
    private String keyPrefix;

    /**
     * 自己修改了key的序列化,符合自己项目需求
     */
    @Autowired
    public void init(RedisConnectionFactory connectionFactory) {
        RedisSerializer keySerializer = new MyStringSerializer(keyPrefix);
        RedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

        try {
            Field field = GenericJackson2JsonRedisSerializer.class.getDeclaredField("mapper");
            field.setAccessible(true);
            ObjectMapper objectMapper = (ObjectMapper) field.get(valueSerializer);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            this.objectMapper = objectMapper;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        StringRedisTemplate stringTemplate = new StringRedisTemplate();
        stringTemplate.setConnectionFactory(connectionFactory);
        stringTemplate.setKeySerializer(keySerializer);
        stringTemplate.setHashKeySerializer(keySerializer);
        stringTemplate.afterPropertiesSet();
        RedisTemplate<String, Object> template = new RedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        if (!this.isInit) {
            this.stringRedisTemplate = stringTemplate;
            this.objectRedisTemplate = template;
            this.isInit = true;
        }

    }


    /**
     * 获取Value，如无返空
     */
    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 写入Value，并设定存活时间 (单位: 秒)
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        // 判断是否为永不过期
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            stringRedisTemplate.opsForValue().set(key, value);
        } else {
            stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 修修改指定key-value键值对 (过期时间不变)
     */
    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    /**
     * 删除Value
     */
    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getTimeout(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    /**
     * 修改Value的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.set(key, this.get(key), timeout);
            }
            return;
        }
        stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }


    /**
     * 获取Object，如无返空
     */
    @Override
    public Object getObject(String key) {
        return objectRedisTemplate.opsForValue().get(key);
    }

    /**
     * 写入Object，并设定存活时间 (单位: 秒)
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        // 判断是否为永不过期
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            objectRedisTemplate.opsForValue().set(key, object);
        } else {
            objectRedisTemplate.opsForValue().set(key, object, timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 更新Object (过期时间不变)
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 = 无此键
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * 删除Object
     */
    @Override
    public void deleteObject(String key) {
        objectRedisTemplate.delete(key);
    }

    /**
     * 获取Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public long getObjectTimeout(String key) {
        return objectRedisTemplate.getExpire(key);
    }

    /**
     * 修改Object的剩余存活时间 (单位: 秒)
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        // 判断是否想要设置为永久
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getObjectTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // 如果其已经被设置为永久，则不作任何处理
            } else {
                // 如果尚未被设置为永久，那么再次set一次
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        objectRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }


    /**
     * 搜索数据
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size) {
        Set<String> keys = stringRedisTemplate.keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<String>(keys);
        return SaFoxUtil.searchList(list, start, size);
    }

}
```

其他

+ 在第八次(cache)基础上修改
+ 修改部分文档结构

---

### [springboot 项目 第九次 搭建](https://github.com/bujian-self/springboot-demo/blob/main/springboot-security/HELP.md)
