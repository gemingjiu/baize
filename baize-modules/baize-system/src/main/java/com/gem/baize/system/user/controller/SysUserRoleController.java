package com.gem.baize.system.user.controller;

import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.user.service.SysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色关联控制器
 */
@RestController
@RequestMapping("/system/user/role")
public class SysUserRoleController {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @GetMapping("/{userId}/roles")
    @Operation(summary = "获取用户角色ID列表")
    public ApiResult<List<String>> getRoleIdsByUserId(@PathVariable String userId) {
        return ApiResult.ok(sysUserRoleService.getRoleIdsByUserId(userId));
    }

    @PostMapping("/{userId}/assign")
    @Operation(summary = "为用户分配角色")
    public ApiResult<Void> assignRoles(@PathVariable String userId,
                                       @RequestBody List<String> roleIds,
                                       @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        sysUserRoleService.assignRoles(userId, roleIds, tenantId);
        return ApiResult.ok();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户所有角色关联")
    public ApiResult<Void> removeByUserId(@PathVariable String userId) {
        sysUserRoleService.removeByUserId(userId);
        return ApiResult.ok();
    }
}
