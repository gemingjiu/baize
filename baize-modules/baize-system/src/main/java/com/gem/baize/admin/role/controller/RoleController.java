package com.gem.baize.admin.role.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.role.entity.Role;
import com.gem.baize.admin.role.service.RoleService;
import com.gem.baize.api.admin.role.domain.dto.RoleDTO;
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
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取角色", description = "根据ID查询角色信息")
    public Result<RoleDTO> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        Role role = roleService.getById(id);
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(role, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建角色")
    public Result<Integer> create(@Valid @RequestBody RoleDTO dto) {
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        return Result.success(roleService.create(role));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody RoleDTO dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        roleService.update(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public Result<Void> delete(@PathVariable String id) {
        roleService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询角色")
    public Result<Page<RoleDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody RoleDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        Page<Role> page = roleService.page(pageParam, role);
        Page<RoleDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage);
        return Result.success(dtoPage);
    }
}
