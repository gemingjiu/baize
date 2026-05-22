package com.gem.baize.system.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.core.annotation.Log;
import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.security.domain.vo.UserVO;
import com.gem.baize.system.user.entity.SysUser;
import com.gem.baize.system.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "根据ID查询用户信息")
    public ApiResult<SysUserDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysUserService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    @Log(title = "用户管理", businessType = Log.BusinessType.INSERT)
    public ApiResult<Integer> create(@Valid @RequestBody SysUserDto dto) {
        sysUserService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    @Log(title = "用户管理", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody SysUserDto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }

        sysUserService.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @Log(title = "用户管理", businessType = Log.BusinessType.DELETE)
    public ApiResult<Void> delete(@PathVariable String id) {
        sysUserService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询用户")
    public ApiResult<Page<SysUserDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysUserDto dto) {
        Page<SysUser> query = new Page<>(current, size);
        Page<SysUserDto> pages = sysUserService.page(query, dto);
        return ApiResult.ok(pages);
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户信息")
    public ApiResult<UserVO> currentUser(@RequestHeader(CustomHttpHeaders.USER_ID) String userId) {
        return ApiResult.ok(sysUserService.getCurrentUser(userId));
    }

    @GetMapping("/getByUsername")
    @Operation(summary = "根据用户名查询用户", description = "根据用户名查询用户信息")
    public ApiResult<SysUserDto> getByUsername(@RequestParam("username") String username) {
        if (StringUtils.isBlank(username)) {
            throw new ParamException("用户名不能为空");
        }
        return ApiResult.ok(sysUserService.getByUsername(username));
    }

    @GetMapping("/getByUsernameAndTenant")
    @Operation(summary = "根据用户名和租户ID查询用户", description = "根据用户名和租户ID查询用户信息")
    public ApiResult<SysUserDto> getByUsernameAndTenant(@RequestParam("username") String username,
                                                        @RequestParam("tenantId") String tenantId) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(tenantId)) {
            throw new ParamException("用户名和租户ID不能为空");
        }
        return ApiResult.ok(sysUserService.getByUsernameAndTenantId(username, tenantId));
    }

    @PutMapping("/updateLastLoginTime")
    @Operation(summary = "更新最后登录时间", description = "更新用户最后登录时间和IP")
    public ApiResult<Void> updateLastLoginTime(@RequestParam("userId") String userId,
                                               @RequestParam(value = "loginIp", required = false) String loginIp) {
        sysUserService.updateLastLoginTime(userId, loginIp);
        return ApiResult.ok();
    }

    @PutMapping("/resetPassword")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    @Log(title = "用户管理", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> resetPassword(@RequestParam("userId") String userId,
                                         @RequestParam("newPassword") String newPassword) {
        sysUserService.resetPassword(userId, newPassword);
        return ApiResult.ok();
    }

    @PutMapping("/updatePassword")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @Log(title = "用户管理", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> updatePassword(@RequestParam("userId") String userId,
                                          @RequestParam("oldPassword") String oldPassword,
                                          @RequestParam("newPassword") String newPassword) {
        sysUserService.updatePassword(userId, oldPassword, newPassword);
        return ApiResult.ok();
    }

    @PutMapping("/changeStatus")
    @Operation(summary = "修改用户状态", description = "修改用户启用/禁用状态")
    @Log(title = "用户管理", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> changeStatus(@RequestParam("userId") String userId,
                                        @RequestParam("status") String status) {
        sysUserService.changeStatus(userId, status);
        return ApiResult.ok();
    }
}