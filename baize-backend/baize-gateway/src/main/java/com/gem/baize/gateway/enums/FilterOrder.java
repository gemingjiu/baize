package com.gem.baize.gateway.enums;

import lombok.Getter;

/**
 * 过滤器顺序枚举
 */
@Getter
public enum FilterOrder {

    TRACE_ID_FILTER(-1000, "traceId 生成过滤器"),
    AUTH_FILTER(-900, "JWT 认证过滤器"),
    TENANT_FILTER(-800, "租户校验过滤器"),
    LOGGING_FILTER(-700, "请求日志过滤器"),
    RATE_LIMITER_FILTER(-600, "限流过滤器"),
    GRAY_ROUTE_FILTER(-500, "灰度路由过滤器");

    private final int order;
    private final String desc;

    FilterOrder(int order, String desc) {
        this.order = order;
        this.desc = desc;
    }

    public int getOrder() {
        return order;
    }

    public String getDesc() {
        return desc;
    }
}
