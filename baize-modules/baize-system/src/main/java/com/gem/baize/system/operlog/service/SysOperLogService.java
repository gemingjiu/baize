package com.gem.baize.system.operlog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.operlog.domain.dto.SysOperLogDto;
import com.gem.baize.system.operlog.entity.SysOperLog;

/**
 * 操作日志服务接口
 */
public interface SysOperLogService extends IService<SysOperLog> {

    /**
     * 分页查询操作日志
     */
    Page<SysOperLogDto> page(Page<SysOperLog> page, SysOperLogDto sysOperLogDto);

    /**
     * 获取操作日志详情
     */
    SysOperLogDto getById(String id);

    /**
     * 新增操作日志
     */
    void insertOperlog(SysOperLogDto operLog);

    /**
     * 删除操作日志
     */
    void removeById(String id);

    /**
     * 清空操作日志
     */
    void cleanOperLog();
}
