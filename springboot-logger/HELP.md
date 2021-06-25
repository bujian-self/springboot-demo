# springboot 项目 第五次 搭建
* 使用`springboot`自带的`logback`来记录日志
 
 [详情看logback官网](http://logback.qos.ch/manual/introduction.html)
 
* 简单配置版   
 在配置文件`application.yml`中 添加以下配置代码
```yaml
logging:
  level:
    root: warn # 根目录 级别
    org:
      springframework:
        web: error
        security: debug
      hibernate: error
    com:
      bujian: info # 自己项目级别
  pattern:
    console: '%d{yyyy-MM-dd HH:mm:ss.SSS} %+5level ${PID} --- [%+22thread] %-41logger{15}: %msg%n' # 日志打印格式
    file: '%d{yyyy-MM-dd HH:mm:ss.SSS} %+5level ${PID} --- [%+22thread] %-41logger{15}: %msg%n' # 日志打印格式
  file:
    path: ${spring.application.name} # 日志文件输出路径和名称
# 以下描述不一定准确
  logback:
    rollingpolicy:
      max-file-size: 50MB # 最大日志大小
      max-history: 7 # 日志保留时长
      file-name-pattern: logs/%d{yyyyMMdd}-%i.log # 日志压缩
      total-size-cap: 500MB # 总日志大小
```
* 原生配置版   
 在配置文件`application.yml`中 添加以下配置代码
```yaml
logging:
  config: classpath:logback-spring.xml
```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
在`src\main\resources`路径下添加文件`logback-spring.xml`文件  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[文件内容参考](https://www.cnblogs.com/zhangjianbing/p/8992897.html)
* 注  
 当配置数据和xml文件同时存在时,优先使用xml文件里面的配置  
 xml文件名称不是默认名称时,需要在pom.xml中移除原来的日志文件信息,再次添加自定义文件信息,并且在`application.yml`重新指定文件路径,才会生效


---
### [springboot 项目 第四次 搭建](https://github.com/lijiepersion/springboot-demo/blob/main/springboot-swagger/HELP.md)
