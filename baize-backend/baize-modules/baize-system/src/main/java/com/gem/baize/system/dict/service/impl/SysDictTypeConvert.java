package com.gem.baize.system.dict.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.dict.domain.dto.SysDictTypeDto;
import com.gem.baize.system.dict.entity.SysDictType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysDictType ↔ SysDictTypeDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysDictTypeConvert {

    SysDictTypeConvert INSTANCE = Mappers.getMapper(SysDictTypeConvert.class);

    SysDictTypeDto toDto(SysDictType entity);

    SysDictType toEntity(SysDictTypeDto dto);

    List<SysDictTypeDto> toDtoList(List<SysDictType> list);

    default Page<SysDictTypeDto> toDtoPage(Page<SysDictType> page) {
        Page<SysDictTypeDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
