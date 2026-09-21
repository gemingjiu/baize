package com.gem.baize.api.system.dict.domain.dto;

import lombok.Data;

@Data
public class SysDictDataDto {
    private String id;
    private String tenantId;
    private String dictType;
    private String dictLabel;
    private String dictValue;
    private String cssClass;
    private String listClass;
    private String defaulted;
    private int sort;
    private String status;
    private String remark;
}
