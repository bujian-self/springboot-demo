package com.bujian.caffeine.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bujian.caffeine.bean.FuzideshilianDo;
import com.bujian.caffeine.mapper.FuzideshilianMapper;
import com.bujian.caffeine.service.CacheService;
import com.bujian.caffeine.service.FuzideshilianService;
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
    @Autowired
    private CacheService cacheService;

    /**
     * 一般 先取后存
     */
    @Override
    public List<FuzideshilianDo> selectByBean(FuzideshilianDo bean) {
        log.info("FuzideshilianDo根据实体类获取信息: {}", bean);
        String key = "FuzideshilianDo::" + JSONObject.toJSONString(bean);
        if (bean != null) {
            log.info("从缓存中获取");
            List<FuzideshilianDo> fuzideshilianDos = cacheService.getList(key, FuzideshilianDo.class);
            if (fuzideshilianDos.size() > 0) {
                return fuzideshilianDos;
            }
            log.info("缓存中未获取到信息");
        }
        log.info("从数据库获取");
        List<FuzideshilianDo> fuzideshilianDos = fuzideshilianMapper.selectByBean(bean);
        if (fuzideshilianDos.size() > 0) {
            synchronized (this) {
                if (bean != null && !cacheService.hasKey(key)) {
                    cacheService.saveOrUpdate(key, fuzideshilianDos);
                    log.info("加入缓存中成功");
                }
            }
        }
        return fuzideshilianDos;
    }

    /**
     * 先存后取
     */
    public List<FuzideshilianDo> selectByBean2(FuzideshilianDo bean) {
        log.info("FuzideshilianDo根据实体类获取信息: {}", bean);
        String key = "FuzideshilianDo::" + JSONObject.toJSONString(bean);
        if (bean != null && !cacheService.hasKey(key)) {
            log.info("从数据库获取");
            List<FuzideshilianDo> fuzideshilianDos = fuzideshilianMapper.selectByBean(bean);
            if (fuzideshilianDos.size() > 0) {
                synchronized (this) {
                    if (!cacheService.hasKey(key)) {
                        cacheService.saveOrUpdate(key, fuzideshilianDos);
                        log.info("加入缓存中成功");
                    }
                }
            }
        }
        log.info("从缓存中获取");
        return cacheService.getList(key, FuzideshilianDo.class);
    }
}
