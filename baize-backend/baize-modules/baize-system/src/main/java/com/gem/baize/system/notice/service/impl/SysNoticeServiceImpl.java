package com.gem.baize.system.notice.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

import java.util.List;
import java.util.Optional;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {


    @Override
    public SysNoticeDto getById(String id) {
        SysNotice sysNotice = Optional.ofNullable(super.getById(id)).orElseThrow(() -> new NotFoundException("公告不存在"));
        return SysNoticeConvert.INSTANCE.toDto(sysNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysNoticeDto dto) {
        SysNotice sysNotice = SysNoticeConvert.INSTANCE.toEntity(dto);
        super.save(sysNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysNoticeDto dto) {
        SysNotice sysNotice = SysNoticeConvert.INSTANCE.toEntity(dto);
        boolean success = super.updateById(sysNotice);
        if (!success) {
            throw new NotFoundException("公告不存在或已删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(String id) {
        // 逻辑删除：只把 deleted 置 '1'（status 同步置 '1'），保留行供审计/外键引用。
        // 不调用 super.removeById：MP 的全局 logic-delete 配置对由 DTO 转换的实体无效，会退化成物理 DELETE。
        LambdaUpdateWrapper<SysNotice> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysNotice::getId, id);
        wrapper.eq(SysNotice::getDeleted, "0");
        wrapper.set(SysNotice::getDeleted, "1");
        wrapper.set(SysNotice::getStatus, "1");
        boolean success = this.update(wrapper);
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
        return SysNoticeConvert.INSTANCE.toDtoPage(sysNoticePage);
    }

    @Override
    public List<SysNoticeDto> listLatest(int limit) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        // 只取启用中的公告；deleted='0' 由全局逻辑删除过滤自动附加
        wrapper.eq(SysNotice::getStatus, "0");
        wrapper.orderByDesc(SysNotice::getCreatedTime);
        wrapper.last("LIMIT " + Math.max(1, limit));
        return SysNoticeConvert.INSTANCE.toDtoList(super.list(wrapper));
    }
}
