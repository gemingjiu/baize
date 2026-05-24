package com.gem.baize.system.dict.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.dict.domain.dto.SysDictTypeDto;
import com.gem.baize.system.dict.entity.SysDictType;

public interface SysDictTypeService extends IService<SysDictType> {

    SysDictTypeDto getById(String id);

    void create(SysDictTypeDto dto);

    void updateById(SysDictTypeDto dto);

    void removeById(String id);

    Page<SysDictTypeDto> page(Page<SysDictType> page, SysDictTypeDto dto);
}
