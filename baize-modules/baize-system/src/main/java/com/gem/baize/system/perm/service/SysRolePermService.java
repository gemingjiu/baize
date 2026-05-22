package com.gem.baize.system.perm.service;

import com.gem.baize.system.perm.entity.SysRolePerm;

import java.util.List;

/**
 * 角色权限关联服务
 */
public interface SysRolePermService {

    /**
     * 根据角色ID获取权限ID列表
     */
    List<String> getPermIdsByRoleId(String roleId);

    /**
     * 根据角色ID列表获取权限编码列表
     */
    List<String> getPermCodesByRoleIds(List<String> roleIds);

    /**
     * 为角色分配权限
     */
    void assignPerms(String roleId, List<String> permIds, String tenantId);

    /**
     * 删除角色所有权限关联
     */
    void removeByRoleId(String roleId);
}
