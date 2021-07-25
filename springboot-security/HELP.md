# springboot 项目 第九次 搭建
* 添加`security`验证
1. 删除原来自定义的请求头
```yaml
server:
   # 默认后端访问路径
   interface:
      # 请求头
      header: Authorization # 这个不用了
```
2. 删除原来自定义的监听器`AuthorizeInterceptor.java`文件(使用`security`替代)


3. 在`maven`的`pom.xml`引入jar包
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>5.5.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>5.5.1</version>
</dependency>
```
4. 在`application.yml`配置文件中添加`token`配置
```yaml
# token配置
token:
  # 令牌自定义标识
  header: Authorization
  # 令牌密钥
  secret: abcdefghijklmnopqrstuvwxyz
  # 令牌有效期（默认10分钟）
  expireTime: 2m
```
5. 添加`SecurityConfig`配置文件
```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)//开启权限注解,默认是关闭的
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 自定义用户认证逻辑
     */
    @Autowired
    private UserDetailsService userDetailsService;
    /**
     * token认证过滤器
     */
    @Autowired
    private JwtAuthTokenFilter jwtAuthTokenFilter;
    /**
     * 跨域过滤器
     */
    @Autowired
    private CorsFilter corsFilter;
    /**
     * 允许的匿名路径
     */
    @Value("${server.interface.anonymous:/anon}")
    private String anonUrl;
    /**
     * 强散列哈希加密实现
     */
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 解决 无法直接注入 AuthenticationManager
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    /**
     * 身份认证接口
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CSRF禁用，因为不使用session
        http.csrf().disable();
        // 禁用X-Frame-Options 防止iframe嵌入 ??
        http.headers().frameOptions().disable();
        // 基于token，所以不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 过滤请求 允许匿名访问的路径
        http.authorizeRequests().antMatchers(anonUrl+"/**", "/error").anonymous()
                .antMatchers(HttpMethod.GET, "/*.html", "/**/*.html", "/**/*.css", "/**/*.js")
                // 除上面外的所有请求全部需要鉴权认证
                .permitAll().anyRequest().authenticated();
        // 认证失败处理类，前后端分离前端不会302重定向，返回403异常没有错误信息，所以直接上抛异常
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> { throw authException; });
        // 关闭退出操作，自己从controller里面写退出逻辑
        http.logout().clearAuthentication(true).invalidateHttpSession(true).disable();
        // 添加JWT filter
        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加CORS filter
        http.addFilterBefore(corsFilter, JwtAuthTokenFilter.class);
        http.addFilterBefore(corsFilter, LogoutFilter.class);
    }
}
```
6. 修改`AuthTokenFilter.java`为`JwtAuthTokenFilter.java`token认证过滤器
```java
@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 解密 token 获取 登录用户信息
        LoginUser loginUser = jwtTokenUtil.claims2JavaObject(jwtTokenUtil.decryptToken(request), LoginUser.class);
        if (loginUser != null) {
            // 将 用户 交给 Security 管理
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

}
```
6. 添加`CorsConfig.java` 跨域过滤器配置文件
```java
@Configuration
public class CorsConfig {
   @Bean
   public CorsFilter corsFilter() {
      final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
      final CorsConfiguration corsConfiguration = new CorsConfiguration();
      corsConfiguration.setAllowCredentials(true);
      // 设置访问源地址
      corsConfiguration.addAllowedOrigin("*");
      // 设置访问源请求头
      corsConfiguration.addAllowedHeader("*");
      // 设置访问源请求方法
      corsConfiguration.addAllowedMethod("*");
      // 对接口配置跨域设置
      urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
      return new CorsFilter(urlBasedCorsConfigurationSource);
   }
}
```
7. 添加`LoginController.java` 验证登录
```java
@RestController
public class LoginController {

    @Autowired
    private LoginUserService loginService;

    @ApiImplicitParam(name = "json", value = "用户登录信息", required = true, example = "{'username':'user','password':'123456'}")
    @ApiOperation(value="用户登录")
    @PostMapping("${swagger.pathMapping:/anon}"+"/login")
    public Object login(@RequestBody JSONObject json) {
        log.info("用户登录: {}", json);
        // TODO login user Front check
        String username = json.getString("username");
        String password = json.getString("password");
        return loginService.login(username, password);
    }

