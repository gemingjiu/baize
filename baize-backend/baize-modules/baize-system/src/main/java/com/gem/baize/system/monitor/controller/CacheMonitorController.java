package com.gem.baize.system.monitor.controller;

import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.monitor.domain.vo.CacheInfoVO;
import com.gem.baize.system.monitor.domain.vo.CacheKeyVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.gem.baize.common.core.constant.PermCodes;
/**
 * Redis 缓存监控Controller
 *
 * <p>只读端点：INFO / DBSIZE / SCAN / TYPE / TTL，不提供任何写操作（清缓存、删 key
 * 不在本次范围内），避免监控接口变成可写面。
 */
@Slf4j
@RestController
@RequestMapping("/system/monitor/cache")
public class CacheMonitorController {

    /** /cache/keys 单次最多返回的 key 数（SCAN 到上限即停止） */
    private static final int MAX_KEYS = 200;
    /** 前缀统计一次最多扫描的 key 数，超出则统计不完整（前端不展示总量时无影响） */
    private static final int MAX_SCAN_FOR_STATS = 10000;
    /** SCAN 每次迭代向 Redis 建议返回的元素数 */
    private static final long SCAN_COUNT = 500L;

    /**
     * 本工程实际会写入 Redis 的 key 前缀清单。
     *
     * <p>来源（均为代码/实测，非臆造）：
     * <ul>
     *   <li>{@code sys_*::} —— baize-system 各 ServiceImpl 的
     *       {@code @Cacheable(cacheNames = "sys_xxx")}，Spring RedisCacheManager 默认
     *       key 前缀为 {@code cacheName + "::"}（CacheKeyPrefix.simple()）；</li>
     *   <li>{@code baize:online:} —— OnlineUserServiceImpl.ONLINE_KEY_PREFIX（ZSET）；</li>
     *   <li>{@code baize:token:blacklist:} —— TokenBlacklistUtils.BLACKLIST_KEY_PREFIX；</li>
     *   <li>{@code refresh_token:} —— baize-auth RedisTokenManager.REFRESH_TOKEN_PREFIX
     *       （实测抽样时库里唯一存在的 key 就是 refresh_token:0:1）。</li>
     * </ul>
     * 未命中任何已知前缀的 key 归入「其他」。
     */
    private static final String[] KEY_PREFIXES = {
            "sys_config::",
            "sys_dept::",
            "sys_role::",
            "sys_perm::",
            "sys_user::",
            "sys_menu::",
            "sys_post::",
            "sys_tenant::",
            "sys_dict_type::",
            "sys_dict_data::",
            "baize:online:",
            "baize:token:blacklist:",
            "refresh_token:"
    };

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取 Redis 基础信息
     */
    @GetMapping
    @Operation(summary = "获取缓存监控信息", description = "Redis 版本、客户端数、内存、命中率、运行时长及按前缀分组的 key 数量")
    @RequiresPermission(PermCodes.CACHE_QUERY)
    public ApiResult<CacheInfoVO> getCacheInfo() {
        CacheInfoVO vo = new CacheInfoVO();
        Properties info = null;
        try {
            info = redisTemplate.execute((RedisCallback<Properties>) RedisConnection::info);
        } catch (Exception e) {
            log.warn("读取 Redis INFO 失败: {}", e.getMessage());
            vo.setUnavailable(true);
            vo.setErrorMessage("无法连接 Redis: " + e.getMessage());
            return ApiResult.ok(vo);
        }

        if (info == null || info.isEmpty()) {
            vo.setUnavailable(true);
            vo.setErrorMessage("Redis INFO 返回为空");
            return ApiResult.ok(vo);
        }

        vo.setVersion(str(info, "redis_version"));
        vo.setMode(str(info, "redis_mode"));
        vo.setConnectedClients(lng(info, "connected_clients"));
        vo.setUsedMemoryHuman(str(info, "used_memory_human"));
        vo.setUsedMemoryPeakHuman(str(info, "used_memory_peak_human"));
        vo.setUsedMemory(lng(info, "used_memory"));
        vo.setMaxMemory(lng(info, "maxmemory"));
        vo.setMaxMemoryPolicy(str(info, "maxmemory_policy"));
        vo.setInstantaneousOpsPerSec(lng(info, "instantaneous_ops_per_sec"));
        Long uptime = lng(info, "uptime_in_seconds");
        vo.setUptimeInSeconds(uptime);
        if (uptime != null) {
            vo.setUptimeHuman(formatUptime(uptime));
        }

        // 命中率：hits / (hits + misses)，一次都没访问过分母为 0，返回 -1 表示未知
        Long hits = lng(info, "keyspace_hits");
        Long misses = lng(info, "keyspace_misses");
        vo.setKeyspaceHits(hits);
        vo.setKeyspaceMisses(misses);
        if (hits != null && misses != null) {
            long total = hits + misses;
            vo.setHitRate(total <= 0 ? -1D : Math.round((double) hits / total * 1000.0) / 10.0);
        } else {
            vo.setHitRate(-1D);
        }

        try {
            Long dbSize = redisTemplate.execute(RedisConnection::dbSize);
            vo.setDbSize(dbSize);
        } catch (Exception e) {
            log.warn("读取 Redis DBSIZE 失败: {}", e.getMessage());
        }

        // db0/db1... 维度统计
        List<CacheInfoVO.DbStat> dbStats = new ArrayList<>();
        for (String name : info.stringPropertyNames()) {
            if (!name.matches("db\\d+")) {
                continue;
            }
            CacheInfoVO.DbStat stat = new CacheInfoVO.DbStat();
            stat.setDb(name);
            stat.setKeys(parseDbField(info.getProperty(name), "keys"));
            stat.setExpires(parseDbField(info.getProperty(name), "expires"));
            dbStats.add(stat);
        }
        dbStats.sort((a, b) -> a.getDb().compareTo(b.getDb()));
        vo.setDbStats(dbStats);

        // 按前缀分组统计（一次 SCAN 完成，避免对每个前缀各扫一遍）
        vo.setPrefixStats(statByPrefix());
        return ApiResult.ok(vo);
    }

