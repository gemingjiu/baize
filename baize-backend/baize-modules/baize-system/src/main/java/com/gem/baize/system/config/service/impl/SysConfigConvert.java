package com.gem.baize.system.config.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.config.domain.dto.SysConfigDto;
import com.gem.baize.system.config.entity.SysConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysConfig ↔ SysConfigDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysConfigConvert {

    SysConfigConvert INSTANCE = Mappers.getMapper(SysConfigConvert.class);

    SysConfigDto toDto(SysConfig entity);

    SysConfig toEntity(SysConfigDto dto);

    List<SysConfigDto> toDtoList(List<SysConfig> list);

    default Page<SysConfigDto> toDtoPage(Page<SysConfig> page) {
        Page<SysConfigDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
