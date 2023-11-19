package com.baize.system.controller;


import com.baize.common.core.controller.BaseController;
import com.baize.common.core.domain.AjaxResult;
import com.baize.common.core.domain.TableCollection;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.common.security.annotation.RequiresPermissions;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.api.domain.SysDictData;
import com.baize.system.service.ISysDictDataService;
import com.baize.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典信息
 * 
 * @author gemj
 */
@RestController
@RequestMapping("/dict/data")
public class SysDictDataController extends BaseController
{
    @Autowired
    private ISysDictDataService dictDataService;
    
    @Autowired
    private ISysDictTypeService dictTypeService;

    @RequiresPermissions("system:dict:list")
    @GetMapping("/list")
    public TableCollection list(SysDictData dictData)
    {
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return getDataTable(list);
    }



    /**
     * 查询字典数据详细
     */

    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(@PathVariable String dictCode)
    {
        return success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType)
    {
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return success(data);
    }

    /**
     * 新增字典类型
     */

    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictData dict)
    {
        dict.setModifiedBy(SecurityUtils.getUsername());
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典类型
     */

    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictData dict)
    {
        dict.setModifiedBy(SecurityUtils.getUsername());
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典类型
     */

    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable String[] dictCodes)
    {
        dictDataService.deleteDictDataByIds(dictCodes);
        return success();
    }
}
