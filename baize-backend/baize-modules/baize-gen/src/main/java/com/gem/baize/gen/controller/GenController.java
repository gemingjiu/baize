package com.gem.baize.gen.controller;

import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.gen.domain.dto.GenTableDTO;
import com.gem.baize.gen.domain.vo.GenTableVO;
import com.gem.baize.gen.service.GenService;
import com.gem.baize.gen.util.GenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 代码生成Controller
 */
@Tag(name = "代码生成器")
@RestController
@RequestMapping("/gen")
public class GenController {

    @Autowired
    private GenService genService;

    @GetMapping("/tables")
    @Operation(summary = "获取数据库表列表")
    public ApiResult<List<Map<String, Object>>> tables() {
        return ApiResult.ok(genService.getTableList());
    }

    @GetMapping("/table/{tableName}")
    @Operation(summary = "获取表字段信息")
    public ApiResult<GenTableVO> tableInfo(@PathVariable String tableName) {
        GenTableVO tableInfo = genService.getTableInfo(tableName);
        // 自动推断类名
        if (tableInfo.getClassName() == null) {
            tableInfo.setClassName(GenUtils.tableToClassName(tableName, null));
        }
        return ApiResult.ok(tableInfo);
    }

    @PostMapping("/generate")
    @Operation(summary = "生成代码")
    public ApiResult<Map<String, String>> generate(@RequestBody GenTableDTO dto) {
        return ApiResult.ok(genService.generateCode(dto));
    }
}
