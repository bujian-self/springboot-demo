package com.bujian.demo.service;

import com.bujian.demo.bean.FuzideshilianDo;
import com.bujian.demo.mapper.FuzideshilianMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Service层 代码
 * @author lijie
 * @date 2021/6/16 11:15
 */
public interface FuzideshilianService {

    /**
     * 根据实体类查询方法
     * @param bean
     * @return java.util.List<com.bujian.demo.bean.FuzideshilianDo>
     */
    public List<FuzideshilianDo> selectByBean(FuzideshilianDo bean);

    /**
     * 根据id查询数据
     * @param id
     * @return com.bujian.demo.bean.FuzideshilianDo
     */
    public FuzideshilianDo selectById(long id);

}
