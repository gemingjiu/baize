package com.gem.baize.api.system.post.domain.dto;

import lombok.Data;

@Data
public class SysPostDto {
    /**
     * 业务ID
     */
    private String id;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 岗位编码
     */
    private String postCode;
    /**
     * 岗位名称
     */
    private String postName;
}
