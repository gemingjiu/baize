package com.gem.baize.system.dept.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.vo.Result;
import com.gem.baize.system.dept.entity.SysDept;
import com.gem.baize.system.dept.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/dept")
public class SysDeptController {
    @Autowired
    private SysDeptService sysDeptService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取部门", description = "根据ID查询部门信息")
    public Result<SysDeptDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }

        return Result.success(sysDeptService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建部门")
    public Result<Integer> create(@Valid @RequestBody SysDeptDto dto) {
        return Result.success(sysDeptService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部门")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysDeptDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }

        sysDeptService.updateById(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public Result<Void> delete(@PathVariable String id) {
        sysDeptService.removeById(id);
        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询部门")
    public Result<Page<SysDeptDto>> page(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @Valid @RequestBody SysDeptDto dto) {
        Page<SysDept> page = new Page<>(pageNum, pageSize);
        return Result.success(sysDeptService.page(page, dto));
    }
}
