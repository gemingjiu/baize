package com.gem.baize.system.role.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.system.role.entity.SysRole;

/**
 * 角色服务类接口
 */
public interface SysRoleService extends IService<SysRole> {

    SysRoleDto getById(String id);

    Integer create(SysRoleDto sysRoleDto);

    void updateById(SysRoleDto sysRoleDto);

    void removeById(String id);

    Page<SysRoleDto> page(Page<SysRole> page, SysRoleDto sysRoleDto);
}
