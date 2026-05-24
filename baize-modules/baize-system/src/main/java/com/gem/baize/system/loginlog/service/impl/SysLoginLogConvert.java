package com.gem.baize.system.loginlog.service.impl;

import com.gem.baize.api.system.loginlog.domain.dto.SysLoginLogDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.loginlog.entity.SysLoginLog;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysLoginLogConvert extends BaseConvert<SysLoginLog, SysLoginLogDto> {
    @Override
    public SysLoginLogDto toDto(SysLoginLog entity) {
        if (entity == null) {
            return null;
        }
        SysLoginLogDto dto = new SysLoginLogDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SysLoginLog toEntity(SysLoginLogDto dto) {
        if (dto == null) {
            return null;
        }
        SysLoginLog entity = new SysLoginLog();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
