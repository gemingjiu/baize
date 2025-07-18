package com.gem.baize.admin.tenant.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.admin.tenant.entity.Tenant;
import com.gem.baize.admin.tenant.mapper.TenantMapper;
import com.gem.baize.admin.tenant.service.TenantService;
import com.gem.baize.common.core.model.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 租户服务类实现
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Autowired
    private TenantMapper tenantMapper;


    @Override
    public Tenant getByBizId(String bizId) {
        return tenantMapper.selectByBizId(bizId);
    }

    @Override
    public Integer create(Tenant tenant) {
        tenant.initCreated();
        return tenantMapper.insert(tenant);
    }

    @Override
    public Boolean update(Tenant tenant) {
        return tenantMapper.updateByBizId(tenant) > 0;
    }

    @Override
    public Boolean deleteByBizId(String bizId) {
        return tenantMapper.deleteByBizId(bizId) > 0;
    }

    @Override
    public Page<Tenant> page(PageParam pageParam, Tenant tenant) {
        return tenantMapper.selectPage(pageParam, tenant);
    }
}
