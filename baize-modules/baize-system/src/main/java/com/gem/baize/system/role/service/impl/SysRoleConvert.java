package com.gem.baize.system.role.service.impl;

import com.gem.baize.api.system.role.domain.dto.SysRoleDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.role.entity.SysRole;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysRoleConvert extends BaseConvert<SysRole, SysRoleDto> {
    @Override
    public SysRoleDto toDto(SysRole entity) {
        if (entity == null) {
            return null;
        }
        SysRoleDto dto = new SysRoleDto();
        BeanUtils.copyProperties(entity, dto);
        // 特殊字段处理
        return dto;
    }

    @Override
    public SysRole toEntity(SysRoleDto dto) {
        if (dto == null) {
            return null;
        }
        SysRole entity = new SysRole();
        BeanUtils.copyProperties(dto, entity);
        // 初始化Entity特殊字段
        return entity;
    }
}
