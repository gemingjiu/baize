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
     * 每页显示条数
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
     * countId
     */
    private String countId;
}
