package com.gem.baize.system.perm.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.perm.domain.dto.SysPermDto;
import com.gem.baize.system.perm.entity.SysPerm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysPerm ↔ SysPermDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysPermConvert {

    SysPermConvert INSTANCE = Mappers.getMapper(SysPermConvert.class);

    SysPermDto toDto(SysPerm entity);

    SysPerm toEntity(SysPermDto dto);

    List<SysPermDto> toDtoList(List<SysPerm> list);

    default Page<SysPermDto> toDtoPage(Page<SysPerm> page) {
        Page<SysPermDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
