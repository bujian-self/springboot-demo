package com.bujian.logger.service.impl;

import com.bujian.logger.bean.FuzideshilianDo;
import com.bujian.logger.mapper.FuzideshilianMapper;
import com.bujian.logger.service.FuzideshilianService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FuzideshilianServiceImpl implements FuzideshilianService {

    @Autowired
    private FuzideshilianMapper fuzideshilianMapper;

    @Override
    public FuzideshilianDo selectById(long id) {
        log.error("FuzideshilianDo根据id查询信息: {}", id);
        log.warn("FuzideshilianDo根据id查询信息: {}", id);
        log.info("FuzideshilianDo根据id查询信息: {}", id);
        log.debug("FuzideshilianDo根据id查询信息: {}", id);
        log.trace("FuzideshilianDo根据id查询信息: {}", id);
        Assert.isTrue(id > 0,"id 必须大于 0");
        return fuzideshilianMapper.selectById(id);
    }

    @Override
    public List<FuzideshilianDo> selectByBean(FuzideshilianDo bean) {
        return fuzideshilianMapper.selectByBean(bean);
    }
}
