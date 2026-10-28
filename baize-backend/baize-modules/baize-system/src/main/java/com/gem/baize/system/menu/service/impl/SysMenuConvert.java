package com.gem.baize.system.menu.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.system.menu.entity.SysMenu;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysMenu ↔ SysMenuDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysMenuConvert {

    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);

    SysMenuDto toDto(SysMenu entity);

    SysMenu toEntity(SysMenuDto dto);

    List<SysMenuDto> toDtoList(List<SysMenu> list);

    default Page<SysMenuDto> toDtoPage(Page<SysMenu> page) {
        Page<SysMenuDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