    /**
     * 列出匹配 pattern 的 key（SCAN 实现，最多 {@value #MAX_KEYS} 个）
     */
    @GetMapping("/keys")
    @Operation(summary = "查询缓存key列表", description = "按 pattern 用 SCAN 扫描 key，返回 key/类型/TTL，最多 200 个")
    @RequiresPermission(PermCodes.CACHE_QUERY)
    public ApiResult<CacheKeyVO> getCacheKeys(@RequestParam(value = "pattern", defaultValue = "*") String pattern,
                                             @RequestParam(value = "limit", defaultValue = "0") int limit) {
        String safePattern = (pattern == null || pattern.trim().isEmpty()) ? "*" : pattern.trim();
        int max = limit > 0 ? Math.min(limit, MAX_KEYS) : MAX_KEYS;

        CacheKeyVO vo = new CacheKeyVO();
        vo.setPattern(safePattern);
        vo.setLimit(max);

        List<CacheKeyVO.KeyItem> items = new ArrayList<>();
        boolean truncated = false;
        try {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(safePattern)
                    .count(SCAN_COUNT)
                    .build();
            List<byte[]> keys = new ArrayList<>();
            try (Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                    (RedisCallback<Cursor<byte[]>>) conn -> conn.keyCommands().scan(options))) {
                if (cursor != null) {
                    while (cursor.hasNext()) {
                        if (keys.size() >= max) {
                            truncated = true;
                            break;
                        }
                        keys.add(cursor.next());
                    }
                }
            } catch (Exception e) {
                log.warn("SCAN 遍历失败 pattern={}: {}", safePattern, e.getMessage());
                throw e;
            }

            if (!keys.isEmpty()) {
                // TYPE / TTL 逐个查询：key 量已被 MAX_KEYS 限制，往返次数可控
                for (byte[] raw : keys) {
                    String key = new String(raw, StandardCharsets.UTF_8);
                    CacheKeyVO.KeyItem item = new CacheKeyVO.KeyItem();
                    item.setKey(key);
                    try {
                        DataType type = redisTemplate.execute((RedisCallback<DataType>) conn -> conn.type(raw));
                        item.setType(type == null ? "unknown" : type.code());
                    } catch (Exception e) {
                        item.setType("unknown");
                    }
                    try {
                        Long ttl = redisTemplate.execute((RedisCallback<Long>) conn -> conn.ttl(raw));
                        long ttlValue = ttl == null ? -2L : ttl;
                        item.setTtl(ttlValue);
                        item.setTtlHuman(formatTtl(ttlValue));
                    } catch (Exception e) {
                        item.setTtl(-2L);
                        item.setTtlHuman("未知");
                    }
                    items.add(item);
                }
            }
        } catch (Exception e) {
            log.warn("查询缓存 key 列表失败 pattern={}: {}", safePattern, e.getMessage());
        }

