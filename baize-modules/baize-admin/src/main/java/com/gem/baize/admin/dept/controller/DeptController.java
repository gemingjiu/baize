package com.gem.baize.admin.dept.controller;

import com.gem.baize.admin.dept.entity.Dept;
import com.gem.baize.admin.dept.service.DeptService;
import com.gem.baize.api.admin.dept.domain.dto.DeptDTO;
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
@RequestMapping("/admin/dept")
public class DeptController {
    @Autowired
    private DeptService deptService;

    @GetMapping("/{bizId}")
    @Operation(summary = "根据ID获取部门", description = "根据ID查询部门信息")
    public Result<DeptDTO> getById(@PathVariable String bizId) {
        if (StringUtils.isBlank(bizId)) {
            throw new BadRequestException("请求参数bizId不能为空》");
        }
        Dept dept = deptService.getByBizId(bizId);
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(dept, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建部门")
    public Result<Integer> create(@Valid @RequestBody DeptDTO dto) {
        Dept dept = new Dept();
        BeanUtils.copyProperties(dto, dept);
        return Result.success(deptService.create(dept));
    }

    @PutMapping("/{bizId}")
    @Operation(summary = "更新部门")
    public Result<Void> update(@PathVariable String bizId, @Valid @RequestBody DeptDTO dto) {
        Dept dept = new Dept();
        BeanUtils.copyProperties(dto, dept);
        dept.setBizId(bizId);
        deptService.update(dept);
        return Result.success();
    }

    @DeleteMapping("/{bizId}")
    @Operation(summary = "删除部门")
    public Result<Void> delete(@PathVariable String bizId) {
        deptService.deleteByBizId(bizId);
        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询部门")
    public Result<PageResult<DeptDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody DeptDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        Dept dept = new Dept();
        BeanUtils.copyProperties(dto, dept);
        PageResult<DeptDTO> dtoPageResult = new PageResult<>();
        BeanUtils.copyProperties(deptService.page(pageParam, dept), dtoPageResult);
        return Result.success(dtoPageResult);
    }
}
