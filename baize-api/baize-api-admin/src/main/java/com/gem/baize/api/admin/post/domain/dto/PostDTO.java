package com.gem.baize.api.admin.post.domain.dto;

import lombok.Data;

@Data
public class PostDTO {
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
