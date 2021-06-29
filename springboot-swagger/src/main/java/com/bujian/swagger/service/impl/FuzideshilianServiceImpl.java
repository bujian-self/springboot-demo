package com.bujian.swagger.service.impl;

import com.bujian.swagger.bean.FuzideshilianDo;
import com.bujian.swagger.mapper.FuzideshilianMapper;
import com.bujian.swagger.service.FuzideshilianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Service层 代码的实现
 * @author bujian
 * @date 2021/6/16 11:15
 */
@Service
public class FuzideshilianServiceImpl implements FuzideshilianService {

    @Autowired
    private FuzideshilianMapper fuzideshilianMapper;

    @Override
    public FuzideshilianDo selectById(long id) {
        Assert.isTrue(id > 0,"id 必须大于 0");
        return fuzideshilianMapper.selectById(id);
    }

    @Override
    public List<FuzideshilianDo> selectByBean(FuzideshilianDo bean) {
        return fuzideshilianMapper.selectByBean(bean);
    }
}
