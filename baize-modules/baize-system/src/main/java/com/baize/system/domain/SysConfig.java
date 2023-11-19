package com.baize.system.domain;


import com.baize.common.core.domain.BaseEntity;
import lombok.Data;


/**
 * @author gemj
 * @since 2023/12/14 22:50
 */
@Data
public class SysConfig extends BaseEntity {
    private static final Long serialVersionUID = 1L;
    /**
     * 参数名称
     */

    private String configName;

    /**
     * 参数键名
     */

    private String configKey;

    /**
     * 参数键值
     */

    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    private String configType;
}
