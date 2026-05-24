package com.gem.baize.system.dict.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.dict.domain.dto.SysDictTypeDto;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.dict.entity.SysDictType;
import com.gem.baize.system.dict.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取字典类型")
    @RequiresPermission("system:dict:query")
    public ApiResult<SysDictTypeDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysDictTypeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建字典类型")
    @RequiresPermission("system:dict:add")
    public ApiResult<Integer> create(@Valid @RequestBody SysDictTypeDto dto) {
        sysDictTypeService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新字典类型")
    @RequiresPermission("system:dict:edit")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysDictTypeDto dto) {
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }
        sysDictTypeService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典类型")
    @RequiresPermission("system:dict:remove")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysDictTypeService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询字典类型")
    @RequiresPermission("system:dict:list")
    public ApiResult<Page<SysDictTypeDto>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                @Valid @RequestBody SysDictTypeDto dto) {
        Page<SysDictType> page = new Page<>(current, size);
        return ApiResult.ok(sysDictTypeService.page(page, dto));
    }
}
