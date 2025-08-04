package com.gem.baize.admin.post.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.post.entity.Post;
import com.gem.baize.admin.post.service.PostService;
import com.gem.baize.api.admin.post.domain.dto.PostDTO;
import com.gem.baize.common.core.exception.BadRequestException;
import com.gem.baize.common.core.model.dto.PageParam;
import com.gem.baize.common.core.model.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/post")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("/{bizId}")
    @Operation(summary = "根据ID获取部门", description = "根据ID查询部门信息")
    public Result<PostDTO> getById(@PathVariable String bizId) {
        if (StringUtils.isBlank(bizId)) {
            throw new BadRequestException("请求参数bizId不能为空》");
        }
        Post post = postService.getByBizId(bizId);
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

    @PutMapping("/{bizId}")
    @Operation(summary = "更新部门")
    public Result<Void> update(@PathVariable String bizId, @Valid @RequestBody PostDTO dto) {
        Post post = new Post();
        BeanUtils.copyProperties(dto, post);
        post.setBizId(bizId);
        postService.update(post);
        return Result.success();
    }

    @DeleteMapping("/{bizId}")
    @Operation(summary = "删除部门")
    public Result<Void> delete(@PathVariable String bizId) {
        postService.deleteByBizId(bizId);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询部门")
    public Result<Page<PostDTO>> page(@RequestParam("current") int current, @RequestParam("size") int size, @Valid @RequestBody PostDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        Post post = new Post();
        BeanUtils.copyProperties(dto, post);
        Page<Post> page = postService.page(pageParam, post);
        Page<PostDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage);
        return Result.success(dtoPage);
    }
}
