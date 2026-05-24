package com.gem.baize.system.notice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_notice")
public class SysNotice extends BaseEntity {

    @TableField(value = "tenant_id")
    private String tenantId;

    @TableField(value = "notice_title")
    private String noticeTitle;

    @TableField(value = "notice_content")
    private String noticeContent;

    @TableField(value = "notice_type")
    private String noticeType;
}
