package com.gem.baize.system.notice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.notice.domain.dto.SysNoticeDto;
import com.gem.baize.system.notice.entity.SysNotice;

import java.util.List;

public interface SysNoticeService extends IService<SysNotice> {

    SysNoticeDto getById(String id);

    void create(SysNoticeDto dto);

    void updateById(SysNoticeDto dto);

    void removeById(String id);

    Page<SysNoticeDto> page(Page<SysNotice> page, SysNoticeDto dto);

    /**
     * 最新启用的公告列表（登录即可见，2026-11-05 P3 公告展示闭环）。
     * <p>
     * 供首页公告栏与登录后弹窗消费：只返回启用（status='0'）未删除的公告，
     * 按 created_time 倒序取前 N 条；租户隔离由行级拦截器保证。
     */
    List<SysNoticeDto> listLatest(int limit);
}
