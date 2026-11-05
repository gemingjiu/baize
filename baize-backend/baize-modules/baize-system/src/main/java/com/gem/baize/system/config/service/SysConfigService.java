package com.gem.baize.system.config.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.config.domain.dto.SysConfigDto;
import com.gem.baize.system.config.entity.SysConfig;

public interface SysConfigService extends IService<SysConfig> {

    SysConfigDto getById(String id);

    void create(SysConfigDto dto);

    void updateById(SysConfigDto dto);

    void removeById(String id);

    Page<SysConfigDto> page(Page<SysConfig> page, SysConfigDto dto);

    SysConfigDto getByConfigKey(String configKey);

    /**
     * 按参数键读取参数值（业务消费口径，2026-11-05 P2）。
     * <p>
     * 合并解析：当前租户自定义值优先，缺省回落平台租户兜底值；只读启用中的参数。
     * 返回裸值（非 DTO），查不到返回 null（消费方自行决定默认行为）。
     */
    String getConfigValueByKey(String configKey);
}
