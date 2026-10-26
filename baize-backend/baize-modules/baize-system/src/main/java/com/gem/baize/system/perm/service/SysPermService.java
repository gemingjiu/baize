package com.gem.baize.system.perm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.perm.domain.dto.SysPermDto;
import com.gem.baize.system.perm.entity.SysPerm;

import java.util.List;

/**
 * 部门服务类接口
 */
public interface SysPermService extends IService<SysPerm> {

    SysPermDto getById(String id);

    void create(SysPermDto sysPermDto);

    void updateById(SysPermDto sysPermDto);

    void removeById(String id);

    Page<SysPermDto> page(Page<SysPerm> page, SysPermDto sysPermDto);

    /**
     * 查询权限树。
     * <p>
     * 对应 GET /system/perm/tree。该端点必须存在：若缺失，请求会被
     * {@code @GetMapping("/{id}")} 捕获并让 id 拿到字符串 "tree"，
     * 最终以数据库类型错误的形式 500，错误信息与真实原因无关。
     */
    List<SysPermDto> tree();
}
