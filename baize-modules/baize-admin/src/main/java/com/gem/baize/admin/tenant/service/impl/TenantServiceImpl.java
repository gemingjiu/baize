package com.gem.baize.admin.tenant.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.admin.tenant.mapper.TenantMapper;
import com.gem.baize.api.admin.tenant.entity.Tenant;
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
    public Tenant getById(String id) {
        int tenantId = Integer.parseInt(id);
        return tenantMapper.selectById(tenantId);
    }

    @Override
    public Integer create(Tenant tenant) {
        tenant.initCreated();
        return tenantMapper.insert(tenant);
    }

    @Override
    public void update(Tenant tenant) {
        tenantMapper.updateById(tenant);

    }

    @Override
    public void delete(String id) {
        int tenantId = Integer.parseInt(id);
        tenantMapper.deleteById(tenantId);
    }

    @Override
    public Page<Tenant> page(PageParam pageParam, Tenant tenant) {

        Page<Tenant> page = new Page<>(pageParam.getCurrent(), pageParam.getSize());

        LambdaQueryWrapper<Tenant> queryWrapper = new LambdaQueryWrapper<>();

        // 模糊查询租户名称
        if (StringUtils.isNotBlank(tenant.getTenantName())) {
            queryWrapper.like(Tenant::getTenantName, tenant.getTenantName());
        }
        // 模糊查询租户编码
        if (StringUtils.isNotBlank(tenant.getTenantCode())) {
            queryWrapper.like(Tenant::getTenantCode, tenant.getTenantCode());
        }
        // 查询联系人
        if (StringUtils.isNotBlank(tenant.getContactPerson())) {
            queryWrapper.like(Tenant::getContactPerson, tenant.getContactPerson());
        }

        // 查询联系号码
        if (StringUtils.isNotBlank(tenant.getContactPhone())) {
            queryWrapper.like(Tenant::getContactPhone, tenant.getContactPhone());
        }

        return tenantMapper.selectPage(page, queryWrapper);
    }
}
