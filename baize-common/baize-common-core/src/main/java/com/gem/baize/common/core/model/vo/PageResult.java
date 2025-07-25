package com.gem.baize.common.core.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private Long current;
    private Long size;
    private Long total;
    private List<T> records;
}
