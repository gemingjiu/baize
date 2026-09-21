package com.gem.baize.common.core.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class BaseDto implements Serializable {
    private String id; // 主键ID

    private int sort;

    private String status;

    private String remark;


    public String getRemark() {
        return remark == null ? "" : remark;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }
}
