package com.gem.baize.api.admin.role.domain.dto;

import lombok.Data;

@Data
public class RoleDTO {
    private String bizId;

    private String tenantId;

    private String roleName;

    private String roleCode;

    private String dataScope;

    private String menuCheckStrictly;

    private String deptCheckStrictly;
}
