package com.gem.baize.system.perm.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.system.perm.entity.SysPerm;
import com.gem.baize.system.perm.mapper.SysPermMapper;
import com.gem.baize.system.perm.service.SysPermService;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * 权限服务类实现
 */
@Service
public class SysPermServiceImpl extends ServiceImpl<SysPermMapper, SysPerm> implements SysPermService {

    @Autowired
    private SysPermMapper sysPermMapper;

    @Override
    public SysPerm getById(String id) {
        return Optional.ofNullable(sysPermMapper.selectById(id)).orElseThrow(() -> new NotFoundException("权限不存在"));

    }

    @Override
    public Integer create(SysPerm sysPerm) {
        int result = sysPermMapper.insert(sysPerm);
        if (result <= 0) {
            throw new DataCreationException("权限创建失败");
        }
        return result;
    }

    @Override
    public void update(SysPerm sysPerm) {
        int affectedRows = sysPermMapper.updateById(sysPerm);

        if (affectedRows <= 0) {
            throw new NotFoundException("权限信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("权限信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteById(String id) {
        int affectedRows = sysPermMapper.deleteById(id);
        if (affectedRows <= 0) {
            throw new NotFoundException("权限信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("权限信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<SysPerm> page(PageParam pageParam, SysPerm sysPerm) {
        return Optional.ofNullable(sysPermMapper.selectPage(pageParam, sysPerm))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到权限信息"));
    }
}
