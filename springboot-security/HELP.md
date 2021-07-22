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
```
5. 添加`SecurityConfig`配置文件
```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)//开启权限注解,默认是关闭的
public class SecurityConfig extends WebSecurityConfigurerAdapter {
   /**
    * token认证过滤器
    */
   @Autowired
   private AuthTokenFilter authTokenFilter;
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

   @Override
   protected void configure(HttpSecurity http) throws Exception {
      // CSRF禁用，因为不使用session
      http.csrf().disable();
      // 禁用X-Frame-Options 防止iframe嵌入 ??
      http.headers().frameOptions().disable();
      // 基于token，所以不需要session
      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
      // 过滤请求 允许匿名访问的路径
      http.authorizeRequests().antMatchers(anonUrl+"/**").anonymous()
              .antMatchers(HttpMethod.GET, "/*.html", "/**/*.html", "/**/*.css", "/**/*.js")
              // 除上面外的所有请求全部需要鉴权认证
              .permitAll().anyRequest().authenticated();
      // 认证失败处理类，前后端分离前端不会302重定向，直接返回403异常
      http.exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());
      // 关闭退出操作，自己从controller里面写退出逻辑
      http.logout().clearAuthentication(true).invalidateHttpSession(true).disable();
      // 添加JWT filter
      http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
      // 添加CORS filter
      http.addFilterBefore(corsFilter, AuthTokenFilter.class);
      http.addFilterBefore(corsFilter, LogoutFilter.class);
   }
}
```
6. 添加`AuthTokenFilter.java`token认证过滤器
```java
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Value("${token.header}")
    private String header;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader(this.header);
        if (StringUtils.isNotBlank(token)) {
            // 这里先写死, 后续再优化
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(token, "$2a$10$LWAq6mF/6g1sVY4pQ413oukQfEL6uJBZ6h8re7SnsMSOUmQN7YVPK", Arrays.asList(new SimpleGrantedAuthority("admin")));
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
   @GetMapping("/anon/login")
   public Object login(){
      //TODO login
      return "login";
   }
   @RequestMapping("/logout")
   public Object logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @RequestBody String data){
      System.err.println("logout");
      //TODO logout
      return "logout";
   }
}
```
其他. 修改部分文档结构


---
### [springboot 项目 第八次 搭建](https://github.com/bujian-self/springboot-demo/blob/main/springboot-cache/HELP.md)
