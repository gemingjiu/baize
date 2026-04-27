package com.gem.baize.system.tenant.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.tenant.entity.SysTenant;
import com.gem.baize.system.tenant.mapper.SysTenantMapper;
import com.gem.baize.system.tenant.service.SysTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 租户服务类实现
 */
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements SysTenantService {

    @Autowired
    private SysTenantConvert sysTenantConvert;


    @Override
    @Cacheable(cacheNames = "sys_tenant", key = "#id")
    public SysTenantDto getById(String id) {
        SysTenant sysTenant = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("租户不存在"));
        return sysTenantConvert.toDto(sysTenant);
    }


    @Override
    public void create(SysTenantDto sysTenantDto) {
        SysTenant sysTenant = sysTenantConvert.toEntity(sysTenantDto);

        try {
            super.save(sysTenant);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("租户编码不存在，请更换后重试");
        }


    }


    @Override
    @CachePut(cacheNames = "sys_tenant", key = "#sysTenantDto.id")
    public void updateById(SysTenantDto sysTenantDto) {
        SysTenant sysTenant = sysTenantConvert.toEntity(sysTenantDto);


        boolean success = super.updateById(sysTenant);
        if (!success) {
            throw new NotFoundException("租户不存在或已删除");
        }

    }

    @Override
    @CacheEvict(cacheNames = "sys_tenant", key = "#id")
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("租户不存在或已删除");
        }
    }


    @Override
    public Page<SysTenantDto> page(Page<SysTenant> page, SysTenantDto sysTenantDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();

        // 默认排序
        wrapper.orderByAsc(SysTenant::getSort);

        // 动态条件查询
        if (StringUtils.isNotBlank(sysTenantDto.getTenantName())) {
            wrapper.like(SysTenant::getTenantName, sysTenantDto.getTenantName());
        }
        if (StringUtils.isNotBlank(sysTenantDto.getTenantCode())) {
            wrapper.like(SysTenant::getTenantCode, sysTenantDto.getTenantCode());
        }
        if (StringUtils.isNotBlank(sysTenantDto.getStatus())) {
            wrapper.eq(SysTenant::getStatus, sysTenantDto.getStatus());
        }

        Page<SysTenant> sysTenantPage = Optional.ofNullable(super.page(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到租户信息"));
        return sysTenantConvert.toDtoPage(sysTenantPage);
    }

    @Override
    public SysTenantDto getByTenantCodeOrDomain(String tenant) {
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(SysTenant::getTenantCode, tenant).or().eq(SysTenant::getDomain, tenant));
        wrapper.last("LIMIT 1");
        SysTenant sysTenant = super.getOne(wrapper);
        if (sysTenant == null) {
            throw new NotFoundException("租户不存在");
        }
        return sysTenantConvert.toDto(sysTenant);
    }
}
