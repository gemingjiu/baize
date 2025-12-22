package com.gem.baize.system.tenant.service;

import com.gem.baize.system.tenant.entity.SysTenant;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.common.core.convert.BaseConvert;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysTenantConvert extends BaseConvert<SysTenant, SysTenantDto> {
    @Override
    public SysTenantDto toDto(SysTenant entity) {
        if (entity == null) {
            return null;
        }
        SysTenantDto dto = new SysTenantDto();
        BeanUtils.copyProperties(entity, dto);
        // 特殊字段处理
        // dto.setStatusName(convertStatus(entity.getStatus()));
        return dto;
    }

    @Override
    public SysTenant toEntity(SysTenantDto dto) {
        if (dto == null) {
            return null;
        }
        SysTenant entity = new SysTenant();
        BeanUtils.copyProperties(dto, entity);
        // 初始化Entity特殊字段
        // entity.setCreateTime(LocalDateTime.now());
        return entity;
    }
}
