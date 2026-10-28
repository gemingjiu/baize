package com.gem.baize.system.monitor.controller;

import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.monitor.domain.vo.ServerInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gem.baize.common.core.constant.PermCodes;
/**
 * 服务器监控Controller
 */
@Slf4j
@RestController
@RequestMapping("/system/monitor")
public class ServerInfoController {

    /** CPU 使用率取不到时的哨兵值：前端据此显示「未知」而不是 0% */
    private static final double CPU_USAGE_UNKNOWN = -1D;
    /** 系统负载取不到时的哨兵值（JDK 的 getSystemLoadAverage() 约定返回负数） */
    private static final double LOAD_AVERAGE_UNKNOWN = -1D;

    /**
     * 获取服务器信息
     */
    @GetMapping("/server")
    @Operation(summary = "获取服务器信息", description = "获取CPU、内存、磁盘、JVM等服务器信息")
    @RequiresPermission(PermCodes.SERVER_QUERY)
    public ApiResult<ServerInfoVO> getServerInfo() {
        ServerInfoVO info = new ServerInfoVO();

        // 操作系统信息
        ServerInfoVO.OsInfo osInfo = new ServerInfoVO.OsInfo();
        osInfo.setName(System.getProperty("os.name"));
        osInfo.setArch(System.getProperty("os.arch"));
        osInfo.setVersion(System.getProperty("os.version"));
        info.setOs(osInfo);

        // CPU信息
        info.setCpu(buildCpuInfo());

        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        ServerInfoVO.MemInfo memInfo = new ServerInfoVO.MemInfo();
        long totalMem = runtime.totalMemory();
        long freeMem = runtime.freeMemory();
        long maxMem = runtime.maxMemory();
        long usedMem = totalMem - freeMem;
        memInfo.setTotal(totalMem / 1024 / 1024);
        memInfo.setUsed(usedMem / 1024 / 1024);
        memInfo.setFree(freeMem / 1024 / 1024);
        memInfo.setUsage(totalMem == 0 ? 0 : Math.round((double) usedMem / totalMem * 100));
        info.setMem(memInfo);

        // JVM信息
        ServerInfoVO.JvmInfo jvmInfo = new ServerInfoVO.JvmInfo();
        jvmInfo.setName(System.getProperty("java.vm.name"));
        jvmInfo.setVersion(System.getProperty("java.version"));
        jvmInfo.setTotal(totalMem / 1024 / 1024);
        jvmInfo.setUsed(usedMem / 1024 / 1024);
        jvmInfo.setFree(freeMem / 1024 / 1024);
        jvmInfo.setMax(maxMem / 1024 / 1024);
        jvmInfo.setUsage(totalMem == 0 ? 0 : Math.round((double) usedMem / totalMem * 100));

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeMXBean.getUptime();
        Date startDate = new Date(System.currentTimeMillis() - uptime);
        jvmInfo.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate));
        jvmInfo.setUptime(uptime / 1000);
        info.setJvm(jvmInfo);

        // 磁盘信息：遍历所有根分区（Windows 多盘符 / Linux 多挂载点）
        List<ServerInfoVO.DiskInfo> disks = buildDiskInfos();
        info.setDisks(disks);
        // 兼容旧渲染：disk 指向第一个分区
        info.setDisk(disks.isEmpty() ? null : disks.get(0));

        return ApiResult.ok(info);
    }

    /**
     * 采集 CPU 信息，使用率取真实值。
     *
     * <p>取值优先级：
     * <ol>
     *   <li>{@code com.sun.management.OperatingSystemMXBean#getProcessCpuLoad()}：
     *       本 JVM 进程自上次采样以来的 CPU 占用（0~1），×100 转百分比；</li>
     *   <li>{@code getSystemCpuLoad()}：整机 CPU 占用（0~1）；</li>
     *   <li>{@code getSystemLoadAverage()}：1 分钟平均负载，兜底换算成
     *       load / 逻辑核数 的百分比（可能 &gt; 100，语义为「负载」而非「占用」）；</li>
     *   <li>都拿不到（JDK 未实现 / 平台不支持，接口约定返回负数）时返回 -1，
     *       由前端展示「未知」。</li>
     * </ol>
     *
     * 注意：JDK 的 CPU load 采样基于 JVM 内部的两次采样间隔，进程刚启动时
     * 第一次调用可能返回负值，这里按「未知」处理而不是当成 0%。
     */
    private ServerInfoVO.CpuInfo buildCpuInfo() {
        ServerInfoVO.CpuInfo cpuInfo = new ServerInfoVO.CpuInfo();
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        cpuInfo.setName(System.getProperty("os.arch"));
        cpuInfo.setPhysicalCount(processors);
        cpuInfo.setLogicalCount(processors);

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        double loadAverage = LOAD_AVERAGE_UNKNOWN;
        try {
            loadAverage = osBean.getSystemLoadAverage();
        } catch (Exception e) {
            log.debug("getSystemLoadAverage 不可用: {}", e.getMessage());
        }
        cpuInfo.setLoadAverage(loadAverage);

        double usage = CPU_USAGE_UNKNOWN;
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            // 进程级 CPU 占用：0~1，负数代表不可用（首帧/平台不支持）
            double processLoad = sunOsBean.getProcessCpuLoad();
            if (processLoad >= 0) {
                usage = roundToOneDecimal(processLoad * 100);
            } else {
                // 整机 CPU 占用：0~1
                double systemLoad = sunOsBean.getSystemCpuLoad();
                if (systemLoad >= 0) {
                    usage = roundToOneDecimal(systemLoad * 100);
                }
            }
        }

        // 两个 load 接口都不可用时，用平均负载 / 核数兜底估算
        if (usage < 0 && loadAverage >= 0 && processors > 0) {
            usage = roundToOneDecimal(loadAverage / processors * 100);
        }

        cpuInfo.setUsage(usage);
        return cpuInfo;
    }

    /**
     * 遍历 {@link File#listRoots()} 采集所有磁盘分区信息。
     *
     * <p>不可读/未挂载的分区（USB 空槽、网络盘掉线）getTotalSpace() 会返回 0，
     * 这里整块跳过，避免把 0 容量分区渲染成 100% 使用率的假象。
     */
    private List<ServerInfoVO.DiskInfo> buildDiskInfos() {
        List<ServerInfoVO.DiskInfo> disks = new ArrayList<>();
        File[] roots = File.listRoots();
        if (roots == null) {
            return disks;
        }
        for (File root : roots) {
            long totalSpace;
            long freeSpace;
            try {
                totalSpace = root.getTotalSpace();
                freeSpace = root.getFreeSpace();
            } catch (Exception e) {
                // 分区不可访问（权限/掉线），跳过
                log.debug("磁盘分区 {} 不可访问: {}", root.getPath(), e.getMessage());
                continue;
            }
            if (totalSpace <= 0) {
                continue;
            }
            long usedSpace = totalSpace - freeSpace;

            ServerInfoVO.DiskInfo diskInfo = new ServerInfoVO.DiskInfo();
            diskInfo.setName(root.getPath());
            diskInfo.setPath(root.getAbsolutePath());
            diskInfo.setTotal(totalSpace / 1024 / 1024 / 1024);
            diskInfo.setUsed(usedSpace / 1024 / 1024 / 1024);
            diskInfo.setFree(freeSpace / 1024 / 1024 / 1024);
            diskInfo.setUsage(Math.round((double) usedSpace / totalSpace * 100));
            disks.add(diskInfo);
        }
        return disks;
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
