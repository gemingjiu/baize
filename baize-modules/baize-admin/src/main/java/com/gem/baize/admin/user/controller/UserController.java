package com.gem.baize.admin.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.user.entity.User;
import com.gem.baize.admin.user.service.UserService;
import com.gem.baize.api.admin.user.domain.dto.UserDTO;
import com.gem.baize.api.admin.user.domain.vo.UserVO;
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
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{bizId}")
    @Operation(summary = "根据ID获取用户", description = "根据ID查询用户信息")
    public Result<UserVO> getById(@PathVariable String bizId) {
        if (StringUtils.isBlank(bizId)) {
            throw new BadRequestException("请求参数bizId不能为空》");
        }
        User user = userService.getByBizId(bizId);
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return Result.success(vo);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public Result<Integer> create(@Valid @RequestBody UserDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return Result.success(userService.create(user));
    }

    @PutMapping("/{bizId}")
    @Operation(summary = "更新用户")
    public Result<Void> update(@PathVariable String bizId, @Valid @RequestBody UserDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setBizId(bizId);
        userService.update(user);
        return Result.success();
    }

    @DeleteMapping("/{bizId}")
    @Operation(summary = "删除用户")
    public Result<Void> delete(@PathVariable String bizId) {
        userService.deleteByBizId(bizId);
        return Result.success();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询用户")
    public Result<Page<UserVO>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody UserDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        Page<User> page = userService.page(pageParam, user);
        Page<UserVO> voPage = new Page<>();
        BeanUtils.copyProperties(page, voPage);
        return Result.success(voPage);
    }
}