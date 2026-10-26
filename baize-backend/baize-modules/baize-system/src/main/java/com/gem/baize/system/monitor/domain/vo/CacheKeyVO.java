package com.gem.baize.system.monitor.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Redis key 列表VO
 */
@Data
public class CacheKeyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 查询使用的 pattern */
    private String pattern;
    /** 实际返回的 key 数量 */
    private Integer size;
    /** 是否因达到上限而被截断 */
    private boolean truncated;
    /** 上限值 */
    private Integer limit;
    /** key 明细 */
    private List<KeyItem> keys;

    @Data
    public static class KeyItem implements Serializable {
        private static final long serialVersionUID = 1L;
        /** key 名称 */
        private String key;
        /** 类型：string / hash / list / set / zset ... */
        private String type;
        /** 剩余存活时间（秒）：-1 永不过期，-2 key 不存在 */
        private Long ttl;
        /** TTL 可读文本：永不过期 / 已过期 / 3天2小时 */
        private String ttlHuman;
    }
}
