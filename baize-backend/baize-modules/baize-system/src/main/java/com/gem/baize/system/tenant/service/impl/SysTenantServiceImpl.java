package com.gem.baize.system.tenant.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

import java.util.List;
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
    @Transactional(rollbackFor = Exception.class)
    public void create(SysTenantDto sysTenantDto) {
        SysTenant sysTenant = sysTenantConvert.toEntity(sysTenantDto);

        try {
            super.save(sysTenant);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("租户编码不存在，请更换后重试");
        }


    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_tenant", key = "#sysTenantDto.id")
    public void updateById(SysTenantDto sysTenantDto) {
        SysTenant sysTenant = sysTenantConvert.toEntity(sysTenantDto);


        boolean success = super.updateById(sysTenant);
        if (!success) {
            throw new NotFoundException("租户不存在或已删除");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "sys_tenant", key = "#id")
    public void removeById(String id) {
        // 逻辑删除：只把 deleted 置 '1'（status 同步置 '1'），保留行供审计/外键引用。
        // 不调用 super.removeById：MP 的全局 logic-delete 配置对由 DTO 转换的实体无效，会退化成物理 DELETE。
        LambdaUpdateWrapper<SysTenant> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysTenant::getId, id);
        wrapper.eq(SysTenant::getDeleted, "0");
        wrapper.set(SysTenant::getDeleted, "1");
        wrapper.set(SysTenant::getStatus, "1");
        boolean success = this.update(wrapper);
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

        Page<SysTenant> sysTenantPage = super.page(page, wrapper);
        return sysTenantConvert.toDtoPage(sysTenantPage);
    }

    @Override
    public List<SysTenantDto> listAvailable() {
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        // 只暴露正常租户：删除的、停用的都不出现在登录页下拉框
        wrapper.eq(SysTenant::getDeleted, "0");
        wrapper.eq(SysTenant::getStatus, "0");
        wrapper.orderByAsc(SysTenant::getSort);
        wrapper.orderByAsc(SysTenant::getId);
        return sysTenantConvert.toDtoList(super.list(wrapper));
    }

    @Override
    public boolean existsById(String id) {
        // 租户ID必须是数字：它会进入 SQL 条件，同时也会被写入令牌的 X-Tenant-Id
        if (StringUtils.isBlank(id) || !id.matches("\\d+")) {
            return false;
        }
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTenant::getId, id);
        wrapper.eq(SysTenant::getDeleted, "0");
        wrapper.eq(SysTenant::getStatus, "0");
        return super.count(wrapper) > 0;
    }

    @Override
    public SysTenantDto getByTenantCodeOrDomain(String tenant) {
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(SysTenant::getTenantCode, tenant).or().eq(SysTenant::getDomain, tenant));
        SysTenant sysTenant = super.getOne(wrapper);
        if (sysTenant == null) {
            throw new NotFoundException("租户不存在");
        }
        return sysTenantConvert.toDto(sysTenant);
    }
}
