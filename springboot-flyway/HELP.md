# springboot 项目 第十二次 搭建

* 集成 `flyway`

1. `pom.xml` 添加 `flywaydb`

```xml

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>7.11.4</version>
</dependency>
```

2. yaml配置文件 添加`flyway`配置

```yaml
spring:
  flyway:
    # 如果启动的时候需要flyway管理sql脚本的话，将enabled设置为true
    enabled: true
    # 如果数据库不是空表，需要设置成 true，否则启动报错
    baseline-on-migrate: true
    # 验证错误时 是否自动清除数据库 高危操作!
    clean-on-validation-error: false
    # 初始化 sql 脚本的路劲
    locations: classpath:db/migration
```

3. 创建sql脚本文件和相关路径

```path
resources
    db
        migration
            V1.x__XXX.sql
```

其他

+ 修改部分项目结构

---

### [springboot 项目 第十一次 搭建](https://github.com/bujian-self/springboot-demo/blob/main/springboot-exception/HELP.md)
