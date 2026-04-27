package com.gem.baize.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.system.user.entity.SysUserRole;

import java.util.List;

/**
 * 用户角色关联服务
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户ID获取角色ID列表
     */
    List<String> getRoleIdsByUserId(String userId);

    /**
     * 根据角色ID获取用户ID列表
     */
    List<String> getUserIdsByRoleId(String roleId);

    /**
     * 为用户分配角色
     */
    void assignRoles(String userId, List<String> roleIds, String tenantId);

    /**
     * 删除用户所有角色关联
     */
    void removeByUserId(String userId);

    /**
     * 删除角色所有用户关联
     */
    void removeByRoleId(String roleId);
}
