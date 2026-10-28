package com.gem.baize.system.dept.controller;

import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import com.gem.baize.system.dept.entity.SysDept;
import com.gem.baize.system.dept.service.SysDeptService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.gem.baize.common.core.constant.PermCodes;
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {
    @Autowired
    private SysDeptService sysDeptService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取部门", description = "根据ID查询部门信息")
    @RequiresPermission(PermCodes.DEPT_QUERY)
    public ApiResult<SysDeptDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }

        return ApiResult.ok(sysDeptService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建部门")
    @RequiresPermission(PermCodes.DEPT_ADD)
    public ApiResult<Integer> create(@Valid @RequestBody SysDeptDto dto) {
        sysDeptService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部门")
    @RequiresPermission(PermCodes.DEPT_EDIT)
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysDeptDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }

        sysDeptService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    @RequiresPermission(PermCodes.DEPT_REMOVE)
    public ApiResult<Void> delete(@PathVariable String id) {
        sysDeptService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询部门")
    @RequiresPermission(PermCodes.DEPT_LIST)
    public ApiResult<PageResult<SysDeptDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysDeptDto dto) {
        Page<SysDept> page = new Page<>(current, size);
        PageConvert<SysDeptDto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(sysDeptService.page(page, dto)));
    }

    @PostMapping("/tree")
    @Operation(summary = "查询部门树")
    @RequiresPermission(PermCodes.DEPT_LIST)
    public ApiResult<List<SysDeptDto>> tree(@RequestBody(required = false) SysDeptDto dto) {
        if (dto == null) {
            dto = new SysDeptDto();
        }
        return ApiResult.ok(sysDeptService.tree(dto));
    }

    @GetMapping("/treeselect")
    @Operation(summary = "获取部门树选择框数据")
    @RequiresPermission(PermCodes.DEPT_QUERY)
    public ApiResult<List<SysDeptDto>> treeselect(@RequestParam(required = false) String tenantId) {
        SysDeptDto dto = new SysDeptDto();
        dto.setTenantId(tenantId);
        return ApiResult.ok(sysDeptService.tree(dto));
    }

    @PostMapping("/list")
    @Operation(summary = "获取部门列表")
    @RequiresPermission(PermCodes.DEPT_LIST)
    public ApiResult<List<SysDeptDto>> list(@RequestBody(required = false) SysDeptDto dto) {
        if (dto == null) {
            dto = new SysDeptDto();
        }
        return ApiResult.ok(sysDeptService.list(dto));
    }
}
