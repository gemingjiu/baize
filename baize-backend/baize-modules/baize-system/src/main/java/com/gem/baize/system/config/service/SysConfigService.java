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
}
