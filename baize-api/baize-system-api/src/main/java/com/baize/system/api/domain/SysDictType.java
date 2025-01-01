package com.baize.system.api.domain;

import com.baize.common.core.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 字典类型表 sys_dict_type
 * 
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictType extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    /** 字典ID */
    private String id;
    /** 字典名称 */
    private String dictName;

    /** 字典类型 */
    private String dictType;
}
