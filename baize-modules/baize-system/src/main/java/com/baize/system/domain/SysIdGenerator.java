package com.baize.system.domain;

import com.baize.common.core.domain.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自增ID生成对象 sys_id_generator
 *
 * @author baize
 * @date 2024-05-13
 */
@Data
public class SysIdGenerator extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /** 主键ID */
    @ApiModelProperty(name = "主键ID", notes = "")
    private String id;
    /** 当前最大id */
    @ApiModelProperty(name = "当前最大id", notes = "")
    private Long maxId;
    /** 号段的布长 */
    @ApiModelProperty(name = "号段的布长", notes = "")
    private Integer step;
    /** 业务类型 */
    @ApiModelProperty(name = "业务类型", notes = "")
    private String bizType;

}