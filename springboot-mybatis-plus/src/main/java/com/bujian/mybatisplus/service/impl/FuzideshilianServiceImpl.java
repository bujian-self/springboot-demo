package com.bujian.mybatisplus.service.impl;

import com.bujian.mybatisplus.bean.FuzideshilianDo;
import com.bujian.mybatisplus.mapper.FuzideshilianMapper;
import com.bujian.mybatisplus.service.FuzideshilianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service层 代码的实现
 * @author lijie
 * @date 2021/6/16 11:15
 */
@Service
public class FuzideshilianServiceImpl implements FuzideshilianService {

    @Autowired
    private FuzideshilianMapper fuzideshilianMapper;

    @Override
    public FuzideshilianDo selectById(Long id) {
        assert id != null;
        return fuzideshilianMapper.selectById(id);
    }
}
