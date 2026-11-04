package com.gem.baize.api.system.dict.domain.dto;

import lombok.Data;

@Data
public class SysDictDataDto {
    private String id;
    private String tenantId;
    private String dictType;
    private String dictLabel;
    private String dictValue;
    /** 是否内置字典项;0:租户自定义,1:平台内置（平台维护，租户不可删） */
    private String isBuiltin;
    private String defaulted;
    private int sort;
    private String status;
    private String remark;
}
