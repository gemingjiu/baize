package com.gem.baize.system.role.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.system.role.entity.SysRole;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * SysRole ↔ SysRoleDto 转换（MapStruct 编译期生成，替代 BeanUtils 反射拷贝：
 * 新增实体字段漏加 DTO 时编译期告警/显式映射，不再静默丢字段）。
 */
@Mapper
public interface SysRoleConvert {

    SysRoleConvert INSTANCE = Mappers.getMapper(SysRoleConvert.class);

    SysRoleDto toDto(SysRole entity);

    SysRole toEntity(SysRoleDto dto);

    List<SysRoleDto> toDtoList(List<SysRole> list);

    default Page<SysRoleDto> toDtoPage(Page<SysRole> page) {
        Page<SysRoleDto> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(toDtoList(page.getRecords()));
        return result;
    }
}
