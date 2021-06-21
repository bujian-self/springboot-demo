package com.bujian.test.controller;


import com.bujian.test.bean.FuzideshilianDo;
import com.bujian.test.service.FuzideshilianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * controller层 代码
 * @author lijie
 * @date 2021/6/18 09:51
 */
@RestController
@RequestMapping("fuzideshilian")
public class FuzideshilianController {

    @Autowired
    private FuzideshilianService fuzideshilianService;

    @GetMapping("/findInfoById/{id}")
    public Object findInfoById(@PathVariable("id") Long id){
        return fuzideshilianService.selectById(id);
    }

    @PostMapping("/findInfo")
    public Object findInfo(@RequestBody FuzideshilianDo bean){
        return fuzideshilianService.selectByBean(bean);
    }
}
