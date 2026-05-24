package com.gem.baize.system.notice.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.notice.domain.dto.SysNoticeDto;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.notice.entity.SysNotice;
import com.gem.baize.system.notice.mapper.SysNoticeMapper;
import com.gem.baize.system.notice.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Autowired
    private SysNoticeConvert sysNoticeConvert;

    @Override
    public SysNoticeDto getById(String id) {
        SysNotice sysNotice = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("公告不存在"));
        return sysNoticeConvert.toDto(sysNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysNoticeDto dto) {
        SysNotice sysNotice = sysNoticeConvert.toEntity(dto);
        super.save(sysNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysNoticeDto dto) {
        SysNotice sysNotice = sysNoticeConvert.toEntity(dto);
        boolean success = super.updateById(sysNotice);
        if (!success) {
            throw new NotFoundException("公告不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("公告不存在或已删除");
        }
    }

    @Override
    public Page<SysNoticeDto> page(Page<SysNotice> page, SysNoticeDto dto) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysNotice::getCreatedTime);

        if (StringUtils.isNotBlank(dto.getNoticeTitle())) {
            wrapper.like(SysNotice::getNoticeTitle, dto.getNoticeTitle());
        }
        if (StringUtils.isNotBlank(dto.getNoticeType())) {
            wrapper.eq(SysNotice::getNoticeType, dto.getNoticeType());
        }
        if (StringUtils.isNotBlank(dto.getTenantId())) {
            wrapper.eq(SysNotice::getTenantId, dto.getTenantId());
        }

        Page<SysNotice> sysNoticePage = super.page(page, wrapper);
        return sysNoticeConvert.toDtoPage(sysNoticePage);
    }
}
