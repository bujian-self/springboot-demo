# springboot 项目 第四次 搭建
 * 使用Swagger生成API文档  
 [引入Swagger包](https://mvnrepository.com/artifact/io.springfox/springfox-swagger2)
 ```xml
<dependency>
   <groupId>io.springfox</groupId>
   <artifactId>springfox-swagger2</artifactId>
   <version>2.9.2</version>
</dependency>
 ```
 [引入Swagger-ui包](https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui)
 ```xml
<dependency>
   <groupId>io.springfox</groupId>
   <artifactId>springfox-swagger-ui</artifactId>
   <version>2.9.2</version>
</dependency>
 ```
 * 在方法上添加 swagger 注解  
 [Swagger注解相关](https://swagger.io)  
 [详解参考](https://www.liangzl.com/get-article-detail-820.html)
 
 * 启动前配置  
  在 启动类`xxApplication.java`文件上添加注解`@EnableSwagger2`
 
 * 去除`basic-error-controller`错误  
  添加`SwaggerConfig.java`配置文件,去除启动项上的`@EnableSwagger2`注解
 ```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket demoApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("(?!/error.*).*"))
                .build();
    }
}
 ```
---
### [springboot 项目 第三次 搭建](https://github.com/lijiepersion/springboot-demo/blob/main/springboot-test/HELP.md)
