package com.gem.baize.system.config.service.impl;

import com.gem.baize.api.system.config.domain.dto.SysConfigDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.config.entity.SysConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysConfigConvert extends BaseConvert<SysConfig, SysConfigDto> {
    @Override
    public SysConfigDto toDto(SysConfig entity) {
        if (entity == null) {
            return null;
        }
        SysConfigDto dto = new SysConfigDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SysConfig toEntity(SysConfigDto dto) {
        if (dto == null) {
            return null;
        }
        SysConfig entity = new SysConfig();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
