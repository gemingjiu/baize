package com.gem.baize.system.dept.controller;

import com.gem.baize.system.dept.entity.SysDept;
import com.gem.baize.system.dept.service.SysDeptService;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDTO;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.Result;
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
    private SysDeptService deptService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取部门", description = "根据ID查询部门信息")
    public Result<SysDeptDTO> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        SysDept sysDept = deptService.getById(id);
        SysDeptDTO dto = new SysDeptDTO();
        BeanUtils.copyProperties(sysDept, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建部门")
    public Result<Integer> create(@Valid @RequestBody SysDeptDTO dto) {
        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(dto, sysDept);
        return Result.success(deptService.create(sysDept));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部门")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysDeptDTO dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(dto, sysDept);
        deptService.updateById(sysDept);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public Result<Void> delete(@PathVariable String id) {
        deptService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询部门")
    public Result<PageResult<SysDeptDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysDeptDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(dto, sysDept);
        PageResult<SysDeptDTO> dtoPageResult = new PageResult<>();
        BeanUtils.copyProperties(deptService.page(pageParam, sysDept), dtoPageResult);
        return Result.success(dtoPageResult);
    }
}
