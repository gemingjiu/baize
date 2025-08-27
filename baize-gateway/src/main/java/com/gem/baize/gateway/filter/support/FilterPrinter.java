package com.gem.baize.gateway.filter.support;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

import java.util.Map;


@Component
public class FilterPrinter {
    private final ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(FilterPrinter.class);

    public FilterPrinter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void printGlobalFilters() {
        Map<String, GlobalFilter> filters = applicationContext.getBeansOfType(GlobalFilter.class);
        if (log.isInfoEnabled()) {
            log.info("===== Global Filters =====");
            filters.forEach((name, filter) -> {
                // 打印过滤器名称、类名和 Order 值（如果有）
                int order = (filter instanceof Ordered) ? ((Ordered) filter).getOrder() : 0;
                log.info("- {} ({}) : order={}",
                        name,
                        filter.getClass().getSimpleName(),
                        order);
            });
            log.info("===== Global Filters End =====");
        }

    }
}
