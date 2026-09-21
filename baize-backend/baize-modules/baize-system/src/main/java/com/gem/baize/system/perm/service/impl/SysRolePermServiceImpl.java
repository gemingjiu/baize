package com.gem.baize.system.perm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gem.baize.system.perm.entity.SysPerm;
import com.gem.baize.system.perm.entity.SysRolePerm;
import com.gem.baize.system.perm.mapper.SysRolePermMapper;
import com.gem.baize.system.perm.service.SysPermService;
import com.gem.baize.system.perm.service.SysRolePermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色权限关联服务实现
 */
@Service
public class SysRolePermServiceImpl implements SysRolePermService {

    @Autowired
    private SysRolePermMapper sysRolePermMapper;

    @Autowired
    private SysPermService sysPermService;

    @Override
    public List<String> getPermIdsByRoleId(String roleId) {
        LambdaQueryWrapper<SysRolePerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePerm::getRoleId, roleId);
        return sysRolePermMapper.selectList(wrapper).stream()
                .map(SysRolePerm::getPermId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPermCodesByRoleIds(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取所有关联的权限ID
        LambdaQueryWrapper<SysRolePerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRolePerm::getRoleId, roleIds);
        List<String> permIds = sysRolePermMapper.selectList(wrapper).stream()
                .map(SysRolePerm::getPermId)
                .distinct()
                .collect(Collectors.toList());

        if (permIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取权限编码列表
        LambdaQueryWrapper<SysPerm> permWrapper = new LambdaQueryWrapper<>();
        permWrapper.in(SysPerm::getId, permIds);
        return sysPermService.list(permWrapper).stream()
                .map(SysPerm::getPermCode)
                .filter(code -> code != null && !code.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPerms(String roleId, List<String> permIds, String tenantId) {
        // 先删除角色现有权限
        removeByRoleId(roleId);

        // 批量添加新权限
        if (permIds != null && !permIds.isEmpty()) {
            for (String permId : permIds) {
                SysRolePerm rolePerm = new SysRolePerm();
                rolePerm.setRoleId(roleId);
                rolePerm.setPermId(permId);
                sysRolePermMapper.insert(rolePerm);
            }
        }
    }

    @Override
    public void removeByRoleId(String roleId) {
        LambdaQueryWrapper<SysRolePerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePerm::getRoleId, roleId);
        sysRolePermMapper.delete(wrapper);
    }
}
