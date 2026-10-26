package com.gem.baize.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gem.baize.system.role.entity.SysRoleDept;
import com.gem.baize.system.role.mapper.SysRoleDeptMapper;
import com.gem.baize.system.role.service.SysRoleDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色部门关联服务实现
 */
@Service
public class SysRoleDeptServiceImpl implements SysRoleDeptService {

    @Autowired
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Override
    public List<String> getDeptIdsByRoleId(String roleId) {
        LambdaQueryWrapper<SysRoleDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleDept::getRoleId, roleId);
        return sysRoleDeptMapper.selectList(wrapper).stream()
                .map(SysRoleDept::getDeptId)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignDepts(String roleId, List<String> deptIds) {
        // 先删除角色现有部门
        removeByRoleId(roleId);

        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }

        // 批量添加（tenant_id 由 BaseEntityObjectHandler 自动填充）
        for (String deptId : deptIds) {
            if (deptId == null || deptId.isBlank()) {
                continue;
            }
            SysRoleDept roleDept = new SysRoleDept();
            roleDept.setRoleId(roleId);
            roleDept.setDeptId(deptId);
            sysRoleDeptMapper.insert(roleDept);
        }
    }

    @Override
    public void removeByRoleId(String roleId) {
        LambdaQueryWrapper<SysRoleDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleDept::getRoleId, roleId);
        sysRoleDeptMapper.delete(wrapper);
    }
}
