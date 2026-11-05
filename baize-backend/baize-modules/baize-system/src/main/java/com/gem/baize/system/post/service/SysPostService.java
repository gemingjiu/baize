package com.gem.baize.system.post.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.post.domain.dto.SysPostDto;
import com.gem.baize.system.post.entity.SysPost;

import java.util.List;

/**
 * 部门服务类接口
 */
public interface SysPostService extends IService<SysPost> {

    SysPostDto getById(String id);

    void create(SysPostDto sysPostDto);

    void updateById(SysPostDto sysPostDto);

    void removeById(String id);

    Page<SysPostDto> page(Page<SysPost> page, SysPostDto sysPostDto);

    /**
     * 启用中的岗位列表（2026-11-05 P6：用户表单岗位多选数据源，登录即可见）。
     */
    List<SysPostDto> listAvailable();
}
