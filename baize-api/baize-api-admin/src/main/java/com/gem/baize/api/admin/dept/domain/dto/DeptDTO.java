package com.gem.baize.api.admin.dept.domain.dto;

import lombok.Data;

@Data
public class DeptDTO {
    /**
     * 业务ID
     */
    private String bizId;
    /**
     * 租户ID
     */

    private String tenantId;
    /**
     * 父部门id
     */

    private String parentId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 负责人
     */

    private String leader;
    /**
     * 电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
}