    @ApiImplicitParam(name = "json", value = "退出登录时包含的信息", required = true)
    @ApiOperation(value="退出登录")
    @RequestMapping("/logout")
    public Object logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @RequestBody JSONObject json){
        System.err.println("logout");
        //TODO logout
        return "logout";
    }

    @ApiOperation(value="刷新token")
    @GetMapping("/refreshToken")
    public Object refreshToken(HttpServletRequest request){
        System.err.println("refreshToken");
        //TODO refreshToken
        return loginService.refreshToken(request);
    }

    @ApiOperation(value="hello")
    @GetMapping("/hello")
    public Object hello(Authentication authentication){
        return authentication.getPrincipal();
    }

}
```
8. 添加`JwtTokenUtil.java` jwt工具
```java
@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = 1L;

    @Value("${token.header:Authorization}")
    private String header;
    @Value("${token.secret:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ}")
    private String secret;
    @Value("${token.expireTime:10m}")
    private Duration expireTime;
    private SignatureAlgorithm hs256 = SignatureAlgorithm.HS256;

    private Key getKey() {
        if (secret.length() < 44) {
            // hs256 要求数据格式长度 最少 44字符
            secret = String.join("", Collections.nCopies(44/ secret.length()+1, secret));
        }
        return new SecretKeySpec(DatatypeConverter.parseBase64Binary(secret), hs256.getJcaName());
    }

    /**
     * 快速创建 jwt 默认时间
     */
    public String creatJwt(Map<String, Object> claims) {
        Date end = null;
        if (expireTime != null && expireTime.getSeconds() > 0) {
            end = new Date(System.currentTimeMillis() + expireTime.toMillis());
        }
        return this.creatJwt(claims,end);
    }

    /**
     * 快速创建 指定过期时间
     */
    public String creatJwt(Map<String, Object> claims, Date expiration) {
        return this.creatJwt(claims, null, null, null, null, expiration);
    }

    /**
     * 创建 生成 完整jwt
     */
    public String creatJwt(Map<String, Object> claims, String id, String subject, String issuer, String audience, Date expiration) {
        return Jwts.builder().setId(id).setSubject(subject).setIssuer(issuer).setIssuedAt(new Date(System.currentTimeMillis()))
                .setAudience(audience).setExpiration(expiration).addClaims(claims).signWith(hs256, getKey()).compact();
    }

    /**
     * token是否过期
     */
    public boolean isTimout(String token) {
        try {
            Claims claims = decryptJwt(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 解密token
     */
    public Claims decryptToken(HttpServletRequest request) {
        String toekn = request.getHeader(this.header);
        if (StringUtils.isBlank(toekn)) {
            return null;
        }
        return this.decryptJwt(toekn);
    }
    /**
     * 解密 jwt
     */
    public Claims decryptJwt(String token) {
        try {
            return Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultClaims();
    }

    /**
     * Claims 转 对象
     */
    public <T> T claims2JavaObject(Claims claims, Class<T> clazz){
        Assert.isTrue(claims!=null , "未获取到 claims");
        Assert.isTrue(clazz!=null , "Class 不能为空");
        return JSONObject.parseObject(JSONObject.toJSONString(claims), clazz);
    }

    /**
     * 刷新 jwt 时间
     */
    public String refreshToken(HttpServletRequest request) {
        String toekn = request.getHeader(this.header);
        if (StringUtils.isBlank(toekn)) {
            return null;
        }
        return this.refreshJwt(toekn);
    }

    /**
     * 刷新 jwt 时间
     */
    public String refreshJwt(String token) {
        Claims claims = decryptJwt(token);
        Date end = null;
        if (expireTime != null && expireTime.getSeconds() > 0) {
            end = new Date(System.currentTimeMillis() + expireTime.toMillis());
        }
        return Jwts.builder().setClaims(claims).setExpiration(end).signWith(hs256, getKey()).compact();
    }
}
```

其他. 修改部分文档结构

---
### [springboot 项目 第八次 搭建](https://github.com/bujian-self/springboot-demo/blob/main/springboot-cache/HELP.md)
