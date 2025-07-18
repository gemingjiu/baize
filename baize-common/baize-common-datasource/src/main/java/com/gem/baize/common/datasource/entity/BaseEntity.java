package com.gem.baize.common.datasource.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    private String bizId;     // 业务ID

    @TableField(value = "version", fill = FieldFill.INSERT_UPDATE)
    @Version
    private Long version;

    @TableField(value = "status", fill = FieldFill.INSERT_UPDATE)
    private String status;

    @TableField(value = "deleted", fill = FieldFill.INSERT_UPDATE)
    private String deleted;

    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @TableField(value = "modified_by", fill = FieldFill.INSERT_UPDATE)
    private Long modifiedBy;

    @TableField(value = "modified_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedTime;

    @TableField(value = "remark", fill = FieldFill.INSERT_UPDATE)
    private String remark;

    public void initCreated() {
        this.status = "0";
        this.deleted = "0";
        this.version = 1L;
        this.createdBy = 0L;
        this.createdTime = LocalDateTime.now();
    }

    public void initModified() {
        this.modifiedBy = 0L;
        this.modifiedTime = LocalDateTime.now();
    }
}
