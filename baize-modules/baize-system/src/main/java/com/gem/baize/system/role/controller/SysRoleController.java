package com.gem.baize.system.role.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.role.entity.SysRole;
import com.gem.baize.system.role.service.SysRoleService;
import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取角色", description = "根据ID查询角色信息")
    public Result<SysRoleDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        return Result.success(sysRoleService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建角色")
    public Result<Integer> create(@Valid @RequestBody SysRoleDto dto) {
        return Result.success(sysRoleService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysRoleDto dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        sysRoleService.updateById(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public Result<Void> delete(@PathVariable String id) {
        sysRoleService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询角色")
    public Result<Page<SysRoleDto>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @Valid @RequestBody SysRoleDto dto) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        return Result.success(sysRoleService.page(page, dto));
    }
}
