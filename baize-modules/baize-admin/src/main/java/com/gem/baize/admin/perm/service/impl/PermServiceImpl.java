package com.gem.baize.admin.perm.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.admin.perm.entity.Perm;
import com.gem.baize.admin.perm.mapper.PermMapper;
import com.gem.baize.admin.perm.service.PermService;
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
public class PermServiceImpl extends ServiceImpl<PermMapper, Perm> implements PermService {

    @Autowired
    private PermMapper permMapper;

    @Override
    public Perm getByBizId(String bizId) {
        return Optional.ofNullable(permMapper.selectByBizId(bizId)).orElseThrow(() -> new NotFoundException("权限不存在"));

    }

    @Override
    public Integer create(Perm perm) {
        int result = permMapper.insert(perm);
        if (result <= 0) {
            throw new DataCreationException("权限创建失败");
        }
        return result;
    }

    @Override
    public void update(Perm perm) {
        int affectedRows = permMapper.updateByBizId(perm);

        if (affectedRows <= 0) {
            throw new NotFoundException("权限信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("权限信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteByBizId(String bizId) {
        int affectedRows = permMapper.deleteByBizId(bizId);
        if (affectedRows <= 0) {
            throw new NotFoundException("权限信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("权限信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<Perm> page(PageParam pageParam, Perm perm) {
        return Optional.ofNullable(permMapper.selectPage(pageParam, perm))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到权限信息"));
    }
}
