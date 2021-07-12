package com.bujian.cache.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bujian.cache.bean.FuzideshilianDo;
import com.bujian.cache.mapper.FuzideshilianMapper;
import com.bujian.cache.service.FuzideshilianService;
import com.bujian.cache.service.RedisSimpleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private RedisSimpleService redisService;

    /**
     * 一般 先取后存
     */
    @Override
    public List<FuzideshilianDo> selectByBean(FuzideshilianDo bean) {
        log.info("FuzideshilianDo根据实体类获取信息: {}", bean);
        String key = "FuzideshilianDo::" + JSONObject.toJSONString(bean);
        if (bean != null) {
            log.info("从缓存中获取");
            List<FuzideshilianDo> fuzideshilianDos = redisService.getList(key, FuzideshilianDo.class);
            if (fuzideshilianDos.size() > 0) {
                return fuzideshilianDos;
            }
            log.info("缓存中未获取到信息");
        }
        log.info("从数据库获取");
        List<FuzideshilianDo> fuzideshilianDos = fuzideshilianMapper.selectByBean(bean);
        if (fuzideshilianDos.size() > 0) {
            synchronized (this) {
                if (bean != null && !redisService.hasKey(key)) {
                    redisService.saveOrUpdate(key, fuzideshilianDos);
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
        if (bean != null && !redisService.hasKey(key)) {
            log.info("从数据库获取");
            List<FuzideshilianDo> fuzideshilianDos = fuzideshilianMapper.selectByBean(bean);
            if (fuzideshilianDos.size() > 0) {
                synchronized (this) {
                    if (!redisService.hasKey(key)) {
                        redisService.saveOrUpdate(key, fuzideshilianDos);
                        log.info("加入缓存中成功");
                    }
                }
            }
        }
        log.info("从缓存中获取");
        return redisService.getList(key, FuzideshilianDo.class);
    }
}
