package com.gem.baize.api.system.dict.domain.dto;

import lombok.Data;

@Data
public class SysDictTypeDto {
    private String id;
    private String tenantId;
    private String dictName;
    private String dictType;
    /** 是否内置字典类型;0:租户自定义,1:平台内置（内置不可删除） */
    private String isBuiltin;
    private int sort;
    private String status;
    private String remark;
}
