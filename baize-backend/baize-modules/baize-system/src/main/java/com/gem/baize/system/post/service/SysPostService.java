package com.gem.baize.system.post.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.post.domain.dto.SysPostDto;
import com.gem.baize.system.post.entity.SysPost;

/**
 * 部门服务类接口
 */
public interface SysPostService extends IService<SysPost> {

    SysPostDto getById(String id);

    void create(SysPostDto sysPostDto);

    void updateById(SysPostDto sysPostDto);

    void removeById(String id);

    Page<SysPostDto> page(Page<SysPost> page, SysPostDto sysPostDto);
}
