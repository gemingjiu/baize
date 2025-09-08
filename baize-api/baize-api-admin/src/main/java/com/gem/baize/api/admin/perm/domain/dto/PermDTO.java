package com.gem.baize.api.admin.perm.domain.dto;

import lombok.Data;

@Data
public class PermDTO {
    /**
     * 业务ID
     */
    private String id;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 父级ID
     */
    private String parentId;
    /**
     * 权限编码
     */
    private String permCode;
    /**
     * 权限名称
     */
    private String permName;
}
