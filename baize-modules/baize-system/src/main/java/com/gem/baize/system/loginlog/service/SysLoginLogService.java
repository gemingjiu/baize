package com.gem.baize.system.loginlog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.loginlog.domain.dto.SysLoginLogDto;
import com.gem.baize.system.loginlog.entity.SysLoginLog;

public interface SysLoginLogService extends IService<SysLoginLog> {

    void recordLoginLog(SysLoginLogDto dto);

    Page<SysLoginLogDto> page(Page<SysLoginLog> page, SysLoginLogDto dto);
}
