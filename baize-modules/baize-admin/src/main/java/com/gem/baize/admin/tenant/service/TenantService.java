package com.gem.baize.admin.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.admin.tenant.entity.Tenant;
import com.gem.baize.common.core.model.PageParam;

/**
 * 租户服务类接口
 */
public interface TenantService extends IService<Tenant> {
    Tenant getById(String id);

    Integer create(Tenant tenant);

    void update(Tenant tenant);

    void delete(String id);

    Page<Tenant> page(PageParam pageParam, Tenant tenant);
}
