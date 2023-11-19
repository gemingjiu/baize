package com.baize.system.api.domain;

import com.baize.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 字典类型表 sys_dict_type
 * 

 */
@Data
public class SysDictType extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    /** 字典名称 */
    private String dictName;

    /** 字典类型 */
    private String dictType;
}
