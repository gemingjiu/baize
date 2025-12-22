package com.gem.baize.system.menu.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.system.menu.entity.SysMenu;
import com.gem.baize.system.menu.mapper.SysMenuMapper;
import com.gem.baize.system.menu.service.SysMenuService;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 部门服务类实现
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public SysMenu getById(String id) {
        return Optional.ofNullable(sysMenuMapper.selectById(id)).orElseThrow(() -> new NotFoundException("部门不存在"));
    }

    @Override
    public Integer create(SysMenu sysMenu) {
        int result = sysMenuMapper.insert(sysMenu);
        if (result <= 0) {
            throw new DataCreationException("部门创建失败");
        }
        return result;
    }

    @Override
    public void update(SysMenu sysMenu) {
        int affectedRows = sysMenuMapper.updateById(sysMenu);

        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteById(String id) {
        int affectedRows = sysMenuMapper.deleteById(id);
        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<SysMenu> page(PageParam pageParam, SysMenu sysMenu) {
        return Optional.ofNullable(sysMenuMapper.selectPage(pageParam, sysMenu))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到部门信息"));
    }
}
