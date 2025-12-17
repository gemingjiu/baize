package com.gem.baize.admin.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.user.po.SysUserPo;
import com.gem.baize.common.core.model.dto.PageParam;

public interface SysUserService {
    SysUserPo getById(String id);

    Integer create(SysUserPo sysUserPo);

    void update(SysUserPo sysUserPo);

    void deleteById(String id);

    Page<SysUserPo> page(PageParam pageParam, SysUserPo sysUserPo);
}
