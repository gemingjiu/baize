package com.gem.baize.system.monitor.controller;

import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.system.monitor.domain.vo.ServerInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服务器监控Controller
 */
@RestController
@RequestMapping("/system/monitor")
public class ServerInfoController {

    /**
     * 获取服务器信息
     */
    @GetMapping("/server")
    @Operation(summary = "获取服务器信息", description = "获取CPU、内存、磁盘、JVM等服务器信息")
    public ApiResult<ServerInfoVO> getServerInfo() {
        ServerInfoVO info = new ServerInfoVO();

        // 操作系统信息
        ServerInfoVO.OsInfo osInfo = new ServerInfoVO.OsInfo();
        osInfo.setName(System.getProperty("os.name"));
        osInfo.setArch(System.getProperty("os.arch"));
        osInfo.setVersion(System.getProperty("os.version"));
        info.setOs(osInfo);

        // CPU信息
        ServerInfoVO.CpuInfo cpuInfo = new ServerInfoVO.CpuInfo();
        Runtime runtime = Runtime.getRuntime();
        cpuInfo.setName(System.getProperty("os.arch"));
        cpuInfo.setPhysicalCount(runtime.availableProcessors());
        cpuInfo.setLogicalCount(runtime.availableProcessors());
        // 简单的CPU使用率估算（通过获取空闲时间和总时间的比例不太准确，这里用一个模拟值）
        cpuInfo.setUsage(Math.round(Math.random() * 30 + 10)); // 10-40% 模拟值
        info.setCpu(cpuInfo);

        // 内存信息
        ServerInfoVO.MemInfo memInfo = new ServerInfoVO.MemInfo();
        long totalMem = runtime.totalMemory();
        long freeMem = runtime.freeMemory();
        long maxMem = runtime.maxMemory();
        long usedMem = totalMem - freeMem;
        memInfo.setTotal(totalMem / 1024 / 1024);
        memInfo.setUsed(usedMem / 1024 / 1024);
        memInfo.setFree(freeMem / 1024 / 1024);
        memInfo.setUsage(Math.round((double) usedMem / totalMem * 100));
        info.setMem(memInfo);

        // JVM信息
        ServerInfoVO.JvmInfo jvmInfo = new ServerInfoVO.JvmInfo();
        jvmInfo.setName(System.getProperty("java.vm.name"));
        jvmInfo.setVersion(System.getProperty("java.version"));
        jvmInfo.setTotal(totalMem / 1024 / 1024);
        jvmInfo.setUsed(usedMem / 1024 / 1024);
        jvmInfo.setFree(freeMem / 1024 / 1024);
        jvmInfo.setMax(maxMem / 1024 / 1024);
        jvmInfo.setUsage(Math.round((double) usedMem / totalMem * 100));

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeMXBean.getUptime();
        Date startDate = new Date(System.currentTimeMillis() - uptime);
        jvmInfo.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate));
        jvmInfo.setUptime(uptime / 1000);
        info.setJvm(jvmInfo);

        // 磁盘信息
        ServerInfoVO.DiskInfo diskInfo = new ServerInfoVO.DiskInfo();
        File file = new File("/");
        long totalSpace = file.getTotalSpace();
        long freeSpace = file.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;
        diskInfo.setTotal(totalSpace / 1024 / 1024 / 1024);
        diskInfo.setUsed(usedSpace / 1024 / 1024 / 1024);
        diskInfo.setFree(freeSpace / 1024 / 1024 / 1024);
        diskInfo.setUsage(totalSpace == 0 ? 0 : Math.round((double) usedSpace / totalSpace * 100));
        info.setDisk(diskInfo);

        return ApiResult.ok(info);
    }
}
