package com.bujian.common.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 提供基于Crud的API实现<br>
 * 继承该方法,需要传入service,和entity类<br>
 *
 * @author bujian
 * @date 2021/7/6 16:43
 */
public class CrudApi<S extends IService<V>, V> {
    private final Class<V> clazz;
    /**
     * null 字符串 会在建 key 时删除<br/>
     * 在配置 RedisCacheConfiguration 时 剔除了null值
     */
    private final String cacheNames = "null";
    @Autowired
    protected S baseService;

    public CrudApi() {
        clazz = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    public S getBaseService() {
        return this.baseService;
    }

    @ApiImplicitParam(name = "vo", value = "实体类", required = true)
    @ApiOperation(value = "创建对象")
    @PostMapping(value = "/")
    public Object create(@RequestBody V vo) {
        return baseService.save(vo);
    }

    /**
     * `@CachePut` 先执行方法体，然后根据 key 去 cache 中更新<br/>
     * 个人觉得没有必要,直接使用 CacheEvict 删除即可
     */
    @CachePut(cacheNames = cacheNames, key = "targetClass.getGenericSuperclass().getActualTypeArguments()[1].getSimpleName()+'::'+#p0")
    @ApiImplicitParam(name = "vo", value = "实体类数据必须包含主键", required = true)
    @ApiOperation(value = "主键更新对象")
    @PutMapping(value = "/")
    public Object updateById(@RequestBody V vo) {
        return baseService.updateById(vo);
    }

    /**
     * `@Cacheable` 先根据 key 去查询 cache 中是否有这个 key<br/>
     * 有则直接返回,没有则执行方法体<br/>
     * 最后将返回的值添加到cache中,key为传入的key<br/>
     * 注意: 这里的key设置的是 `类名:第一个入参`
     */
    @Cacheable(cacheNames = cacheNames, key = "targetClass.getGenericSuperclass().getActualTypeArguments()[1].getSimpleName()+'::'+#p0")
    @ApiImplicitParam(name = "id", value = "主键", required = true, example = "1")
    @ApiOperation(value = "主键查询")
    @GetMapping(value = "/{id}")
    public Object select(@PathVariable(value = "id") Serializable id) {
        return baseService.getById(id);
    }

    /**
     * `@CacheEvict` 先执行方法体，然后根据 key 去 cache 中删除<br/>
     */
    @CacheEvict(cacheNames = cacheNames, key = "targetClass.getGenericSuperclass().getActualTypeArguments()[1].getSimpleName()+'::'+#p0")
    @ApiImplicitParam(name = "ids", value = "主键", required = true, example = "1", dataType = "List")
    @ApiOperation(value = "主键删除对象")
    @DeleteMapping(value = "/{ids}")
    public Object delete(@PathVariable(value = "ids") List<Serializable> ids) {
        return baseService.removeByIds(ids);
    }

    /**
     * 包含分页查询的数据
     */
    @Cacheable(cacheNames = cacheNames, key = "targetClass.getGenericSuperclass().getActualTypeArguments()[1].getSimpleName()+'::'+#p0")
    @ApiImplicitParam(name = "json", value = "包含分页参数的实体类json", required = true)
    @ApiOperation(value = "实体类分页查询对象")
    @PostMapping(value = "/searchEntity")
    public Object searchEntity(@RequestBody JSONObject json) {
        IPage<V> iPage = new Page<>(json.getIntValue("current"), json.getIntValue("size"));
        QueryWrapper<V> queryWrapper = new QueryWrapper<>();
        if (!json.isEmpty()) {
            queryWrapper.setEntity(json.toJavaObject(clazz));
        }
        return baseService.page(iPage, queryWrapper);
    }

}
