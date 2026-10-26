package com.gem.baize.system.monitor.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Redis 缓存监控信息VO
 */
@Data
public class CacheInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Redis 版本 */
    private String version;
    /** 运行模式（standalone / cluster / sentinel） */
    private String mode;
    /** 连接的客户端数 */
    private Long connectedClients;
    /** 已用内存（人类可读，如 1.59M） */
    private String usedMemoryHuman;
    /** 内存使用峰值（人类可读） */
    private String usedMemoryPeakHuman;
    /** 已用内存（字节） */
    private Long usedMemory;
    /** 最大内存限制（字节，0 表示不限制） */
    private Long maxMemory;
    /** 内存淘汰策略 */
    private String maxMemoryPolicy;
    /** key 总数（DBSIZE） */
    private Long dbSize;
    /** 命中率（0~100，保留一位小数；无访问记录时为 -1） */
    private Double hitRate;
    /** 累计命中次数 */
    private Long keyspaceHits;
    /** 累计未命中次数 */
    private Long keyspaceMisses;
    /** 每秒操作数 */
    private Long instantaneousOpsPerSec;
    /** 运行时长（秒） */
    private Long uptimeInSeconds;
    /** 运行时长（人类可读，如 2天 8小时） */
    private String uptimeHuman;
    /** Redis 不可达时为 true，前端据此提示 */
    private boolean unavailable;
    /** 不可达或解析失败时的原因 */
    private String errorMessage;
    /** 按前缀分组的 key 数量统计 */
    private List<KeyPrefixStat> prefixStats;
    /** 数据库维度统计（db0: keys=1,expires=1...） */
    private List<DbStat> dbStats;

    @Data
    public static class KeyPrefixStat implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 前缀（含分隔符，如 sys_config::） */
        private String prefix;
        /** 该前缀下的 key 数量 */
        private Long count;
    }

    @Data
    public static class DbStat implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 数据库名（db0 / db1 ...） */
        private String db;
        /** key 数量 */
        private Long keys;
        /** 带过期时间的 key 数量 */
        private Long expires;
    }
}
