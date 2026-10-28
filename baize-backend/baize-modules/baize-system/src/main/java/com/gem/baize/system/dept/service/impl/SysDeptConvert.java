package com.gem.baize.system.dept.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.system.dept.entity.SysDept;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysDept ↔ SysDeptDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysDeptConvert {

    SysDeptConvert INSTANCE = Mappers.getMapper(SysDeptConvert.class);

    SysDeptDto toDto(SysDept entity);

    SysDept toEntity(SysDeptDto dto);

    List<SysDeptDto> toDtoList(List<SysDept> list);

    default Page<SysDeptDto> toDtoPage(Page<SysDept> page) {
        Page<SysDeptDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
