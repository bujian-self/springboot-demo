package com.bujian.swagger.controller;

import com.bujian.swagger.bean.FuzideshilianDo;
import com.bujian.swagger.service.FuzideshilianService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * controller层 代码
 * @author bujian
 * @date 2021/6/18 09:51
 */
@Api(tags="父子的试炼模块")
@RestController
@RequestMapping("fuzideshilian")
public class FuzideshilianController {

    @Autowired
    private FuzideshilianService fuzideshilianService;

    @ApiOperation(value="查询信息",notes="根据id查询", httpMethod = "GET" )
    @GetMapping("/findInfoById/{id}")
    public Object findInfoById(@ApiParam(value = "id",required = true,example = "1") @PathVariable("id") Long id){
        return fuzideshilianService.selectById(id);
    }

    @ApiOperation(value="查询信息",notes="根据实体类查询", httpMethod = "POST" )
    @PostMapping("/findInfo")
    public Object findInfo(@ApiParam(value = "父子的试炼实体类对象",required = true) @RequestBody FuzideshilianDo bean){
        return fuzideshilianService.selectByBean(bean);
    }
}
