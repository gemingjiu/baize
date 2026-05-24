package com.gem.baize.system.dict.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.dict.domain.dto.SysDictDataDto;
import com.gem.baize.system.dict.entity.SysDictData;

import java.util.List;

public interface SysDictDataService extends IService<SysDictData> {

    SysDictDataDto getById(String id);

    void create(SysDictDataDto dto);

    void updateById(SysDictDataDto dto);

    void removeById(String id);

    Page<SysDictDataDto> page(Page<SysDictData> page, SysDictDataDto dto);

    List<SysDictDataDto> listByDictType(String dictType);
}
