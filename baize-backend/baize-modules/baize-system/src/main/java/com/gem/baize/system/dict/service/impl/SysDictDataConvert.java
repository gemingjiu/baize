package com.gem.baize.system.dict.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.dict.domain.dto.SysDictDataDto;
import com.gem.baize.system.dict.entity.SysDictData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysDictData ↔ SysDictDataDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysDictDataConvert {

    SysDictDataConvert INSTANCE = Mappers.getMapper(SysDictDataConvert.class);

    SysDictDataDto toDto(SysDictData entity);

    SysDictData toEntity(SysDictDataDto dto);

    List<SysDictDataDto> toDtoList(List<SysDictData> list);

    default Page<SysDictDataDto> toDtoPage(Page<SysDictData> page) {
        Page<SysDictDataDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
