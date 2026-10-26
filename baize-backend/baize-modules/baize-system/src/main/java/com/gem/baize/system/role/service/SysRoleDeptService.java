package com.gem.baize.system.role.service;

import java.util.List;

/**
 * 角色部门关联服务（自定义数据权限配置）
 */
public interface SysRoleDeptService {

    /**
     * 获取角色已配置的部门ID列表
     */
    List<String> getDeptIdsByRoleId(String roleId);

    /**
     * 为角色分配部门（自定义数据权限范围）。
     * 采用"先全删再全插"，tenant_id 由 BaseEntityObjectHandler 自动填充。
     */
    void assignDepts(String roleId, List<String> deptIds);

    /**
     * 删除角色所有部门关联
     */
    void removeByRoleId(String roleId);
}
