package com.gem.baize.system.tenant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.system.tenant.entity.SysTenant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysTenant ↔ SysTenantDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysTenantConvert {

    SysTenantConvert INSTANCE = Mappers.getMapper(SysTenantConvert.class);

    SysTenantDto toDto(SysTenant entity);

    SysTenant toEntity(SysTenantDto dto);

    List<SysTenantDto> toDtoList(List<SysTenant> list);

    default Page<SysTenantDto> toDtoPage(Page<SysTenant> page) {
        Page<SysTenantDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
