package com.gem.baize.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.system.user.entity.SysUserRole;
import com.gem.baize.system.user.mapper.SysUserRoleMapper;
import com.gem.baize.system.user.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联服务实现
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Override
    public List<String> getRoleIdsByUserId(String userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        return list(wrapper).stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserIdsByRoleId(String roleId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, roleId);
        return list(wrapper).stream()
                .map(SysUserRole::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(String userId, List<String> roleIds, String tenantId) {
        // 先删除用户现有角色
        removeByUserId(userId);

        // 批量添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = new ArrayList<>();
            for (String roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setTenantId(tenantId);
                userRoles.add(userRole);
            }
            saveBatch(userRoles);
        }
    }

    @Override
    public void removeByUserId(String userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        remove(wrapper);
    }

    @Override
    public void removeByRoleId(String roleId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, roleId);
        remove(wrapper);
    }
}
