package com.gem.baize.system.post.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.post.entity.SysPost;
import com.gem.baize.system.post.service.SysPostService;
import com.gem.baize.api.system.post.domain.dto.SysPostDTO;
import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.common.core.model.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/post")
public class SysPostController {
    @Autowired
    private SysPostService sysPostService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取部门", description = "根据ID查询部门信息")
    public Result<SysPostDTO> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        SysPost sysPost = sysPostService.getById(id);
        SysPostDTO dto = new SysPostDTO();
        BeanUtils.copyProperties(sysPost, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建部门")
    public Result<Integer> create(@Valid @RequestBody SysPostDTO dto) {
        SysPost sysPost = new SysPost();
        BeanUtils.copyProperties(dto, sysPost);
        return Result.success(sysPostService.create(sysPost));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部门")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody SysPostDTO dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        SysPost sysPost = new SysPost();
        BeanUtils.copyProperties(dto, sysPost);
        sysPostService.update(sysPost);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public Result<Void> delete(@PathVariable String id) {
        sysPostService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询部门")
    public Result<Page<SysPostDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysPostDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        SysPost sysPost = new SysPost();
        BeanUtils.copyProperties(dto, sysPost);
        Page<SysPost> page = sysPostService.page(pageParam, sysPost);
        Page<SysPostDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage);
        return Result.success(dtoPage);
    }
}
