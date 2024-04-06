package com.baize.common.cache.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.baize.common.core.constant.Constants;
import com.baize.common.core.utils.JsonUtils;

public class JsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Set<String> whitePrefixes = new HashSet<>(Arrays.asList(Constants.JSON_WHITELIST_STR));

    private final Class<T> clazz;

    public JsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JsonUtils.toByteArray(t);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        T t = JsonUtils.parseObject(str, clazz);
        String className = t.getClass().getName();
        for (String prefix : whitePrefixes) {
            if (className.startsWith(prefix)) {
                return t;
            }
        }
        throw new SerializationException("Class is not in the whitelist");
    }
}
