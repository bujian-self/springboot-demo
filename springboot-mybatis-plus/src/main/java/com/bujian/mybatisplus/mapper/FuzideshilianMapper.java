package com.bujian.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bujian.mybatisplus.bean.FuzideshilianDo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Mapper 注解 将 DAO 层的 类交给 spring 管理
    与 启动类的 的 @MapperScan 可以只保留一个
 * @author lijie
 * @date 2021/6/16 11:15
 */
@Mapper
public interface FuzideshilianMapper extends BaseMapper<FuzideshilianDo> {
}
