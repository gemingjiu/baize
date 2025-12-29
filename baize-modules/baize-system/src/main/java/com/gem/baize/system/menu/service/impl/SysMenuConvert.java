package com.gem.baize.system.menu.service.impl;

import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.menu.entity.SysMenu;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysMenuConvert extends BaseConvert<SysMenu, SysMenuDto> {
    @Override
    public SysMenuDto toDto(SysMenu entity) {
        if (entity == null) {
            return null;
        }
        SysMenuDto dto = new SysMenuDto();
        BeanUtils.copyProperties(entity, dto);
        // 特殊字段处理
        return dto;
    }

    @Override
    public SysMenu toEntity(SysMenuDto dto) {
        if (dto == null) {
            return null;
        }
        SysMenu entity = new SysMenu();
        BeanUtils.copyProperties(dto, entity);
        // 初始化Entity特殊字段
        return entity;
    }
}
