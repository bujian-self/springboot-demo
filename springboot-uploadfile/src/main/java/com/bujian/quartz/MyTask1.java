package com.bujian.quartz;

import com.bujian.server.bean.FuzideshilianDo;
import com.bujian.server.service.FuzideshilianService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 一个演示示例
 * @author bujian
 */
public class MyTask1 extends QuartzJobBean {

    //验证是否成功可以注入service   之前在ssm当中需要额外进行配置
    @Autowired
    private FuzideshilianService service;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.err.println(service.selectByBean(FuzideshilianDo.builder().id(1).build()));
        //TODO 这里写定时任务的执行逻辑
        System.out.println("动态的定时任务执行时间：" + LocalDateTime.now());
    }

    public static void tss() {
        System.err.println("tss");
    }


    public static void main(String[] args) {
        Method[] declaredMethods = MyTask1.class.getDeclaredMethods();
        for (Method method : declaredMethods) {
            System.out.println("方法名称为：" + method.getName());
            System.out.println("方法是否带有可变数量的参数：" + method.isVarArgs());
            System.out.println("方法的参数类型依次为：");
            // 获取所有参数类型
            for (Class aClass : method.getParameterTypes()) {
                System.out.println("\t" + aClass);
            }
            // 获取返回值类型
            System.out.println("方法的返回值类型为：" + method.getReturnType());
            System.out.println("方法可能抛出的异常类型有：");
            // 获取所有可能抛出的异常
            for (Class<?> exceptionType : method.getExceptionTypes()) {
                System.out.println(" " + exceptionType);
            }
        }
    }
}
