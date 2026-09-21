package com.gem.baize.system.monitor.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务器信息VO
 */
@Data
public class ServerInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作系统信息
     */
    private OsInfo os;

    /**
     * CPU信息
     */
    private CpuInfo cpu;

    /**
     * 内存信息
     */
    private MemInfo mem;

    /**
     * JVM信息
     */
    private JvmInfo jvm;

    /**
     * 磁盘信息
     */
    private DiskInfo disk;

    @Data
    public static class OsInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 操作系统名称 */
        private String name;
        /** 系统架构 */
        private String arch;
        /** 系统版本 */
        private String version;
    }

    @Data
    public static class CpuInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /** CPU名称 */
        private String name;
        /** CPU核心数（物理） */
        private int physicalCount;
        /** CPU核心数（逻辑） */
        private int logicalCount;
        /** CPU使用率（百分比） */
        private double usage;
    }

    @Data
    public static class MemInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 总内存（MB） */
        private long total;
        /** 已用内存（MB） */
        private long used;
        /** 剩余内存（MB） */
        private long free;
        /** 使用率（百分比） */
        private double usage;
    }

    @Data
    public static class JvmInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /** JVM名称 */
        private String name;
        /** JVM版本 */
        private String version;
        /** JVM总内存（MB） */
        private long total;
        /** JVM已用内存（MB） */
        private long used;
        /** JVM剩余内存（MB） */
        private long free;
        /** JVM最大内存（MB） */
        private long max;
        /** 使用率（百分比） */
        private double usage;
        /** 启动时间 */
        private String startTime;
        /** 运行时长（秒） */
        private long uptime;
    }

    @Data
    public static class DiskInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 总容量（GB） */
        private long total;
        /** 已用容量（GB） */
        private long used;
        /** 剩余容量（GB） */
        private long free;
        /** 使用率（百分比） */
        private double usage;
    }
}
