package com.gem.baize.system.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.system.tenant.entity.SysTenant;

/**
 * 租户服务类接口
 */
public interface SysTenantService extends IService<SysTenant> {

    SysTenantDto getById(String id);

    Integer create(SysTenantDto sysTenantDto);

    void update(SysTenantDto sysTenantDto);

    Page<SysTenantDto> page(Page<SysTenant> page, SysTenantDto sysTenantDto);
}
