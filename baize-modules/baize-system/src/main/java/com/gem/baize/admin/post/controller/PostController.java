package com.gem.baize.admin.post.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.post.entity.Post;
import com.gem.baize.admin.post.service.PostService;
import com.gem.baize.api.admin.post.domain.dto.PostDTO;
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
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取部门", description = "根据ID查询部门信息")
    public Result<PostDTO> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BadRequestException("请求参数id不能为空");
        }
        Post post = postService.getById(id);
        PostDTO dto = new PostDTO();
        BeanUtils.copyProperties(post, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建部门")
    public Result<Integer> create(@Valid @RequestBody PostDTO dto) {
        Post post = new Post();
        BeanUtils.copyProperties(dto, post);
        return Result.success(postService.create(post));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部门")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody PostDTO dto) {
        // 双重验证
        if(!id.equals(dto.getId())) {
            throw new BadRequestException("请求参数id不一致");
        }
        Post post = new Post();
        BeanUtils.copyProperties(dto, post);
        postService.update(post);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public Result<Void> delete(@PathVariable String id) {
        postService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/search")
    @Operation(summary = "分页查询部门")
    public Result<Page<PostDTO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody PostDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        Post post = new Post();
        BeanUtils.copyProperties(dto, post);
        Page<Post> page = postService.page(pageParam, post);
        Page<PostDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage);
        return Result.success(dtoPage);
    }
}
