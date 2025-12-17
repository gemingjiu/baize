package com.gem.baize.admin.menu.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.admin.menu.entity.Menu;
import com.gem.baize.admin.menu.mapper.MenuMapper;
import com.gem.baize.admin.menu.service.MenuService;
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
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public Menu getById(String id) {
        return Optional.ofNullable(menuMapper.selectById(id)).orElseThrow(() -> new NotFoundException("部门不存在"));
    }

    @Override
    public Integer create(Menu menu) {
        int result = menuMapper.insert(menu);
        if (result <= 0) {
            throw new DataCreationException("部门创建失败");
        }
        return result;
    }

    @Override
    public void update(Menu menu) {
        int affectedRows = menuMapper.updateById(menu);

        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteById(String id) {
        int affectedRows = menuMapper.deleteById(id);
        if (affectedRows <= 0) {
            throw new NotFoundException("部门信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("部门信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<Menu> page(PageParam pageParam, Menu menu) {
        return Optional.ofNullable(menuMapper.selectPage(pageParam, menu))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到部门信息"));
    }
}
