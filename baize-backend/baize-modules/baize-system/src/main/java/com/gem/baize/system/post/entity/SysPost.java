package com.gem.baize.system.post.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_post")
public class SysPost extends BaseEntity {
    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private String tenantId;
    /**
     * 岗位编码
     */
    @TableField(value = "post_code")
    private String postCode;
    /**
     * 岗位名称
     */
    @TableField(value = "post_name")
    private String postName;
}
