package com.baize.system.domain;

import com.baize.common.core.domain.BaseEntity;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 系统ID对象 sys_id
 * 用于管理ID自增方式
 * @author baize
 * @date 2024-05-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysId extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    /** 主键ID */
    @Schema(name = "主键ID")
    private String id;
    /** 业务类型 */
    @Schema(name = "业务类型")
    private String bizType;
    /** 业务描述 */
    @Schema(name = "业务描述")
    private String bizDesc;
    /** 生成类型 */
    @Schema(name = "生成类型")
    private String genType;
    /** 当前最大id */
    @Schema(name = "当前最大id")
    private Long maxId;
    /** 号段的布长 */
    @Schema(name = "号段的布长")
    private Integer step;


}