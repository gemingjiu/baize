package com.gem.baize.common.core.id.handle;


import com.gem.baize.common.core.id.annotation.GeneratedId;
import com.gem.baize.common.core.id.IdGenerator;
import com.gem.baize.common.core.id.strategy.IncrementGenerator;
import com.gem.baize.common.core.id.strategy.SnowflakeGenerator;
import com.gem.baize.common.core.id.strategy.UuidGenerator;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IdGeneratorProcessor {
    private static final Map<String, IdGenerator> generators = new ConcurrentHashMap<>();

    static {
        registerDefaultGenerators();
    }

    private static void registerDefaultGenerators() {
        registerGenerator("uuid", new UuidGenerator());
        registerGenerator("snowflake", new SnowflakeGenerator());
        registerGenerator("increment", new IncrementGenerator());
    }

    public static void registerGenerator(String strategy, IdGenerator generator) {
        generators.put(strategy, generator);
    }

    public void process(Object entity) {
        if (entity == null) return;

        Class<?> clazz = entity.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            GeneratedId annotation = field.getAnnotation(GeneratedId.class);
            if (annotation != null) {
                generateAndSetId(entity, field, annotation);
            }
        }
    }

    private void generateAndSetId(Object entity, Field field, GeneratedId config) {
        try {
            IdGenerator generator = generators.get(config.strategy());
            if (generator == null) {
                throw new RuntimeException("Unsupported ID generation strategy: " + config.strategy());
            }

            field.setAccessible(true);
            Object currentValue = field.get(entity);
            if (currentValue == null) {
                String id = generator.generate(config);
                field.set(entity, id);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to generate ID for field: " + field.getName(), e);
        }
    }
}