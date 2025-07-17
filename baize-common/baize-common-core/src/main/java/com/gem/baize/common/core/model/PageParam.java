package com.gem.baize.common.core.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageParam {
    @Schema(description = "当前页", example = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序方式(asc/desc)")
    private String sortOrder;
}