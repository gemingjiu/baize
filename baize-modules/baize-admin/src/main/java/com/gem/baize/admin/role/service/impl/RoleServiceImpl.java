package com.gem.baize.admin.role.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.admin.role.entity.Role;
import com.gem.baize.admin.role.mapper.RoleMapper;
import com.gem.baize.admin.role.service.RoleService;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Role getById(String id) {
        return Optional.ofNullable(roleMapper.selectById(id)).orElseThrow(() -> new NotFoundException("角色不存在"));

    }

    @Override
    public Integer create(Role role) {
        int result = roleMapper.insert(role);
        if (result <= 0) {
            throw new DataCreationException("角色创建失败");
        }
        return result;
    }

    @Override
    public void update(Role role) {
        int affectedRows = roleMapper.updateById(role);

        if (affectedRows <= 0) {
            throw new NotFoundException("角色信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("角色信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteById(String id) {
        int affectedRows = roleMapper.deleteById(id);
        if (affectedRows <= 0) {
            throw new NotFoundException("角色信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("角色信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<Role> page(PageParam pageParam, Role role) {
        return Optional.ofNullable(roleMapper.selectPage(pageParam, role))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到角色信息"));
    }
}
