package com.gem.baize.system.role.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.role.entity.SysRole;
import com.gem.baize.system.role.service.SysRoleService;
import com.gem.baize.api.system.role.domain.dto.SysRoleDTO;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.common.core.model.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取角色", description = "根据ID查询角色信息")
    public Result<SysRoleDTO> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        SysRole sysRole = sysRoleService.getById(id);
        SysRoleDTO dto = new SysRoleDTO();
        BeanUtils.copyProperties(sysRole, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建角色")
    public Result<Integer> create(@Valid @RequestBody SysRoleDTO dto) {
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(dto, sysRole);
        return Result.success(sysRoleService.create(sysRole));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysRoleDTO dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(dto, sysRole);
        sysRoleService.update(sysRole);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public Result<Void> delete(@PathVariable String id) {
        sysRoleService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询角色")
    public Result<Page<SysRoleDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysRoleDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(dto, sysRole);
        Page<SysRole> page = sysRoleService.page(pageParam, sysRole);
        Page<SysRoleDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage);
        return Result.success(dtoPage);
    }
}
