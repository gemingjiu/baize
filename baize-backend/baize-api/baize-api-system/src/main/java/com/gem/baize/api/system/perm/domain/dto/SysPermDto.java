package com.gem.baize.api.system.perm.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class SysPermDto {
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

    /**
     * 子权限列表（树形接口 /system/perm/tree 出参，列表/分页接口为 null）。
     * 字段名与前端 ProTable tree 数据的 childrenColumnName 保持一致。
     */
    private List<SysPermDto> children;
}
