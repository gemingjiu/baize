package com.gem.baize.common.core.id;


import com.gem.baize.common.core.id.annotation.GeneratedId;

public interface IdGenerator {

    /**
     * 生成ID
     *
     * @param config ID生成配置（包含策略、前缀、长度等参数）
     * @return 生成的ID字符串
     */
    String generate(GeneratedId config);

    /**
     * 判断是否支持给定的生成策略
     *
     * @param strategy 策略名称（如"uuid", "snowflake"等）
     * @return 是否支持
     */
    boolean supports(String strategy);
}
