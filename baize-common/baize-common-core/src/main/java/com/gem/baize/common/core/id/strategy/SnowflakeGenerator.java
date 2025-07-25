package com.gem.baize.common.core.id.strategy;

import com.gem.baize.common.core.annotation.GeneratedId;
import com.gem.baize.common.core.id.IdGenerator;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeGenerator implements IdGenerator {
    @Override
    public String generate(GeneratedId config) {
        return "";
    }

    @Override
    public boolean supports(String strategy) {
        return "snowflake".equals(strategy);
    }
}
