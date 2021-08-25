package com.bujian.quartz.controller;

import com.bujian.quartz.bean.QuartzBean;
import com.bujian.quartz.utils.QuartzUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务模块
 * @author bujian
 */
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
