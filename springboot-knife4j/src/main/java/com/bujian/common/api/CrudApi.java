package com.bujian.common.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 提供基于Crud的API实现<br>
 * 继承该方法,需要传入service,和entity类<br>
 * @author bujian
 * @date 2021/7/6 16:43
 */
public class CrudApi<S extends IService<V>, V> {
	private final Class<V> clazz;
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

	@ApiImplicitParam(name = "vo", value = "实体类数据必须包含主键", required = true)
	@ApiOperation(value = "主键更新对象")
	@PutMapping(value = "/")
	public Object updateById(@RequestBody V vo) {
		return baseService.updateById(vo);
	}

	@ApiImplicitParam(name = "id", value = "主键", required = true, example = "1")
	@ApiOperation(value = "主键查询")
	@GetMapping(value = "/{id}")
	public Object select(@PathVariable(value = "id") Serializable id) {
		return baseService.getById(id);
	}

	@ApiImplicitParam(name = "ids", value = "主键", required = true, example = "1", dataType = "List")
	@ApiOperation(value = "主键删除对象")
	@DeleteMapping(value = "/{ids}")
	public Object delete(@PathVariable(value = "ids") List<Serializable> ids) {
		return baseService.removeByIds(ids);
	}

	/**
	 * 包含分页查询的数据
	 */
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
