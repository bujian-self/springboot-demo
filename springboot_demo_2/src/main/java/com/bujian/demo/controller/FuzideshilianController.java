package com.bujian.demo.controller;


import com.bujian.demo.service.FuzideshilianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/findInfoById/{id}")
    public Object findInfo(@PathVariable("id") Long id){
        return fuzideshilianService.selectById(id);
    }
}
