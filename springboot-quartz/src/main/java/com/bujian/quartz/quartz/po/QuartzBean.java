package com.bujian.quartz.quartz.po;

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
        new CronExpression(cronExpression);
        this.cronExpression = cronExpression;
    }
}
