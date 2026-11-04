package com.gem.baize.system.dict.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.dict.domain.dto.SysDictDataDto;
import com.gem.baize.system.dict.entity.SysDictData;

import java.util.List;
import java.util.Map;

public interface SysDictDataService extends IService<SysDictData> {

    SysDictDataDto getById(String id);

    void create(SysDictDataDto dto);

    void updateById(SysDictDataDto dto);

    void removeById(String id);

    Page<SysDictDataDto> page(Page<SysDictData> page, SysDictDataDto dto);

    /**
     * 按字典类型查询当前租户可见的启用字典数据。
     * <p>
     * 合并解析（2026-11-05 字典模型）：当前租户有该类型的自定义项时整类以其为准，
     * 否则回落到平台内置项（tenant_id=平台 且 is_builtin='1'）。
     */
    List<SysDictDataDto> listByDictType(String dictType);

    /**
     * 批量获取当前租户可见的全部启用字典（key=dict_type，value=合并解析后的数据列表）。
     * 供前端登录后一次性拉取并全局缓存（决策 D3），避免逐类型请求。
     */
    Map<String, List<SysDictDataDto>> listAll();
}
