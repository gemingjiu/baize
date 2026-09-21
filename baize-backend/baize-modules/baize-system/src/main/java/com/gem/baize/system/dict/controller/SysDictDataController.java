package com.gem.baize.system.dict.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.dict.domain.dto.SysDictDataDto;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.dict.entity.SysDictData;
import com.gem.baize.system.dict.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    @Autowired
    private SysDictDataService sysDictDataService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取字典数据")
    @RequiresPermission("system:dict:query")
    public ApiResult<SysDictDataDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysDictDataService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建字典数据")
    @RequiresPermission("system:dict:add")
    public ApiResult<Integer> create(@Valid @RequestBody SysDictDataDto dto) {
        sysDictDataService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新字典数据")
    @RequiresPermission("system:dict:edit")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysDictDataDto dto) {
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }
        sysDictDataService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典数据")
    @RequiresPermission("system:dict:remove")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysDictDataService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询字典数据")
    @RequiresPermission("system:dict:list")
    public ApiResult<Page<SysDictDataDto>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                @Valid @RequestBody SysDictDataDto dto) {
        Page<SysDictData> page = new Page<>(current, size);
        return ApiResult.ok(sysDictDataService.page(page, dto));
    }

    @GetMapping("/type/{dictType}")
    @Operation(summary = "根据字典类型获取字典数据列表")
    @RequiresPermission("system:dict:query")
    public ApiResult<List<SysDictDataDto>> listByDictType(@PathVariable String dictType) {
        if (StringUtils.isBlank(dictType)) {
            throw new ParamException("字典类型不能为空");
        }
        return ApiResult.ok(sysDictDataService.listByDictType(dictType));
    }
}
