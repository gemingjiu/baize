package com.baize.system.api.domain;

import com.baize.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 字典数据表 sys_dict_data
 */
@Data
public class SysDictData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典键值
     */
    private String dictValue;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    private String cssClass;

    /**
     * 表格字典样式
     */
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    private String defaulted;
}
