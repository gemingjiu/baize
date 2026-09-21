package com.gem.baize.system.notice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.notice.domain.dto.SysNoticeDto;
import com.gem.baize.system.notice.entity.SysNotice;

public interface SysNoticeService extends IService<SysNotice> {

    SysNoticeDto getById(String id);

    void create(SysNoticeDto dto);

    void updateById(SysNoticeDto dto);

    void removeById(String id);

    Page<SysNoticeDto> page(Page<SysNotice> page, SysNoticeDto dto);
}
