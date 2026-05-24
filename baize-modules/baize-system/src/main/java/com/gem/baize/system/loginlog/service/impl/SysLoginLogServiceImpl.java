package com.gem.baize.system.loginlog.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.loginlog.domain.dto.SysLoginLogDto;
import com.gem.baize.system.loginlog.entity.SysLoginLog;
import com.gem.baize.system.loginlog.mapper.SysLoginLogMapper;
import com.gem.baize.system.loginlog.service.SysLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    @Autowired
    private SysLoginLogConvert sysLoginLogConvert;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordLoginLog(SysLoginLogDto dto) {
        SysLoginLog entity = sysLoginLogConvert.toEntity(dto);
        super.save(entity);
    }

    @Override
    public Page<SysLoginLogDto> page(Page<SysLoginLog> page, SysLoginLogDto dto) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysLoginLog::getLoginTime);

        if (dto != null) {
            if (StringUtils.isNotBlank(dto.getUserName())) {
                wrapper.like(SysLoginLog::getUserName, dto.getUserName());
            }
            if (StringUtils.isNotBlank(dto.getStatus())) {
                wrapper.eq(SysLoginLog::getStatus, dto.getStatus());
            }
            if (StringUtils.isNotBlank(dto.getTenantId())) {
                wrapper.eq(SysLoginLog::getTenantId, dto.getTenantId());
            }
        }

        Page<SysLoginLog> resultPage = super.page(page, wrapper);
        return sysLoginLogConvert.toDtoPage(resultPage);
    }
}
