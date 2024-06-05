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
import com.baize.system.domain.SysIdGen;
import com.baize.system.service.ISysIdGenService;


/**
 * 自增ID生成Controller
 *
 * @author baize
 * @date 2024-05-13
 */
@RestController
@RequestMapping("/idGen")
public class SysIdGenController extends BaseController {
    @Autowired
    private ISysIdGenService sysIdGenService;

/**
 * 查询自增ID生成列表
 */
@RequiresPermissions("system:idGen:list")
@GetMapping("/list")
    public TableCollection list(SysIdGen sysIdGen) {
        startPage();
        List<SysIdGen> list = sysIdGenService.selectSysIdGenList(sysIdGen);
        return getDataTable(list);
    }



    /**
     * 获取自增ID生成详细信息
     */
    @RequiresPermissions("system:idGen:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(sysIdGenService.selectSysIdGenById(id));
    }

    /**
     * 新增自增ID生成
     */
    @RequiresPermissions("system:idGen:add")
    @PostMapping
    public AjaxResult add(@RequestBody SysIdGen sysIdGen) {
        return toAjax(sysIdGenService.insertSysIdGen(sysIdGen));
    }

    /**
     * 修改自增ID生成
     */
    @RequiresPermissions("system:idGen:edit")
    @PutMapping
    public AjaxResult edit(@RequestBody SysIdGen sysIdGen) {
        return toAjax(sysIdGenService.updateSysIdGen(sysIdGen));
    }
    @RequiresPermissions("system:idGen:edit")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysIdGen sysIdGen) {
        return toAjax(sysIdGenService.updateSysIdGenStatus(sysIdGen));
    }
    /**
     * 删除自增ID生成
     */
    @RequiresPermissions("system:idGen:remove")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(sysIdGenService.deleteSysIdGenByIds(ids));
    }
}