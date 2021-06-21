package com.bujian.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @SpringBootApplication 开启了 Spring 的组件扫描和 springboot 的自动配置功能，相当于将以下三个注解组合在了一起
 * 1、@Configuration：表名该类使用基于Java的配置,将此类作为配置类。
 * 2、@ComponentScan：启用注解扫描。
 * 3、@EnableAutoConfiguration：开启springboot的自动配置功能。
 * 也可以再次使用这些注解进行自定义
 * @author SpringBoot
 * @date 2021/6/15 14:41
 */
@SpringBootApplication
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

}
