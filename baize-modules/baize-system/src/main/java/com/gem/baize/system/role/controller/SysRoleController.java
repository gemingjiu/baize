package com.gem.baize.system.role.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.role.entity.SysRole;
import com.gem.baize.system.role.service.SysRoleService;
import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.user.service.SysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取角色", description = "根据ID查询角色信息")
    public ApiResult<SysRoleDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysRoleService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建角色")
    public ApiResult<Integer> create(@Valid @RequestBody SysRoleDto dto) {
        sysRoleService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysRoleDto dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }
        sysRoleService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysRoleService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询角色")
    public ApiResult<Page<SysRoleDto>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @Valid @RequestBody SysRoleDto dto) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        return ApiResult.ok(sysRoleService.page(page, dto));
    }

    @GetMapping("/{roleId}/users")
    @Operation(summary = "获取角色的用户ID列表")
    public ApiResult<List<String>> getUserIdsByRoleId(@PathVariable String roleId) {
        return ApiResult.ok(sysUserRoleService.getUserIdsByRoleId(roleId));
    }

    @PostMapping("/{roleId}/authUser")
    @Operation(summary = "为角色分配用户")
    public ApiResult<Void> assignUsersToRole(@PathVariable String roleId,
                                             @RequestBody List<String> userIds,
                                             @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        // 删除角色现有用户关联
        sysUserRoleService.removeByRoleId(roleId);
        // 批量添加新用户
        if (userIds != null && !userIds.isEmpty()) {
            for (String userId : userIds) {
                sysUserRoleService.assignRoles(userId, List.of(roleId), tenantId);
            }
        }
        return ApiResult.ok();
    }
}
