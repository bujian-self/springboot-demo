package com.bujian.cache.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bujian.cache.bean.FuzideshilianDo;
import com.bujian.cache.mapper.FuzideshilianMapper;
import com.bujian.cache.service.FuzideshilianService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service层 代码的实现
 * @author bujian
 * @date 2021/6/16 11:15
 */
@Service
@Slf4j
public class FuzideshilianServiceImpl extends ServiceImpl<FuzideshilianMapper, FuzideshilianDo> implements FuzideshilianService {

    @Autowired
    private FuzideshilianMapper fuzideshilianMapper;

    @Override
    public List<FuzideshilianDo> selectByBean(FuzideshilianDo bean) {
        log.info("FuzideshilianDo根据实体类查询信息: {}", bean);
        return fuzideshilianMapper.selectByBean(bean);
    }
}
