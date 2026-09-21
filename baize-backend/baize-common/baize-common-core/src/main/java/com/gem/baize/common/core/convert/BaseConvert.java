package com.gem.baize.common.core.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用转换器模板（泛型）
 * @param <E> Entity类型
 * @param <D> DTO类型
 */
public abstract class BaseConvert<E, D> {
    /**
     * 单个Entity转DTO（必须由子类实现）
     */
    public abstract D toDto(E entity);

    /**
     * 单个DTO转Entity（必须由子类实现）
     */
    public abstract E toEntity(D dto);

    /**
     * 列表转换：Entity列表 -> DTO列表
     */
    public List<D> toDtoList(List<E> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 列表转换：DTO列表 -> Entity列表
     */
    public List<E> toEntityList(List<D> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 分页转换：Entity分页 -> DTO分页
     */
    public Page<D> toDtoPage(Page<E> entityPage) {
        if (entityPage == null) {
            return null;
        }

        Page<D> dtoPage = new Page<>();
        dtoPage.setCurrent(entityPage.getCurrent());
        dtoPage.setSize(entityPage.getSize());
        dtoPage.setTotal(entityPage.getTotal());
        dtoPage.setPages(entityPage.getPages());
        dtoPage.setRecords(toDtoList(entityPage.getRecords()));
        return dtoPage;
    }

    /**
     * 更新Entity（忽略null值）- 默认使用BeanUtils
     */
    public void updateEntityFromDto(D dto, E entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity, getNullPropertyNames(dto));
    }

    /**
     * 获取为null的属性名（子类可重写）
     */
    protected String[] getNullPropertyNames(D dto) {
        // 基础实现：返回空数组（即拷贝所有属性）
        // 子类可以重写此方法以忽略特定字段
        return new String[]{};
    }
}
