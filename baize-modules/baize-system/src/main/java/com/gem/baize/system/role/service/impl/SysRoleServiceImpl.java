package com.gem.baize.system.role.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.system.role.entity.SysRole;
import com.gem.baize.system.role.mapper.SysRoleMapper;
import com.gem.baize.system.role.service.SysRoleService;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 角色服务类实现
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public SysRole getById(String id) {
        return Optional.ofNullable(sysRoleMapper.selectById(id)).orElseThrow(() -> new NotFoundException("角色不存在"));

    }

    @Override
    public Integer create(SysRole sysRole) {
        int result = sysRoleMapper.insert(sysRole);
        if (result <= 0) {
            throw new DataCreationException("角色创建失败");
        }
        return result;
    }

    @Override
    public void update(SysRole sysRole) {
        int affectedRows = sysRoleMapper.updateById(sysRole);

        if (affectedRows <= 0) {
            throw new NotFoundException("角色信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("角色信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteById(String id) {
        int affectedRows = sysRoleMapper.deleteById(id);
        if (affectedRows <= 0) {
            throw new NotFoundException("角色信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("角色信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<SysRole> page(PageParam pageParam, SysRole sysRole) {
        return Optional.ofNullable(sysRoleMapper.selectPage(pageParam, sysRole))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到角色信息"));
    }
}
