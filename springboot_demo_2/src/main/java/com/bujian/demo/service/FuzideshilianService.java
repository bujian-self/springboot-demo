package com.bujian.demo.service;

import com.bujian.demo.bean.FuzideshilianDo;
import com.bujian.demo.mapper.FuzideshilianMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Service层 代码
 * @author lijie
 * @date 2021/6/16 11:15
 */
public interface FuzideshilianService {

    public FuzideshilianDo selectById(Long id);

}
