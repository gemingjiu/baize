package com.baize.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baize.common.core.controller.BaseController;
import com.baize.common.core.domain.AjaxResult;
import com.baize.common.core.domain.TableCollection;
import com.baize.common.security.annotation.RequiresPermissions;
import com.baize.system.domain.SysIdGenerator;
import com.baize.system.service.ISysIdGeneratorService;


/**
 * 自增ID生成Controller
 *
 * @author baize
 * @date 2024-05-13
 */
@RestController
@RequestMapping("/generator")
public class SysIdGeneratorController extends BaseController {
    @Autowired
    private ISysIdGeneratorService sysIdGeneratorService;

/**
 * 查询自增ID生成列表
 */
@RequiresPermissions("system:generator:list")
@GetMapping("/list")
    public TableCollection list(SysIdGenerator sysIdGenerator) {
        startPage();
        List<SysIdGenerator> list = sysIdGeneratorService.selectSysIdGeneratorList(sysIdGenerator);
        return getDataTable(list);
    }



    /**
     * 获取自增ID生成详细信息
     */
    @RequiresPermissions("system:generator:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(sysIdGeneratorService.selectSysIdGeneratorById(id));
    }

    /**
     * 新增自增ID生成
     */
    @RequiresPermissions("system:generator:add")
    @PostMapping
    public AjaxResult add(@RequestBody SysIdGenerator sysIdGenerator) {
        return toAjax(sysIdGeneratorService.insertSysIdGenerator(sysIdGenerator));
    }

    /**
     * 修改自增ID生成
     */
    @RequiresPermissions("system:generator:edit")
    @PutMapping
    public AjaxResult edit(@RequestBody SysIdGenerator sysIdGenerator) {
        return toAjax(sysIdGeneratorService.updateSysIdGenerator(sysIdGenerator));
    }

    /**
     * 删除自增ID生成
     */
    @RequiresPermissions("system:generator:remove")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(sysIdGeneratorService.deleteSysIdGeneratorByIds(ids));
    }
}