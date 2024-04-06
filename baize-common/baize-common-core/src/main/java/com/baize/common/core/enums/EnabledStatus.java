package com.baize.common.core.enums;

/**
 * 启用状态枚举值
 *
 * @author gemj
 * @since 2023/08/22 15:44
 */

public enum EnabledStatus {
    ENABLED("0", "启用"), // 启用
    DISABLED("1", "禁用"); // 禁用

    private final String status;
    private final String desc;

    EnabledStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
