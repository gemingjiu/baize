package com.gem.baize.system.user.controller;

import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.common.core.service.OnlineUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/online")
public class OnlineUserController {

    @Autowired
    private OnlineUserService onlineUserService;

    @GetMapping("/count")
    @Operation(summary = "获取在线用户数量")
    public ApiResult<Long> getOnlineCount(@RequestParam(value = "tenantId", required = false) String tenantId) {
        return ApiResult.ok(onlineUserService.getOnlineCount(tenantId));
    }

    @GetMapping("/list")
    @Operation(summary = "获取在线用户列表")
    public ApiResult<List<String>> getOnlineUsers(@RequestParam(value = "tenantId", required = false) String tenantId) {
        return ApiResult.ok(onlineUserService.getOnlineUsers(tenantId));
    }

    @GetMapping("/isOnline")
    @Operation(summary = "检查用户是否在线")
    public ApiResult<Boolean> isOnline(@RequestParam("userId") String userId,
                                       @RequestParam(value = "tenantId", required = false) String tenantId) {
        return ApiResult.ok(onlineUserService.isOnline(userId, tenantId));
    }

    @PostMapping("/kickout")
    @Operation(summary = "强制用户下线")
    public ApiResult<Void> kickout(@RequestParam("userId") String userId,
                                   @RequestParam(value = "tenantId", required = false) String tenantId) {
        onlineUserService.kickout(userId, tenantId);
        return ApiResult.ok();
    }
}
