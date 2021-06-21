# springboot 项目 第二次 搭建

### 集成 mybatis-plus 
 * 引入 mysql jar 包
 
[可以去 maven 官网 查找引用](https://mvnrepository.com/artifact/mysql/mysql-connector-java)

向 maven 的 pom.xml 中添加 下述代码
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.26</version>
</dependency>
```
 * 在配置文件中写入 mysql数据库 配置
 ``` yaml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true
    username: root
    password: 123456
 ```
 * 引入 mybatis-plus jar 包
 
[可以去 maven 官网 查找引用](https://mvnrepository.com/artifact/com.baomidou/mybatis-plus-boot-starter)
向 maven 的 pom.xml中添加 下述代码
 ```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.1.0</version>
</dependency>
```
 * 在配置文件中写入 mybatis-plus 配置
 
 [mybatis-plus 官网](https://baomidou.com/config/)
``` yaml
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*Mapper.xml
  configuration:
    map-underscore-to-camel-case: true
    auto-mapping-behavior: full
  global-config:
    db-config:
      logic-not-delete-value: 1
      logic-delete-value: 0
```
 * 编写三层代码架构   
 controller ==>> controller  
 service ==>> service  
 &nbsp;&nbsp;serviceImpl ==>> serviceImpl  
 DAO ==>> mapper
 
 * 启动前添加 配置
 
 1. 在 `xxApplication.java` 的类上添加注解 `@MapperScan("com.bujian.mybatisplus.mapper")`
    路径根据实际情况来填写,注意引入包名
 2. 在DAO层接口文件上添加注解 `@Mapper`
 3. 在do实体类上添加注解 `@TableName(value = "xxx表名称")` 根据实际情况来  
    在主键上添加注解 `@TableId(type = IdType.AUTO)`  
    在其他字段上添加注解 `@TableName(value = "xxx")` 根据实际情况来  
    在数据库里没有隐射的字段上添加注解 `@TableField(exist = false)`  
 [进一步参考](https://blog.csdn.net/m0_37922192/article/details/111413550)  
 [官网](https://mp.baomidou.com/guide/annotation.html)
     
---
### [springboot 项目 第一次 搭建](file://E:/Projects/Idea/springBootProject/springboot_start/HELP.md)
