package com.gem.baize.admin.tenant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.admin.tenant.entity.Tenant;
import com.gem.baize.admin.tenant.mapper.TenantMapper;
import com.gem.baize.admin.tenant.service.TenantService;
import com.gem.baize.common.core.model.dto.PageParam;
import org.apache.commons.lang3.ObjectUtils;
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
        Tenant tenant = tenantMapper.selectByBizId(bizId);
        if (ObjectUtils.isEmpty(tenant)) {
            throw new RuntimeException("记录不存在");
        }
        return tenant;
    }

    @Override
    public Integer create(Tenant tenant) {
        return tenantMapper.insert(tenant);
    }

    @Override
    public void update(Tenant tenant) {
        tenantMapper.updateByBizId(tenant);
    }

    @Override
    public void deleteByBizId(String bizId) {
        tenantMapper.deleteByBizId(bizId);
    }

    @Override
    public Page<Tenant> page(PageParam pageParam, Tenant tenant) {
        return tenantMapper.selectPage(pageParam, tenant);
    }
}
