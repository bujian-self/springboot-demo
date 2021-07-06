package com.bujian.knife4j.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
@ApiModel(value="Fuzideshilian对象")
public class FuzideshilianDo implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;
    @ApiModelProperty(value = "问题")
    private String question;
    @ApiModelProperty(value = "答案")
    private String answer;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "最后操作时间")
    private LocalDateTime lastOperate;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "查询次数")
    private Integer queryTimes;
}
