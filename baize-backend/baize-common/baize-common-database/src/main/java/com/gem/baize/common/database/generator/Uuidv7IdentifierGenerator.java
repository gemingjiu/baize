package com.gem.baize.common.database.generator;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * UUIDv7 主键生成器（RFC 9562）。
 * <p>
 * 布局：48bit 毫秒时间戳 + 4bit 版本(7) + 12bit 同毫秒单调序列 + 2bit 变体(10) + 62bit 随机。
 * 时间有序：同一毫秒内 rand_a 序列递增，跨毫秒由时间戳天然有序；
 * 时钟回拨时沿用上一毫秒并继续递增，保证单调不回退。
 * <p>
 * MP 装配：MybatisPlusAutoConfiguration#getBeanThen 会优先采用容器中的
 * IdentifierGenerator Bean，覆盖自带的 DefaultIdentifierGenerator（雪花 Long）。
 */
public class Uuidv7IdentifierGenerator implements IdentifierGenerator {

    /** 高 52 bit = 毫秒时间戳，低 12 bit = 同毫秒序列 */
    private static final AtomicLong STATE = new AtomicLong(0L);

    @Override
    public String nextUUID(Object entity) {
        return nextUUID();
    }

    @Override
    public Number nextId(Object entity) {
        // 全库主键已切换 ASSIGN_UUID（UUIDv7），ASSIGN_ID 路径显式禁用，调用即快速失败
        throw new UnsupportedOperationException("ASSIGN_ID 已废弃：请将 @TableId 设为 IdType.ASSIGN_UUID");
    }

    /** 生成带连字符的 36 位 UUIDv7 文本 */
    public static String nextUUID() {
        while (true) {
            long now = System.currentTimeMillis();
            long prev = STATE.get();
            long prevMs = prev >>> 12;
            long prevSeq = prev & 0xFFF;
            long ms;
            long seq;
            if (now > prevMs) {
                ms = now;
                seq = ThreadLocalRandom.current().nextLong(0x1000);
            } else {
                // 同毫秒（或时钟回拨）：序列递增，溢出则借下一毫秒
                ms = prevMs;
                seq = prevSeq + 1;
                if (seq > 0xFFF) {
                    ms = prevMs + 1;
                    seq = 0;
                }
            }
            if (STATE.compareAndSet(prev, (ms << 12) | seq)) {
                long msb = (ms << 16) | 0x7000L | seq;
                long lsb = 0x8000000000000000L
                        | (ThreadLocalRandom.current().nextLong() & 0x3FFFFFFFFFFFFFFFL);
                return new UUID(msb, lsb).toString();
            }
        }
    }
}
