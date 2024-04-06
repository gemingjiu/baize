package com.baize.common.core.enums;

/**
 * 运行状态
 *
 * @author gemj
 * @since 2024/04/05 19:23
 */
public enum RunningStatus {
    RUNNING("0", "启用"), // 运行中

    STOPPED("1", "禁用"), // 已停止

    PAUSED("2", "删除"); // 已暂停

    private final String status;
    private final String desc;

    RunningStatus(String code, String desc) {
        this.status = code;
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
