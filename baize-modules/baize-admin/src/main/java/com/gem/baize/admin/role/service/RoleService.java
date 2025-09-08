package com.gem.baize.admin.role.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.admin.role.entity.Role;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 角色服务类接口
 */
public interface RoleService extends IService<Role> {

    Role getById(String id);

    Integer create(Role role);

    void update(Role role);

    void deleteById(String id);

    Page<Role> page(PageParam pageParam, Role role);
}
