package com.gem.baize.common.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {
    @TableField(value = "version", fill = FieldFill.INSERT_UPDATE)
    private Long version;

    @TableField(value = "status", fill = FieldFill.INSERT_UPDATE)
    private String status;

    @TableField(value = "deleted", fill = FieldFill.INSERT_UPDATE)
    private String deleted;

    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "modified_by", fill = FieldFill.INSERT_UPDATE)
    private Long modifiedBy;

    @TableField(value = "modified_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedTime;

    @TableField(value = "remark", fill = FieldFill.INSERT_UPDATE)
    private String remark;
}
