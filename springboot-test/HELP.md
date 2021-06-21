# springboot 项目 第三次 搭建
 * 多填写几条sql
 * 去除 dao层 引入错误  
    `mapper.java` 文件注解添加 `@Repository` ,提升给 spring 管理
### 使用单元测试
 * 引入 junit jar 包
 [可以去 maven 官网 查找引用](https://mvnrepository.com/artifact/junit/junit)
 
 向 maven 的 pom.xml 中添加 下述代码
 ```xml
 <dependency>
     <groupId>junit</groupId>
     <artifactId>junit</artifactId>
     <version>4.13</version>
     <scope>test</scope>
 </dependency>
 ```
文中添加了部分其他的代码

---
### [springboot 项目 第二次 搭建](file://E:/Projects/Idea/springBootProject/springboot-mybatis-plus/HELP.md)
