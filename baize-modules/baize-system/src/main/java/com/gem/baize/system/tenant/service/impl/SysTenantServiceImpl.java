package com.gem.baize.system.tenant.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.tenant.entity.SysTenant;
import com.gem.baize.system.tenant.mapper.SysTenantMapper;
import com.gem.baize.system.tenant.service.SysTenantService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private SysTenantMapper sysTenantMapper;

    @Autowired
    private SysTenantConvert sysTenantConvert;


    @Override
    public SysTenantDto getById(String id) {
        SysTenant sysTenant = Optional.ofNullable(sysTenantMapper.selectById(id)).orElseThrow(() -> new NotFoundException("租户不存在"));
        return sysTenantConvert.toDto(sysTenant);
    }

    @Transactional
    @Override
    public Integer create(SysTenantDto sysTenantDto) {
        SysTenant sysTenant = sysTenantConvert.toEntity(sysTenantDto);
        boolean exists = sysTenantMapper.exists(Wrappers.<SysTenant>lambdaQuery()
                .eq(SysTenant::getTenantCode, sysTenantDto.getTenantCode()));
        if (exists) {
            throw new DuplicateException("租户编码已存在，请更换后重试");
        }

        int result = sysTenantMapper.insert(sysTenant);
        if (result <= 0) {
            throw new DataCreationException("租户创建失败");
        }
        return result;
    }

    @Transactional
    @Override
    public void update(SysTenantDto sysTenantDto) {
        SysTenant sysTenant = sysTenantConvert.toEntity(sysTenantDto);

        boolean exists = sysTenantMapper.exists(Wrappers.<SysTenant>lambdaQuery()
                .eq(SysTenant::getTenantCode, sysTenantDto.getTenantCode()));
        if (!exists) {
            throw new DuplicateException("租户编码不存在，请更换后重试");
        }

        int affectedRows = sysTenantMapper.updateById(sysTenant);
        if (affectedRows <= 0) {
            throw new NotFoundException("租户信息更新失败，记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("租户信息更新异常，影响了多条记录");
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

        Page<SysTenant> sysTenantPage = Optional.ofNullable(sysTenantMapper.selectPage(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到租户信息"));
        return sysTenantConvert.toDtoPage(sysTenantPage);
    }
}
