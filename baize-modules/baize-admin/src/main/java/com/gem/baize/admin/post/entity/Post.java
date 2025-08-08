package com.gem.baize.admin.post.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.core.annotation.GeneratedId;
import com.gem.baize.common.datasource.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_post")
public class Post extends BaseEntity {
    /**
     * 业务ID
     */
    @GeneratedId(prefix = "post-")
    @TableField(value = "biz_id", fill = FieldFill.INSERT_UPDATE)
    private String bizId;
    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private long tenantId;
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
