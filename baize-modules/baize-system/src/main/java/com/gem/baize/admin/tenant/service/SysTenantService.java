package com.gem.baize.admin.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.admin.tenant.entity.SysTenant;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 租户服务类接口
 */
public interface SysTenantService extends IService<SysTenant> {

    SysTenant getById(String id);

    Integer create(SysTenant sysTenant);

    void update(SysTenant sysTenant);

    void deleteById(String id);

    Page<SysTenant> page(PageParam pageParam, SysTenant sysTenant);
}
