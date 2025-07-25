package com.gem.baize.common.core.id.strategy;

import com.gem.baize.common.core.annotation.GeneratedId;
import com.gem.baize.common.core.id.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class IncrementGenerator implements IdGenerator {
    private static final AtomicLong counter = new AtomicLong(0);

    @Override
    public String generate(GeneratedId config) {
        return config.prefix() + counter.incrementAndGet();
    }

    @Override
    public boolean supports(String strategy) {
        return "increment".equals(strategy);
    }
}
