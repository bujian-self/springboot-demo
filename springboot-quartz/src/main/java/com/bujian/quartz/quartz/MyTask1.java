package com.bujian.quartz.quartz;

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
