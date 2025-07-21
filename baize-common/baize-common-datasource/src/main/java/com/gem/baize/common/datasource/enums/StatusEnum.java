package com.gem.baize.common.datasource.enums;


import com.baomidou.mybatisplus.annotation.IEnum;

public enum StatusEnum implements IEnum<String> {
    ENABLED("0", "启用"),
    DISABLED("1", "禁用");

    private final String code;
    private final String desc;

    StatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
