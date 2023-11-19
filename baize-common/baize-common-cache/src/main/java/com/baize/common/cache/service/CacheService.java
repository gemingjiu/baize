package com.baize.common.cache.service;

import org.springframework.data.redis.core.BoundSetOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author gemj
 * @since 2023/12/11 09:37
 */
public interface CacheService {
    <T> void setCacheObject(String key, T value);

    <T> void setCacheObject(
            String key, T value, Long timeout, TimeUnit timeUnit);

    boolean expire(String key, long timeout);

    boolean expire(String key, long timeout, TimeUnit unit);

    long getExpire(String key);

    Boolean hasKey(String key);

    <T> T getCacheObject(String key);

    boolean deleteObject(String key);

    boolean deleteObject(Collection collection);

    <T> long setCacheList(String key, List<T> dataList);

    <T> List<T> getCacheList(String key);

    <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet);

    <T> Set<T> getCacheSet(String key);

    <T> void setCacheMap(String key, Map<String, T> dataMap);

    <T> Map<String, T> getCacheMap(String key);

    <T> void setCacheMapValue(String key, String hKey, T value);

    <T> T getCacheMapValue(String key, String hKey);

    <T> List<T> getMultiCacheMapValue(String key, Collection<Object> hKeys);

    boolean deleteCacheMapValue(String key, String hKey);

    Collection<String> keys(String pattern);
}
