package com.bujian.mybatisplus.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 表数据信息
 * @author bujian
 * @date 2021/6/16 11:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "fuzideshilian")
public class FuzideshilianDo {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String question;
    private String answer;
    private LocalDateTime createTime;
    private LocalDateTime lastOperate;
    private String status;
    private Integer queryTimes;
}
