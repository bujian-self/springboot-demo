package com.bujian.server.controller;

import com.bujian.common.api.CrudApi;
import com.bujian.server.bean.FuzideshilianDo;
import com.bujian.server.service.FuzideshilianService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller层 代码
 *
 * @author bujian
 * @date 2021/6/18 09:51
 */
@Api(tags = "夫子的试炼模块")
@RestController
@RequestMapping("fuzideshilian")
@Slf4j
public class FuzideshilianController extends CrudApi<FuzideshilianService, FuzideshilianDo> {

    @Autowired
    private FuzideshilianService fuzideshilianService;

    @Cacheable(cacheNames = "FuzideshilianDo")
    @ApiImplicitParam(name = "id", value = "主键", required = true, example = "1")
    @ApiOperation(value = "根据主键查询信息")
    @GetMapping("findInfoById/{id}")
    public Object findInfoById(@PathVariable("id") Long id) {
        log.info("根据id查询信息: {}", id);
        return fuzideshilianService.getById(id);
    }

    @ApiImplicitParam(name = "bean", value = "Fuzideshilian实体类", required = true)
    @ApiOperation(value = "根据实体类查询信息")
    @PostMapping("findInfo")
    public Object findInfo(@RequestBody FuzideshilianDo bean) {
        log.info("根据实体类查询信息: {}", bean);
        return fuzideshilianService.selectByBean(bean);
    }
}
