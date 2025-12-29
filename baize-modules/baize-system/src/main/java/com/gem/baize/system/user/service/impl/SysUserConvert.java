package com.gem.baize.system.user.service.impl;

import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.user.entity.SysUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysUserConvert extends BaseConvert<SysUser, SysUserDto> {
    @Override
    public SysUserDto toDto(SysUser entity) {
        if (entity == null) {
            return null;
        }
        SysUserDto dto = new SysUserDto();
        BeanUtils.copyProperties(entity, dto);
        // 特殊字段处理
        return dto;
    }

    @Override
    public SysUser toEntity(SysUserDto dto) {
        if (dto == null) {
            return null;
        }
        SysUser entity = new SysUser();
        BeanUtils.copyProperties(dto, entity);
        // 初始化Entity特殊字段
        return entity;
    }
}
