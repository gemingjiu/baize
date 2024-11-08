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
import com.baize.system.domain.SysId;
import com.baize.system.service.ISysIdService;


/**
 * 自增ID生成Controller
 *
 * @author baize
 * @date 2024-05-13
 */
@RestController
@RequestMapping("/id")
public class SysIdController extends BaseController {
    @Autowired
    private ISysIdService sysIdGenService;

/**
 * 查询自增ID生成列表
 */
@RequiresPermissions("system:id:list")
@GetMapping("/list")
    public TableCollection list(SysId sysId) {
        startPage();
        List<SysId> list = sysIdGenService.selectSysIdList(sysId);
        return getDataTable(list);
    }



    /**
     * 获取自增ID生成详细信息
     */
    @RequiresPermissions("system:id:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(sysIdGenService.selectSysIdById(id));
    }

    /**
     * 新增自增ID生成
     */
    @RequiresPermissions("system:id:add")
    @PostMapping
    public AjaxResult add(@RequestBody SysId sysId) {
        return toAjax(sysIdGenService.insertSysId(sysId));
    }

    /**
     * 修改自增ID生成
     */
    @RequiresPermissions("system:id:edit")
    @PutMapping
    public AjaxResult edit(@RequestBody SysId sysId) {
        return toAjax(sysIdGenService.updateSysId(sysId));
    }

    /**
     * 修改ID状态
     * @param sysId
     * @return
     */
    @RequiresPermissions("system:id:edit")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysId sysId) {
        return toAjax(sysIdGenService.updateSysIdStatus(sysId));
    }
    /**
     * 删除自增ID生成
     */
    @RequiresPermissions("system:id:remove")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(sysIdGenService.deleteSysIdByIds(ids));
    }
}