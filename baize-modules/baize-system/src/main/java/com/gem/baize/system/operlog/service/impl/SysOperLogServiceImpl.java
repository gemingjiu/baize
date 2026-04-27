package com.gem.baize.system.operlog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.operlog.domain.dto.SysOperLogDto;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.operlog.entity.SysOperLog;
import com.gem.baize.system.operlog.service.impl.SysOperLogConvert;
import com.gem.baize.system.operlog.mapper.SysOperLogMapper;
import com.gem.baize.system.operlog.service.SysOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    @Override
    public Page<SysOperLogDto> page(Page<SysOperLog> page, SysOperLogDto sysOperLogDto) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();

        wrapper.orderByDesc(SysOperLog::getOperTime);

        if (StringUtils.isNotBlank(sysOperLogDto.getTitle())) {
            wrapper.like(SysOperLog::getTitle, sysOperLogDto.getTitle());
        }
        if (sysOperLogDto.getBusinessType() != null) {
            wrapper.eq(SysOperLog::getBusinessType, sysOperLogDto.getBusinessType());
        }
        if (StringUtils.isNotBlank(sysOperLogDto.getUserName())) {
            wrapper.like(SysOperLog::getUserName, sysOperLogDto.getUserName());
        }
        if (sysOperLogDto.getStatus() != null) {
            wrapper.eq(SysOperLog::getOperStatus, sysOperLogDto.getStatus());
        }
        if (StringUtils.isNotBlank(sysOperLogDto.getTenantId())) {
            wrapper.eq(SysOperLog::getTenantId, sysOperLogDto.getTenantId());
        }

        Page<SysOperLog> resultPage = Optional.ofNullable(super.page(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到操作日志信息"));

        return SysOperLogConvert.INSTANCE.toDtoPage(resultPage);
    }

    @Override
    public SysOperLogDto getById(String id) {
        SysOperLog sysOperLog = Optional.ofNullable(super.getById(id))
                .orElseThrow(() -> new NotFoundException("操作日志不存在"));
        return SysOperLogConvert.INSTANCE.toDto(sysOperLog);
    }

    @Override
    public void insertOperlog(SysOperLogDto operLog) {
        SysOperLog entity = SysOperLogConvert.INSTANCE.toEntity(operLog);
        super.save(entity);
    }

    @Override
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("操作日志不存在或已删除");
        }
    }

    @Override
    public void cleanOperLog() {
        super.remove(new LambdaQueryWrapper<>());
    }
}
