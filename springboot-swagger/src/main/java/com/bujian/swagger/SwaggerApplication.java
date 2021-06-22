package com.bujian.swagger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @SpringBootApplication 开启了 Spring 的组件扫描和 springboot 的自动配置功能，相当于将以下三个注解组合在了一起
     1、@Configuration：表名该类使用基于Java的配置,将此类作为配置类。
     2、@ComponentScan：启用注解扫描。
     3、@EnableAutoConfiguration：开启springboot的自动配置功能。
    也可以再次使用这些注解进行自定义
 * @MapperScan 用于 mybatis-plus 对 dao层代码的扫描
    与 dao层 的 @Mapper 可以只保留一个
 * @author SpringBoot
 * @date 2021/6/15 14:41
 */
@SpringBootApplication
@MapperScan("com.bujian.swagger.mapper")
//@EnableSwagger2
public class SwaggerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwaggerApplication.class, args);
    }

}
