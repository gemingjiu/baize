package com.gem.baize.system.perm.service.impl;

import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.api.system.perm.domain.dto.SysPermDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.system.perm.entity.SysPerm;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysPermConvert extends BaseConvert<SysPerm, SysPermDto> {
    @Override
    public SysPermDto toDto(SysPerm entity) {
        if (entity == null) {
            return null;
        }
        SysPermDto dto = new SysPermDto();
        BeanUtils.copyProperties(entity, dto);
        // 特殊字段处理
        return dto;
    }

    @Override
    public SysPerm toEntity(SysPermDto dto) {
        if (dto == null) {
            return null;
        }
        SysPerm entity = new SysPerm();
        BeanUtils.copyProperties(dto, entity);
        // 初始化Entity特殊字段
        return entity;
    }
}
