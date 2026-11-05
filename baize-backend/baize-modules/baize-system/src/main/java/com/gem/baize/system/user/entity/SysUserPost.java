package com.gem.baize.system.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseRelationEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户岗位关联（2026-11-05 P6 启用，此前 sys_user_post 为"保留暂未实现"空表）。
 * <p>
 * 三列关系表（tenant_id/user_id/post_id），主键为三列复合（V2026_11_07 迁移补齐）；
 * 继承 BaseRelationEntity 走 tenant_id 自动填充与行级隔离。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user_post")
public class SysUserPost extends BaseRelationEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 岗位ID
     */
    @TableField("post_id")
    private String postId;
}