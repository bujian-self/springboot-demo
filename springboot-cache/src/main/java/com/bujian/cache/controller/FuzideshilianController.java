package com.bujian.cache.controller;

import com.bujian.cache.bean.FuzideshilianDo;
import com.bujian.cache.service.FuzideshilianService;
import com.bujian.cache.utils.RedisUtil;
import com.bujian.common.api.CrudApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * controller层 代码
 * @author bujian
 * @date 2021/6/18 09:51
 */
@Api(tags="夫子的试炼模块")
@RestController
@RequestMapping("fuzideshilian")
@Slf4j
public class FuzideshilianController extends CrudApi<FuzideshilianService, FuzideshilianDo> {

    @Autowired
    private FuzideshilianService fuzideshilianService;

    @Cacheable(cacheNames = "FuzideshilianDo")
    @ApiImplicitParam(name = "id", value = "主键", required = true, example = "1")
    @ApiOperation(value="根据主键查询信息")
    @GetMapping("findInfoById/{id}")
    public Object findInfoById(@PathVariable("id") Long id){
        log.info("根据id查询信息: {}", id);
        return fuzideshilianService.getById(id);
    }

    @Cacheable(cacheNames = "FuzideshilianDo", key = "#bean")
    @ApiImplicitParam(name = "bean", value = "Fuzideshilian实体类", required = true)
    @ApiOperation(value="根据实体类查询信息")
    @PostMapping("findInfo")
    public Object findInfo(@RequestBody FuzideshilianDo bean){
        log.info("根据实体类查询信息: {}", bean);
        return fuzideshilianService.selectByBean(bean);
    }

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value="redis set 测试redis插入")
    @GetMapping("redisSet")
    public Object redisSet(){
        redisTemplate.opsForValue().set("keyTest","valueTest");
        redisUtil.sSet("keyTest2","valueTest2","valueTest3","valueTest4","valueTest5");
        return null;
    }
    @ApiOperation(value="redis get 测试redis 获取")
    @GetMapping("redisGet")
    public Object redisGet(){
        List<Object> keyTest2 = new ArrayList<>();
        keyTest2.addAll(Arrays.asList(redisUtil.sGet("keyTest2").toArray()));
        Object keyTest = redisTemplate.opsForValue().get("keyTest");
        keyTest2.add(keyTest);
        return keyTest2;
    }
}
