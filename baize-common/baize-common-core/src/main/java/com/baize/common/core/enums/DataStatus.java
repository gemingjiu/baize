package com.baize.common.core.enums;

/**
 * 数据状态
 *
 * @author gemj
 * @since 2024/04/05 19:26
 */
public enum DataStatus {
    ENABLED("0", "启用"), DISABLED("1", "停用"), DELETED("2", "已删除");

    private final String status;
    private final String description;

    DataStatus(String code, String description) {
        this.status = code;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
