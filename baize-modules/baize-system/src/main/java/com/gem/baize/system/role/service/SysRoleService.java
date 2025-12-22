package com.gem.baize.system.role.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.system.role.entity.SysRole;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 角色服务类接口
 */
public interface SysRoleService extends IService<SysRole> {

    SysRole getById(String id);

    Integer create(SysRole sysRole);

    void update(SysRole sysRole);

    void deleteById(String id);

    Page<SysRole> page(PageParam pageParam, SysRole sysRole);
}
