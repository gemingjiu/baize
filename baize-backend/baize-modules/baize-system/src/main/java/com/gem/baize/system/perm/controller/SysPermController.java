package com.gem.baize.system.perm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.perm.domain.dto.SysPermDto;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.perm.entity.SysPerm;
import com.gem.baize.system.perm.service.SysPermService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/perm")
public class SysPermController {
    @Autowired
    private SysPermService sysPermService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取权限", description = "根据ID查询权限信息")
    @RequiresPermission("system:perm:query")
    public ApiResult<SysPermDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysPermService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建权限")
    @RequiresPermission("system:perm:add")
    public ApiResult<Integer> create(@Valid @RequestBody SysPermDto dto) {
        sysPermService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    @RequiresPermission("system:perm:edit")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysPermDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }
        sysPermService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    @RequiresPermission("system:perm:remove")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysPermService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询权限")
    @RequiresPermission("system:perm:list")
    public ApiResult<Page<SysPermDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysPermDto dto) {
        Page<SysPerm> page = new Page<>(current, size);
        return ApiResult.ok(sysPermService.page(page, dto));
    }
}
