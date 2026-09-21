package com.gem.baize.system.perm.controller;

import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.perm.service.SysRolePermService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色权限关联控制器
 */
@RestController
@RequestMapping("/system/role/perm")
public class SysRolePermController {

    @Autowired
    private SysRolePermService sysRolePermService;

    @GetMapping("/{roleId}/perms")
    @Operation(summary = "获取角色权限ID列表")
    public ApiResult<List<String>> getPermIdsByRoleId(@PathVariable String roleId) {
        return ApiResult.ok(sysRolePermService.getPermIdsByRoleId(roleId));
    }

    @GetMapping("/roles/codes")
    @Operation(summary = "根据角色ID列表获取权限编码列表")
    public ApiResult<List<String>> getPermCodesByRoleIds(@RequestParam List<String> roleIds) {
        return ApiResult.ok(sysRolePermService.getPermCodesByRoleIds(roleIds));
    }

    @PostMapping("/{roleId}/assign")
    @Operation(summary = "为角色分配权限")
    public ApiResult<Void> assignPerms(@PathVariable String roleId,
                                       @RequestBody List<String> permIds,
                                       @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        sysRolePermService.assignPerms(roleId, permIds, tenantId);
        return ApiResult.ok();
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色所有权限关联")
    public ApiResult<Void> removeByRoleId(@PathVariable String roleId) {
        sysRolePermService.removeByRoleId(roleId);
        return ApiResult.ok();
    }
}
