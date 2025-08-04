package com.gem.baize.admin.menu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.menu.entity.Menu;
import com.gem.baize.admin.menu.service.MenuService;
import com.gem.baize.api.admin.menu.domain.dto.MenuDTO;
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
@RequestMapping("/admin/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping("/{bizId}")
    @Operation(summary = "根据ID获取菜单", description = "根据ID查询菜单信息")
    public Result<MenuDTO> getById(@PathVariable String bizId) {
        if (StringUtils.isBlank(bizId)) {
            throw new BadRequestException("请求参数bizId不能为空》");
        }
        Menu menu = menuService.getByBizId(bizId);
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(menu, dto);
        return Result.success(dto);
    }

    @PostMapping
    @Operation(summary = "创建菜单")
    public Result<Integer> create(@Valid @RequestBody MenuDTO dto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        return Result.success(menuService.create(menu));
    }

    @PutMapping("/{bizId}")
    @Operation(summary = "更新菜单")
    public Result<Void> update(@PathVariable String bizId, @Valid @RequestBody MenuDTO dto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        menu.setBizId(bizId);
        menuService.update(menu);
        return Result.success();
    }

    @DeleteMapping("/{bizId}")
    @Operation(summary = "删除菜单")
    public Result<Void> delete(@PathVariable String bizId) {
        menuService.deleteByBizId(bizId);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询菜单")
    public Result<Page<MenuDTO>> page(@RequestParam("current") int current, @RequestParam("size") int size, @Valid @RequestBody MenuDTO dto) {
        PageParam pageParam = new PageParam(current, size);
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        Page<Menu> page = menuService.page(pageParam, menu);
        Page<MenuDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(page, dtoPage);
        return Result.success(dtoPage);
    }
}
