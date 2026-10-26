package com.gem.baize.system.role.controller;

import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.role.service.SysRoleDeptService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色部门关联控制器（自定义数据权限配置）
 * <p>
 * 与 {@code SysRoleMenuController} 同一 REST 风格：
 * 读取用 {@code GET /{roleId}/depts}，分配用 {@code POST /{roleId}/assign}。
 */
@RestController
@RequestMapping("/system/role/dept")
public class SysRoleDeptController {

    @Autowired
    private SysRoleDeptService sysRoleDeptService;

    @GetMapping("/{roleId}/depts")
    @Operation(summary = "获取角色已配置的部门ID列表")
    @RequiresPermission("system:role:query")
    public ApiResult<List<String>> getDeptIdsByRoleId(@PathVariable String roleId) {
        return ApiResult.ok(sysRoleDeptService.getDeptIdsByRoleId(roleId));
    }

    /**
     * 为角色分配部门（自定义数据权限范围）。
     * 复用 {@code system:role:edit}，与菜单/权限分配保持一致的权限口径。
     */
    @PostMapping("/{roleId}/assign")
    @Operation(summary = "为角色分配部门")
    @RequiresPermission("system:role:edit")
    public ApiResult<Void> assignDepts(@PathVariable String roleId,
                                       @RequestBody List<String> deptIds) {
        sysRoleDeptService.assignDepts(roleId, deptIds);
        return ApiResult.ok();
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色所有部门关联")
    @RequiresPermission("system:role:edit")
    public ApiResult<Void> removeByRoleId(@PathVariable String roleId) {
        sysRoleDeptService.removeByRoleId(roleId);
        return ApiResult.ok();
    }
}
