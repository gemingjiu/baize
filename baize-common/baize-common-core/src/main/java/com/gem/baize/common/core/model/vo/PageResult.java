package com.gem.baize.common.core.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageResult<T> {
    /**
     * 总数
     */
    private Long total;
    /**
     * 煤业显示条数
     */
    private Long size;
    /**
     * 当前页
     */
    private Long current;
    /**
     * 查询数据列表
     */
    private List<T> records;
    /**
     * 排序字段信息
     */
    private List<OrderItem> orders;
    /**
     * 单页分页条数限制
     */
    private Long maxLimit;
    /**
     * countId
     */
    private String countId;
}
