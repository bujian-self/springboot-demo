package com.bujian.satoken.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bujian.satoken.bean.FuzideshilianDo;

import java.util.List;

/**
 * Service层 代码
 * @author bujian
 * @date 2021/6/16 11:15
 */
public interface FuzideshilianService extends IService<FuzideshilianDo> {

    /**
     * 根据实体类查询方法
     * @param bean
     * @return java.util.List<com.bujian.demo.bean.FuzideshilianDo>
     */
    public List<FuzideshilianDo> selectByBean(FuzideshilianDo bean);

}
