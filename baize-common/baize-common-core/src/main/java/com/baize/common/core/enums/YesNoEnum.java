package com.baize.common.core.enums;

/**
 * @author gemj
 * @since 2024/04/06 15:03
 */
public enum YesNoEnum {
    YES("0", "是"), NO("1", "否");

    private final String code;
    private final String desc;

    YesNoEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
