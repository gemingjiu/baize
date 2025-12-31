package com.gem.baize.system.perm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.perm.domain.dto.SysPermDto;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.vo.Result;
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
    public Result<SysPermDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        return Result.success(sysPermService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建权限")
    public Result<Integer> create(@Valid @RequestBody SysPermDto dto) {
        return Result.success(sysPermService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysPermDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        sysPermService.updateById(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    public Result<Void> delete(@PathVariable String id) {
        sysPermService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询权限")
    public Result<Page<SysPermDto>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @Valid @RequestBody SysPermDto dto) {
        Page<SysPerm> page = new Page<>(pageNum, pageSize);
        return Result.success(sysPermService.page(page, dto));
    }
}
