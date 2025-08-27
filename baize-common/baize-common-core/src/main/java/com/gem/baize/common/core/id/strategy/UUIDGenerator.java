package com.gem.baize.common.core.id.strategy;

import com.gem.baize.common.core.id.annotation.GeneratedId;
import com.gem.baize.common.core.id.IdGenerator;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator implements IdGenerator {

    @Override
    public String generate(GeneratedId config) {
        String uuid = UUID.randomUUID().toString();
        return config.prefix() + uuid;
    }

    @Override
    public boolean supports(String strategy) {
        return "uuid".equals(strategy);
    }
}
