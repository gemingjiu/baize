package com.gem.baize.api.system.menu.client;

import com.gem.baize.api.system.menu.domain.dto.SysMenuDto;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(path = "/system", name = "baize-system", url = "${baize.system.url:http://localhost:8020}", contextId = "SysMenuFeignClient")
public interface SysMenuFeignClient {

    /**
     * 根据ID获取菜单
     */
    @GetMapping("/menu/{id}")
    ApiResult<SysMenuDto> getById(@PathVariable("id") String id);

    /**
     * 创建菜单
     */
    @PostMapping("/menu")
    ApiResult<Integer> create(@RequestBody SysMenuDto dto);

    /**
     * 更新菜单
     */
    @PutMapping("/menu/{id}")
    ApiResult<Void> update(@PathVariable("id") String id, @RequestBody SysMenuDto dto);

    /**
     * 删除菜单
     */
    @DeleteMapping("/menu/{id}")
    ApiResult<Void> delete(@PathVariable("id") String id);

    /**
     * 分页查询菜单
     */
    @PostMapping("/menu/page")
    ApiResult<com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysMenuDto>> page(@RequestParam("current") int current,
                                                                                           @RequestParam("size") int size,
                                                                                           @RequestBody SysMenuDto dto);

    /**
     * 查询菜单树
     */
    @PostMapping("/menu/tree")
    ApiResult<List<SysMenuDto>> tree(@RequestBody(required = false) SysMenuDto dto);

    /**
     * 根据用户ID查询菜单树
     */
    @GetMapping("/menu/user/tree")
    ApiResult<List<SysMenuDto>> getUserMenuTree(@RequestHeader("X-User-Id") String userId);

    /**
     * 获取菜单树选择框数据
     */
    @GetMapping("/menu/treeselect")
    ApiResult<List<SysMenuDto>> treeselect(@RequestParam(value = "tenantId", required = false) String tenantId);

    /**
     * 获取角色菜单树选择框数据
     */
    @GetMapping("/menu/roleMenuTreeselect/{roleId}")
    ApiResult<List<SysMenuDto>> roleMenuTreeselect(@PathVariable("roleId") String roleId,
                                                   @RequestParam(value = "tenantId", required = false) String tenantId);
}
