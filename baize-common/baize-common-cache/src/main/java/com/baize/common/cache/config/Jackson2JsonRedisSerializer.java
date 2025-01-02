package com.baize.common.cache.config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.baize.common.core.constant.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
 * Jackson 序列化
 *
 * @author gemj
 * @since 2024/04/07 09:10
 */

@Slf4j
public class Jackson2JsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final ObjectMapper objectMapper;
    private final Class<T> clazz;

    private final String[] jsonWhiteList = Constants.JSON_WHITELIST_STR;

    public Jackson2JsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // POJO无public的属性或方法时，允许序列化空的POJO类，否则序列化空对象时会抛出异常
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // POJO允许属性数量不相等的对象进行反序列化，否则会抛出异常
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化JSON串时，在值上打印出对象类型，替换上方过期的enableDefaultTyping
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会抛出异常
        this.objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY);
        // 解决jackson2无法反序列化LocalDateTime的问题
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (Exception e) {
            throw new SerializationException("将对象序列化为 JSON 时发生错误: " + t, e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            T t = objectMapper.readValue(bytes, clazz);
            String className = t.getClass().getName();
            for (String prefix : jsonWhiteList) {
                if (className.startsWith(prefix)) {
                    return t;
                }
            }
            throw new SerializationException("将 JSON 反序列化为对象时发生错误: " + className + "该对象不在白名单之内.");
        } catch (IOException e) {
            throw new SerializationException("将 JSON 反序列化为对象时发生错误: " + new String(bytes, DEFAULT_CHARSET), e);
        }
    }
}
