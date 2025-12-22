package com.gem.baize.system.perm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.system.perm.entity.SysPerm;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 部门服务类接口
 */
public interface SysPermService extends IService<SysPerm> {

    SysPerm getById(String id);

    Integer create(SysPerm sysPerm);

    void update(SysPerm sysPerm);

    void deleteById(String id);

    Page<SysPerm> page(PageParam pageParam, SysPerm sysPerm);
}
