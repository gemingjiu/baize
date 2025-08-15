package com.gem.baize.admin.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.admin.tenant.entity.Tenant;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 租户服务类接口
 */
public interface TenantService extends IService<Tenant> {

    Tenant getByBizId(String bizId);

    Integer create(Tenant tenant);

    void updateByBizId(Tenant tenant);

    void deleteByBizId(String bizId);

    Page<Tenant> page(PageParam pageParam, Tenant tenant);
}
