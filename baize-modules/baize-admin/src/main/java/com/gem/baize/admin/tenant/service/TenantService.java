package com.gem.baize.admin.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.admin.tenant.entity.Tenant;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 租户服务类接口
 */
public interface TenantService extends IService<Tenant> {

    Tenant getById(String id);

    Integer create(Tenant tenant);

    void update(Tenant tenant);

    void deleteById(String id);

    Page<Tenant> page(PageParam pageParam, Tenant tenant);
}