        vo.setKeys(items);
        vo.setSize(items.size());
        vo.setTruncated(truncated);
        return ApiResult.ok(vo);
    }

    /**
     * 扫描全库 key 并按已知前缀分组计数。
     *
     * <p>只扫一遍，最多 {@value #MAX_SCAN_FOR_STATS} 个；未匹配任何已知前缀的 key
     * 归入「其他」，保证 prefixStats 各项之和等于实际扫描到的 key 数。
     */
    private List<CacheInfoVO.KeyPrefixStat> statByPrefix() {
        Map<String, Long> counter = new LinkedHashMap<>();
        for (String prefix : KEY_PREFIXES) {
            counter.put(prefix, 0L);
        }
        long others = 0L;

        try {
            ScanOptions options = ScanOptions.scanOptions().match("*").count(SCAN_COUNT).build();
            try (Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                    (RedisCallback<Cursor<byte[]>>) conn -> conn.keyCommands().scan(options))) {
                if (cursor == null) {
                    return toStats(counter, others);
                }
                int scanned = 0;
                while (cursor.hasNext() && scanned < MAX_SCAN_FOR_STATS) {
                    String key = new String(cursor.next(), StandardCharsets.UTF_8);
                    scanned++;
                    String matched = null;
                    for (String prefix : KEY_PREFIXES) {
                        if (key.startsWith(prefix)) {
                            matched = prefix;
                            break;
                        }
                    }
                    if (matched != null) {
                        counter.put(matched, counter.get(matched) + 1);
                    } else {
                        others++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("按前缀统计缓存 key 失败: {}", e.getMessage());
        }

        return toStats(counter, others);
    }

    private List<CacheInfoVO.KeyPrefixStat> toStats(Map<String, Long> counter, long others) {
        List<CacheInfoVO.KeyPrefixStat> stats = new ArrayList<>();
        counter.forEach((prefix, count) -> {
            CacheInfoVO.KeyPrefixStat stat = new CacheInfoVO.KeyPrefixStat();
            stat.setPrefix(prefix);
            stat.setCount(count);
            stats.add(stat);
        });
        if (others > 0) {
            CacheInfoVO.KeyPrefixStat other = new CacheInfoVO.KeyPrefixStat();
            other.setPrefix("其他");
            other.setCount(others);
            stats.add(other);
        }
        // 数量多的排前面，全 0 的前缀沉到末尾，页面上看不到噪音
        stats.sort((a, b) -> {
            int cmp = Long.compare(b.getCount() == null ? 0 : b.getCount(), a.getCount() == null ? 0 : a.getCount());
            return cmp != 0 ? cmp : a.getPrefix().compareTo(b.getPrefix());
        });
        return stats;
    }

    private String str(Properties props, String key) {
        return props.getProperty(key);
    }

    private Long lng(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseDbField(String dbLine, String field) {
        if (dbLine == null) {
            return null;
        }
        for (String part : dbLine.split(",")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].trim().equals(field)) {
                try {
                    return Long.parseLong(kv[1].trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private String formatUptime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分钟");
        }
        return sb.length() == 0 ? "不足1分钟" : sb.toString();
    }

    /** TTL 秒数转可读文本：-1 永不过期，-2 已过期/不存在 */
    private String formatTtl(long ttl) {
        if (ttl < 0) {
            return ttl == -1L ? "永不过期" : "已过期";
        }
        if (ttl < 60) {
            return ttl + "秒";
        }
        if (ttl < 3600) {
            return (ttl / 60) + "分钟";
        }
        if (ttl < 86400) {
            return (ttl / 3600) + "小时" + ((ttl % 3600) / 60) + "分钟";
        }
        return (ttl / 86400) + "天" + ((ttl % 86400) / 3600) + "小时";
    }
}
