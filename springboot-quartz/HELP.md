# springboot 项目 第十三次 搭建

[Quartz官方文档](https://www.w3cschool.cn/quartz_doc)

[Quartz参考示例](https://cloud.tencent.com/developer/article/1640190)

* 集成 `Quartz`

1. `pom.xml` 添加 `quartz`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

2. yaml配置文件 添加`quartz`配置
```yaml
spring:
  quartz:
    # 将任务等保存化到数据库
    job-store-type: jdbc
    #    jdbc:
    #      initialize-schema: always
    # 是否等待任务执行完毕后，容器才会关闭
    wait-for-jobs-to-complete-on-shutdown: true
    # 配置的job是否覆盖已经存在的JOB信息
    overwrite-existing-jobs: false
    # 这里居然是个map，搞得智能提示都没有，佛了
    properties:
      org:
        quartz:
          # scheduler相关
          scheduler:
            # 集群名，区分同一系统的不同实例，若使用集群功能，则每一个实例都要使用相同的名字
            instanceName: scheduler
            # 实例id 生成策略
            instanceId: AUTO
          # 线程池相关
          threadPool:
            class: org.quartz.simpl.SimpleThreadPool
            # 线程数
            threadCount: 10
            # 线程优先级
            threadPriority: 5
            threadsInheritContextClassLoaderOfInitializingThread: true
          # 持久化相关
          jobStore:
            class: org.quartz.impl.jdbcjobstore.JobStoreTX
            # 类似于Hibernate的dialect，用于处理DB之间的差异，StdJDBCDelegate能满足大部分的DB（授权）
            driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
            # 表示数据库中相关表是QRTZ_开头的
            tablePrefix: QRTZ_
            useProperties: false
            # 如果有多个Quartz实例在用同一套数据库时，必须设置为true
            isClustered: true
            # 检入到数据库中的频率
            clusterCheckinInterval: 15000
            # 这是JobStore能处理的错过触发的Trigger的最大数量。处理太多（2打）很快就会导致数据库表被锁定够长的时间，
            #            maxMisfiresToHandleAtATime: 1
            # 设置这个参数为true会告诉Quartz从数据源获取连接后不要调用它的setAutoCommit(false)方法。
#            dontSetAutoCommitFalse: false
```

3. 创建`QuartzBean.java`
```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.CronExpression;

import java.text.ParseException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuartzBean {
    /** 任务id */
    private String  id;

    /** 任务名称 */
    private String jobName;

    /** 任务执行类 */
    private String jobClass;

    /** 任务状态 启动还是暂停*/
    private Integer status;

    /** 任务运行时间表达式 */
    private String cronExpression;

    public void setCronExpression(String cronExpression) throws ParseException {
        new CronExpression(cronExpression);//不正确的cron表达式会报错
        this.cronExpression = cronExpression;
    }
}
```
   
4. 创建`QuartzUtil.java`
```java
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

public class QuartzUtil {

    /**
     * 创建定时任务 定时任务创建之后默认启动状态
     * @param scheduler   调度器
     * @param quartzBean  定时任务信息类
     * @throws Exception
     */
    public static void createScheduleJob(Scheduler scheduler, QuartzBean quartzBean){
        try {
            //获取到定时任务的执行类  必须是类的绝对路径名称
            //定时任务类需要是job类的具体实现 QuartzJobBean是job的抽象类。
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(quartzBean.getJobClass());
            // 构建定时任务信息
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(quartzBean.getJobName()).build();
            // 设置定时任务执行方式
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzBean.getCronExpression());
            // 构建触发器trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(quartzBean.getJobName()).withSchedule(scheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (ClassNotFoundException e) {
            System.out.println("定时任务类路径出错：请输入类的绝对路径");
        } catch (SchedulerException e) {
            System.out.println("创建定时任务出错："+e.getMessage());
        }
    }

    /**
     * 根据任务名称暂停定时任务
     * @param scheduler  调度器
     * @param jobName    定时任务名称
     * @throws SchedulerException
     */
    public static void pauseScheduleJob(Scheduler scheduler, String jobName){
        JobKey jobKey = JobKey.jobKey(jobName);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            System.out.println("暂停定时任务出错："+e.getMessage());
        }
    }

    /**
     * 根据任务名称恢复定时任务
     * @param scheduler  调度器
     * @param jobName    定时任务名称
     * @throws SchedulerException
     */
    public static void resumeScheduleJob(Scheduler scheduler, String jobName) {
        JobKey jobKey = JobKey.jobKey(jobName);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            System.out.println("启动定时任务出错："+e.getMessage());
        }
    }

    /**
     * 根据任务名称立即运行一次定时任务
     * @param scheduler     调度器
     * @param jobName       定时任务名称
     * @throws SchedulerException
     */
    public static void runOnce(Scheduler scheduler, String jobName){
        JobKey jobKey = JobKey.jobKey(jobName);
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            System.out.println("运行定时任务出错："+e.getMessage());
        }
    }

    /**
     * 更新定时任务
     * @param scheduler   调度器
     * @param quartzBean  定时任务信息类
     * @throws SchedulerException
     */
    public static void updateScheduleJob(Scheduler scheduler, QuartzBean quartzBean)  {
        try {
            //获取到对应任务的触发器
            TriggerKey triggerKey = TriggerKey.triggerKey(quartzBean.getJobName());
            //设置定时任务执行方式
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzBean.getCronExpression());
            //重新构建任务的触发器trigger
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            //重置对应的job
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            System.out.println("更新定时任务出错："+e.getMessage());
        }
    }

    /**
     * 根据定时任务名称从调度器当中删除定时任务
     * @param scheduler 调度器
     * @param jobName   定时任务名称
     * @throws SchedulerException
     */
    public static void deleteScheduleJob(Scheduler scheduler, String jobName) {
        JobKey jobKey = JobKey.jobKey(jobName);
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            System.out.println("删除定时任务出错："+e.getMessage());
        }
    }
}
```

5. 创建`MyTask1.java`
```java
import com.bujian.quartz.server.bean.FuzideshilianDo;
import com.bujian.quartz.server.service.FuzideshilianService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;

public class MyTask1 extends QuartzJobBean {

    //验证是否成功可以注入service   之前在ssm当中需要额外进行配置
    @Autowired
    private FuzideshilianService service;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.err.println(service.selectByBean(FuzideshilianDo.builder().id(1).build()));
        //TODO 这里写定时任务的执行逻辑
        System.out.println("动态的定时任务执行时间："+ LocalDateTime.now());
    }
}
```

6. 创建`QuartzController.java`
```java
import com.bujian.quartz.quartz.po.QuartzBean;
import com.bujian.quartz.quartz.utils.QuartzUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "定时任务模块")
@RestController
@RequestMapping("/anon")
public class QuartzController {
    //注入任务调度
    @Autowired
    private Scheduler scheduler;

    @ApiImplicitParam(name = "quartzBean", value = "定时任务信息", required = true)
    @ApiOperation(value = "定时任务创建")
    @PostMapping("/createJob")
    public Object  createJob(QuartzBean quartzBean)  {
        try {
            //进行测试所以写死
            quartzBean.setJobClass("com.bujian.quartz.quartz.MyTask1");
            quartzBean.setJobName("test1");
            quartzBean.setCronExpression("*/10 * * * * ?");
            QuartzUtil.createScheduleJob(scheduler,quartzBean);
        } catch (Exception e) {
            return "创建失败";
        }
        return "创建成功";
    }

    @ApiOperation(value = "暂停任务")
    @GetMapping("/pauseJob")
    public String pauseJob()  {
        try {
            QuartzUtil.pauseScheduleJob (scheduler,"test1");
        } catch (Exception e) {
            return "暂停失败";
        }
        return "暂停成功";
    }

    @ApiOperation(value = "任务运行")
    @GetMapping("/runOnce")
    public String  runOnce()  {
        try {
            QuartzUtil.runOnce (scheduler,"test1");
        } catch (Exception e) {
            return "运行一次失败";
        }
        return "运行一次成功";
    }

    @ApiOperation(value = "启动任务")
    @GetMapping("/resume")
    public String  resume()  {
        try {
            QuartzUtil.resumeScheduleJob(scheduler,"test1");
        } catch (Exception e) {
            return "启动失败";
        }
        return "启动成功";
    }

    @ApiOperation(value = "定时任务更新")
    @PostMapping("/update")
    public String  update(QuartzBean quartzBean)  {
        try {
            //进行测试所以写死
            quartzBean.setJobClass("com.bujian.quartz.quartz.MyTask1");
            quartzBean.setJobName("test1");
            quartzBean.setCronExpression("10 * * * * ?");
            QuartzUtil.updateScheduleJob(scheduler,quartzBean);
        } catch (Exception e) {
            return "启动失败";
        }
        return "启动成功";
    }
}
```

---

### [springboot 项目 第十二次 搭建](https://github.com/bujian-self/springboot-demo/blob/main/springboot-flyway/HELP.md)
