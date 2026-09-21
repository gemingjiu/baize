package com.gem.baize.api.system.dict.domain.dto;

import lombok.Data;

@Data
public class SysDictTypeDto {
    private String id;
    private String tenantId;
    private String dictName;
    private String dictType;
    private int sort;
    private String status;
    private String remark;
}
