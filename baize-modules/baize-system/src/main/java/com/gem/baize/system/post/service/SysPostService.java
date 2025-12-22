package com.gem.baize.system.post.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.system.post.entity.SysPost;
import com.gem.baize.common.core.model.dto.PageParam;

/**
 * 部门服务类接口
 */
public interface SysPostService extends IService<SysPost> {

    SysPost getById(String id);

    Integer create(SysPost sysPost);

    void update(SysPost sysPost);

    void deleteById(String id);

    Page<SysPost> page(PageParam pageParam, SysPost sysPost);
}
