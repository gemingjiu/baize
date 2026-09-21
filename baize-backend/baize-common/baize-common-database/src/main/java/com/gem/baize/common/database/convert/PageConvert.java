package com.gem.baize.common.database.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.common.core.convert.BaseConvert;
import com.gem.baize.common.core.model.vo.PageResult;

public class PageConvert<T> extends BaseConvert<Page<T>, PageResult<T>> {

    @Override
    public PageResult<T> toDto(Page<T> entity) {
        if (entity == null) {
            return new PageResult<>();
        }

        PageResult<T> result = new PageResult<>();
        result.setTotal(entity.getTotal());
        result.setSize(entity.getSize());
        result.setCurrent(entity.getCurrent());
        result.setRecords(entity.getRecords());
        result.setCountId(entity.countId());
        return result;
    }

    @Override
    public Page<T> toEntity(PageResult<T> dto) {
        return null;
    }
}
