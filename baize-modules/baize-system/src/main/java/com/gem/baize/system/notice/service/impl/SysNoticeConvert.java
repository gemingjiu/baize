package com.gem.baize.system.notice.service.impl;

import com.gem.baize.api.system.notice.domain.dto.SysNoticeDto;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.notice.entity.SysNotice;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SysNoticeConvert extends BaseConvert<SysNotice, SysNoticeDto> {
    @Override
    public SysNoticeDto toDto(SysNotice entity) {
        if (entity == null) {
            return null;
        }
        SysNoticeDto dto = new SysNoticeDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SysNotice toEntity(SysNoticeDto dto) {
        if (dto == null) {
            return null;
        }
        SysNotice entity = new SysNotice();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
