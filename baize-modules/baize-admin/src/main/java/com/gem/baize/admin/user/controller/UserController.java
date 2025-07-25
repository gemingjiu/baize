package com.gem.baize.admin.user.controller;


import com.gem.baize.admin.user.service.UserService;
import com.gem.baize.api.admin.user.dto.UserDTO;
import com.gem.baize.api.admin.user.vo.UserVO;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "根据ID查询用户信息")
    public Result<UserVO> getById(@PathVariable String id) {
        return Result.success(userService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public Result<Long> create(@Valid @RequestBody UserDTO dto) {
        return Result.success(userService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public Result<Void> update(@Valid @RequestBody UserDTO dto) {
        userService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询用户")
    public Result<PageResult<UserVO>> page(UserDTO query) {
        return Result.success(userService.page(query));
    }
}