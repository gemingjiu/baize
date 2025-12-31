package com.gem.baize.system.perm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.perm.domain.dto.SysPermDto;
import com.gem.baize.system.perm.entity.SysPerm;

/**
 * 部门服务类接口
 */
public interface SysPermService extends IService<SysPerm> {

    SysPermDto getById(String id);

    Integer create(SysPermDto sysPermDto);

    void updateById(SysPermDto sysPermDto);

    void removeById(String id);

    Page<SysPermDto> page(Page<SysPerm> page, SysPermDto sysPermDto);
}
