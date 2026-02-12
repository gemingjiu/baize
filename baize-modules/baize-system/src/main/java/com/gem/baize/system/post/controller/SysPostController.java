package com.gem.baize.system.post.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.post.entity.SysPost;
import com.gem.baize.system.post.service.SysPostService;
import com.gem.baize.api.system.post.domain.dto.SysPostDto;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/post")
public class SysPostController {
    @Autowired
    private SysPostService sysPostService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取部门", description = "根据ID查询部门信息")
    public ApiResult<SysPostDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysPostService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建部门")
    public ApiResult<Integer> create(@Valid @RequestBody SysPostDto dto) {
        sysPostService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部门")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysPostDto dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }

        sysPostService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public ApiResult<Void> delete(@PathVariable String id) {
        sysPostService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询部门")
    public ApiResult<Page<SysPostDto>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @Valid @RequestBody SysPostDto dto) {
        Page<SysPost> page = new Page<>(pageNum, pageSize);
        return ApiResult.ok(sysPostService.page(page, dto));
    }
}
