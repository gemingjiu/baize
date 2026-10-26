package com.gem.baize.system.monitor.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
     * 磁盘信息（第一个根分区，兼容旧的单一分区渲染）
     */
    private DiskInfo disk;

    /**
     * 磁盘分区列表（File.listRoots() 遍历结果，Windows 下为 C:\ D:\ ...，Linux 下为 /）
     */
    private List<DiskInfo> disks;

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
        /**
         * CPU使用率（百分比）。
         * -1 表示取不到（OperatingSystemMXBean 不支持 getProcessCpuLoad /
         * getSystemCpuLoad，且 getSystemLoadAverage() 也返回负数），前端需展示「未知」
         * 而不是把它当成真实百分比。
         */
        private double usage;
        /** 系统平均负载（1 分钟）；不支持时为 -1 */
        private double loadAverage;
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
        /** 分区名称/挂载点（Windows: C:\，Linux: /） */
        private String name;
        /** 分区绝对路径 */
        private String path;
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
