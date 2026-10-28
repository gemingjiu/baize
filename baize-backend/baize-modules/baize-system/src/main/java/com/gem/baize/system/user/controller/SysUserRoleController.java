package com.gem.baize.system.user.controller;

import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.user.service.SysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.gem.baize.common.core.constant.PermCodes;
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
    @RequiresPermission(PermCodes.USER_QUERY)
    public ApiResult<List<String>> getRoleIdsByUserId(@PathVariable String userId) {
        return ApiResult.ok(sysUserRoleService.getRoleIdsByUserId(userId));
    }

    /**
     * 为用户分配角色。
     * 权限码与前端 Authorized 保持一致（system:user:edit），
     * 历史上该端点缺少 @RequiresPermission，等于任何登录用户都能给自己/他人配角色。
     */
    @PostMapping("/{userId}/assign")
    @Operation(summary = "为用户分配角色")
    @RequiresPermission(PermCodes.USER_EDIT)
    public ApiResult<Void> assignRoles(@PathVariable String userId,
                                       @RequestBody List<String> roleIds,
                                       @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        sysUserRoleService.assignRoles(userId, roleIds, tenantId);
        return ApiResult.ok();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户所有角色关联")
    @RequiresPermission(PermCodes.USER_EDIT)
    public ApiResult<Void> removeByUserId(@PathVariable String userId) {
        sysUserRoleService.removeByUserId(userId);
        return ApiResult.ok();
    }
}
