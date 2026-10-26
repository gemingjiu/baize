package com.gem.baize.system.job.service.impl;

import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.system.job.domain.dto.SysJobDto;
import com.gem.baize.system.job.entity.SysJob;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 定时任务对象转换
 */
@Component
public class SysJobConvert extends BaseConvert<SysJob, SysJobDto> {
    @Override
    public SysJobDto toDto(SysJob entity) {
        if (entity == null) {
            return null;
        }
        SysJobDto dto = new SysJobDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SysJob toEntity(SysJobDto dto) {
        if (dto == null) {
            return null;
        }
        SysJob entity = new SysJob();
        BeanUtils.copyProperties(dto, entity);
        // 租户、删除标记、乐观锁与审计字段一律由框架/拦截器填充，禁止来自请求体
        entity.setTenantId(null);
        entity.setDeleted(null);
        entity.setVersion(null);
        entity.setCreatedBy(null);
        entity.setCreatedTime(null);
        entity.setModifiedBy(null);
        entity.setModifiedTime(null);
        return entity;
    }
}
