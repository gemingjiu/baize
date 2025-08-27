package com.gem.baize.common.database.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    @TableField(value = "sort", fill = FieldFill.INSERT)
    private int sort;

    @TableField(value = "version", fill = FieldFill.INSERT_UPDATE)
    @Version
    private Long version;

    @TableField(value = "status", fill = FieldFill.INSERT_UPDATE)
    private String status;

    @TableField(value = "deleted", fill = FieldFill.INSERT_UPDATE)
    private String deleted;

    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @TableField(value = "modified_by", fill = FieldFill.INSERT_UPDATE)
    private String modifiedBy;

    @TableField(value = "modified_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedTime;

    @TableField(value = "remark", fill = FieldFill.INSERT_UPDATE)
    private String remark;
}
