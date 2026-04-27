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

    void create(SysTenantDto sysTenantDto);

    void updateById(SysTenantDto sysTenantDto);

    void removeById(String id);

    Page<SysTenantDto> page(Page<SysTenant> page, SysTenantDto sysTenantDto);

    /**
     * 根据租户编码或域名获取租户信息
     * @param tenant 租户编码或域名
     * @return 租户信息
     */
    SysTenantDto getByTenantCodeOrDomain(String tenant);
}
