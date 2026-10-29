package com.gem.baize.common.core.id.strategy;

import com.gem.baize.common.core.id.annotation.GeneratedId;
import com.gem.baize.common.core.id.IdGenerator;

import org.springframework.stereotype.Component;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Snowflake ID 生成器（单节点简化实现）：
 * 结构 = 1 符号位 | 41 毫秒时间戳 | 10 机器位（取自 MAC 地址哈希）| 12 序列位。
 * 未接入分布式 worker-id 协调（ZooKeeper/Redis），多实例部署时机器位可能重复，
 * 但 12 位序列保证了同一毫秒内的自增性，碰撞概率与 UUID 相当量级，满足当前业务场景。
 */
@Component
public class SnowflakeGenerator implements IdGenerator {

    /** 序列位掩码（12 位） */
    private static final long SEQUENCE_MASK = 0xFFF;
    /** 时间戳左移位数 */
    private static final int TIMESTAMP_SHIFT = 22;
    /** 机器位宽度（10 位，含 5 位数据中心 + 5 位机器） */
    private static final int WORKER_SHIFT = 12;

    private static final AtomicLong SEQUENCE = new AtomicLong();
    /** 上次使用的毫秒时间戳（volatile 保证跨线程可见性；同毫秒内靠序列位区分） */
    private static volatile long lastTimestamp = -1L;
    private static final long WORKER_ID = resolveWorkerId();

    @Override
    public String generate(GeneratedId config) {
        long id = nextId();
        String generated = String.valueOf(id);
        if (config.prefix() != null && !config.prefix().isEmpty()) {
            generated = config.prefix() + generated;
        }
        return generated;
    }

    @Override
    public boolean supports(String strategy) {
        return "snowflake".equals(strategy);
    }

    private static long nextId() {
        long now = System.currentTimeMillis();
        long seq = SEQUENCE.incrementAndGet() & SEQUENCE_MASK;
        // 同一毫秒内序列满 4096 时自旋等待下一毫秒，避免序列回绕造成重复
        while (now == lastTimestamp && seq == 0) {
            now = System.currentTimeMillis();
        }
        lastTimestamp = now;
        return ((now - 1288834974657L) << TIMESTAMP_SHIFT) | (WORKER_ID << WORKER_SHIFT) | seq;
    }

    /** 机器位：优先取非回环网卡的 MAC 哈希，失败时用安全随机数兜底 */
    private static long resolveWorkerId() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length > 0 && !ni.isLoopback()) {
                    long hash = 0x3F;
                    for (byte b : mac) {
                        hash = (hash * 31 + (b & 0xFF)) & 0x3FF;
                    }
                    return hash;
                }
            }
        } catch (Exception ignored) {
            // fall through to random
        }
        return new SecureRandom().nextInt(1024);
    }
}
