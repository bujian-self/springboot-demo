package com.bujian.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bujian.server.bean.FuzideshilianDo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bujian
 * @Repository注解 将文件提交给 spring 管理
 * @date 2021/6/16 11:15
 */
@Repository
public interface FuzideshilianMapper extends BaseMapper<FuzideshilianDo> {

    /**
     * 根据实体类参数查询
     *
     * @param bean
     * @return java.util.List<com.bujian.demo.bean.FuzideshilianDo>
     */
    List<FuzideshilianDo> selectByBean(FuzideshilianDo bean);
}
