package com.gem.baize.api.system.config.domain.dto;

import lombok.Data;

@Data
public class SysConfigDto {
    private String id;
    private String tenantId;
    private String configName;
    private String configKey;
    private String configValue;
    private String configType;
    private int sort;
    private String status;
    private String remark;
}
