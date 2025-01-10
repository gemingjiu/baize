package com.baize.system.domain;

import com.baize.common.core.domain.BaseEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 参数配置表 sys_config
 *
 * @author gemj
 * @since 2024/4/6 10:17:41
 */
@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
public class SysConfig extends BaseEntity {
    private static final Long serialVersionUID = 1L;
    /**
     * 参数ID
     */
    private String id;
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
