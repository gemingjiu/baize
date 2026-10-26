package com.gem.baize.system.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.core.annotation.InnerAuth;
import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.annotation.Log;
import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.core.exception.model.ParamException;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.database.convert.PageConvert;
import com.gem.baize.common.excel.ExcelUtil;
import com.gem.baize.common.security.domain.vo.UserVO;
import com.gem.baize.system.file.service.FileUploadService;
import com.gem.baize.system.user.entity.SysUser;
import com.gem.baize.system.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/system/user")
public class SysUserController {

    /**
     * 单次导出的最大行数：导出走内存组装，需要设上限避免大表把堆打爆。
     */
    private static final int EXPORT_MAX_ROWS = 10000;

    private static final java.util.Map<String, String> GENDER_MAP = java.util.Map.of(
            "0", "男", "1", "女", "2", "未知");

    private static final java.util.Map<String, String> STATUS_MAP = java.util.Map.of(
            "0", "启用", "1", "禁用");

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/uploadAvatar")
    @Operation(summary = "上传头像", description = "上传用户头像")
    public ApiResult<java.util.Map<String, String>> uploadAvatar(@RequestHeader(CustomHttpHeaders.USER_ID) String userId,
                                                                @RequestParam("file") MultipartFile file) throws IOException {
        String url = fileUploadService.upload(file);
        // 更新用户头像
        SysUserDto dto = new SysUserDto();
        dto.setAvatar(url);
        sysUserService.updateProfile(userId, dto);
        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("url", url);
        return ApiResult.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "根据ID查询用户信息")
    @RequiresPermission("system:user:query")
    public ApiResult<SysUserDto> getById(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(sysUserService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    @RequiresPermission("system:user:add")
    @Log(title = "用户管理", businessType = Log.BusinessType.INSERT)
    public ApiResult<Integer> create(@Valid @RequestBody SysUserDto dto) {
        sysUserService.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    @RequiresPermission("system:user:edit")
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
    @RequiresPermission("system:user:remove")
    @Log(title = "用户管理", businessType = Log.BusinessType.DELETE)
    public ApiResult<Void> delete(@PathVariable String id) {
        sysUserService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询用户")
    @RequiresPermission("system:user:list")
    public ApiResult<PageResult<SysUserDto>> page(@RequestParam(value = "current", defaultValue = "1") int current, @RequestParam(value = "size", defaultValue = "10") int size, @Valid @RequestBody SysUserDto dto) {
        Page<SysUser> query = new Page<>(current, size);
        Page<SysUserDto> pages = sysUserService.page(query, dto);
        PageConvert<SysUserDto> pageConvert = new PageConvert<>();
        return ApiResult.ok(pageConvert.toDto(pages));
    }

    /**
     * 导出用户列表。
     * <p>
     * 复用 page 的查询条件，但把页大小设为导出上限并跳过 count 查询（导出不需要总数）。
     */
    @PostMapping("/export")
    @Operation(summary = "导出用户", description = "按查询条件导出用户列表为 Excel")
    @RequiresPermission("system:user:export")
    public void export(@RequestBody(required = false) SysUserDto dto, HttpServletResponse response) {
        Page<SysUser> query = new Page<>(1, EXPORT_MAX_ROWS);
        query.setSearchCount(false);
        List<SysUserDto> records = sysUserService.page(query, dto).getRecords();

        LinkedHashMap<String, java.util.function.Function<SysUserDto, Object>> columns = new LinkedHashMap<>();
        columns.put("用户ID", SysUserDto::getId);
        columns.put("登录名", SysUserDto::getUserName);
        columns.put("用户昵称", SysUserDto::getNickName);
        columns.put("邮箱", SysUserDto::getEmail);
        columns.put("手机号", SysUserDto::getPhone);
        columns.put("性别", u -> GENDER_MAP.getOrDefault(u.getGender(), "未知"));
        columns.put("状态", u -> STATUS_MAP.getOrDefault(u.getStatus(), "未知"));
        columns.put("所属部门ID", SysUserDto::getDeptId);

        ExcelUtil.export(response, "用户列表", "用户数据",
                ExcelUtil.head(columns.keySet().toArray(new String[0])),
                ExcelUtil.buildRows(records, columns));
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户信息")
    public ApiResult<UserVO> currentUser(@RequestHeader(CustomHttpHeaders.USER_ID) String userId) {
        return ApiResult.ok(sysUserService.getCurrentUser(userId));
    }

    @GetMapping("/getByUsername")
    @Operation(summary = "根据用户名查询用户", description = "根据用户名查询用户信息")
    @InnerAuth
    public ApiResult<SysUserDto> getByUsername(@RequestParam("username") String username) {
        if (StringUtils.isBlank(username)) {
            throw new ParamException("用户名不能为空");
        }
        return ApiResult.ok(sysUserService.getByUsername(username));
    }

    @GetMapping("/getByUsernameAndTenant")
    @Operation(summary = "根据用户名和租户ID查询用户", description = "根据用户名和租户ID查询用户信息")
    @InnerAuth
    public ApiResult<SysUserDto> getByUsernameAndTenant(@RequestParam("username") String username,
                                                        @RequestParam("tenantId") String tenantId) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(tenantId)) {
            throw new ParamException("用户名和租户ID不能为空");
        }
        return ApiResult.ok(sysUserService.getByUsernameAndTenantId(username, tenantId));
    }

    /**
     * 仅限认证服务内网调用：返回密码哈希用于口令校验。
     * 通用查询接口的 SysUserDto.password 是 WRITE_ONLY，不会出现在 JSON 中，故单开此接口。
     */
    @GetMapping("/getPasswordByUsername")
    @Operation(summary = "查询用户密码哈希", description = "仅供认证服务校验口令，禁止对外暴露")
    @InnerAuth
    public ApiResult<String> getPasswordByUsername(@RequestParam("username") String username) {
        if (StringUtils.isBlank(username)) {
            throw new ParamException("用户名不能为空");
        }
        return ApiResult.ok(sysUserService.getPasswordByUsername(username));
    }

    @PutMapping("/updateLastLoginTime")
    @Operation(summary = "更新最后登录时间", description = "更新用户最后登录时间和IP")
    @InnerAuth
    public ApiResult<Void> updateLastLoginTime(@RequestParam("userId") String userId,
                                               @RequestParam(value = "loginIp", required = false) String loginIp) {
        sysUserService.updateLastLoginTime(userId, loginIp);
        return ApiResult.ok();
    }

    @PutMapping("/resetPassword")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    @RequiresPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> resetPassword(@RequestParam("userId") String userId,
                                         @RequestParam("newPassword") String newPassword) {
        sysUserService.resetPassword(userId, newPassword);
        return ApiResult.ok();
    }

    @PutMapping("/updatePassword")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @RequiresPermission("system:user:updatePwd")
    @Log(title = "用户管理", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> updatePassword(@RequestParam("userId") String userId,
                                          @RequestParam("oldPassword") String oldPassword,
                                          @RequestParam("newPassword") String newPassword) {
        sysUserService.updatePassword(userId, oldPassword, newPassword);
        return ApiResult.ok();
    }

    @PutMapping("/profile")
    @Operation(summary = "修改个人信息", description = "用户修改自己的个人资料")
    public ApiResult<Void> updateProfile(@RequestHeader(CustomHttpHeaders.USER_ID) String userId,
                                         @Valid @RequestBody SysUserDto dto) {
        sysUserService.updateProfile(userId, dto);
        return ApiResult.ok();
    }

    @PutMapping("/changeStatus")
    @Operation(summary = "修改用户状态", description = "修改用户启用/禁用状态")
    @RequiresPermission("system:user:edit")
    @Log(title = "用户管理", businessType = Log.BusinessType.UPDATE)
    public ApiResult<Void> changeStatus(@RequestParam("userId") String userId,
                                        @RequestParam("status") String status) {
        sysUserService.changeStatus(userId, status);
        return ApiResult.ok();
    }
}