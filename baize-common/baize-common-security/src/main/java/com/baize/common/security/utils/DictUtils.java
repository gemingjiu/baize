package com.baize.common.security.utils;

import java.util.Collection;
import java.util.List;

import com.baize.common.cache.service.CacheService;
import com.baize.common.core.constant.CacheConstants;
import com.baize.common.core.utils.SpringUtils;
import com.baize.system.api.domain.SysDictData;

/**
 * 字典工具类
 * 
 * @author gemj
 */
public class DictUtils {
    /**
     * 设置字典缓存
     * 
     * @param key 参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas) {
        String completeKey = getCacheKey(key);
        SpringUtils.getBean(CacheService.class).setCacheObject(completeKey, dictDatas);
    }

    /**
     * 获取字典缓存
     * 
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key) {
        String completeKey = getCacheKey(key);
        return SpringUtils.getBean(CacheService.class).getCacheObject(completeKey);
    }

    /**
     * 删除指定字典缓存
     * 
     * @param key 字典键
     */
    public static void removeDictCache(String key) {
        String completeKey = getCacheKey(key);
        SpringUtils.getBean(CacheService.class).deleteObject(completeKey);
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache() {
        String queryKey = getCacheKey("*");
        Collection<String> keys = SpringUtils.getBean(CacheService.class).keys(queryKey);
        SpringUtils.getBean(CacheService.class).deleteObject(keys);
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return CacheConstants.SYS_DICT_PREFIX + configKey;
    }
}
