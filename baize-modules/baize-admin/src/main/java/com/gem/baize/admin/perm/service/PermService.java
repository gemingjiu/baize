package com.gem.baize.admin.perm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.admin.perm.entity.Perm;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 部门服务类接口
 */
public interface PermService extends IService<Perm> {

    Perm getById(String id);

    Integer create(Perm perm);

    void update(Perm perm);

    void deleteById(String id);

    Page<Perm> page(PageParam pageParam, Perm perm);
}
