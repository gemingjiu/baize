package com.gem.baize.system.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.system.user.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUserDto getById(String id);

    Integer create(SysUserDto sysUserDto);

    void updateById(SysUserDto sysUserDto);

    void removeById(String id);

    Page<SysUserDto> page(Page<SysUser> page, SysUserDto sysUserDto);
}
