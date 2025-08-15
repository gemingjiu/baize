package com.gem.baize.admin.tenant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.admin.tenant.entity.Tenant;
import com.gem.baize.admin.tenant.mapper.TenantMapper;
import com.gem.baize.admin.tenant.service.TenantService;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 租户服务类实现
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public Tenant getByBizId(String bizId) {
        return Optional.ofNullable(tenantMapper.selectByBizId(bizId)).orElseThrow(() -> new NotFoundException("租户不存在"));
    }

    @Override
    public Integer create(Tenant tenant) {
        int result = tenantMapper.insert(tenant);
        if (result <= 0) {
            throw new DataCreationException("租户创建失败");
        }
        return result;
    }

    @Override
    public void updateByBizId(Tenant tenant) {
        Long innerId = getByBizId(tenant.getBizId()).getId();
        tenant.setId(innerId);
        int affectedRows = tenantMapper.updateById(tenant);
        if (affectedRows <= 0) {
            throw new NotFoundException("租户信息更新失败，记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("租户信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteByBizId(String bizId) {
        Long innerId = getByBizId(bizId).getId();
        int affectedRows = tenantMapper.deleteById(innerId);
        if (affectedRows <= 0) {
            throw new NotFoundException("租户信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("租户信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<Tenant> page(PageParam pageParam, Tenant tenant) {
        return Optional.ofNullable(tenantMapper.selectPage(pageParam, tenant))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到租户信息"));
    }
}
