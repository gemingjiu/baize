package com.gem.baize.api.admin.perm.domain.dto;

import lombok.Data;

@Data
public class PermDTO {
    private String bizId;

    private String tenantId;

    private String parentId;

    private String permCode;

    private String permName;
}
