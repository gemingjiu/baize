package com.gem.baize.system.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.system.user.mapper.SysUserMapper;
import com.gem.baize.system.user.po.SysUserPo;
import com.gem.baize.system.user.service.SysUserService;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.dto.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Override
    public SysUserPo getById(String id) {
        return Optional.ofNullable(sysUserMapper.selectById(id)).orElseThrow(() -> new NotFoundException("用户不存在"));
    }

    @Override
    public Integer create(SysUserPo sysUserPo) {
        int result = sysUserMapper.insert(sysUserPo);
        if (result <= 0) {
            throw new DataCreationException("用户创建失败");
        }
        return result;
    }

    @Override
    public void update(SysUserPo sysUserPo) {
        int affectedRows = sysUserMapper.updateById(sysUserPo);

        if (affectedRows <= 0) {
            throw new NotFoundException("用户信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("用户信息更新异常，影响了多条记录");
        }
    }

    @Override
    public void deleteById(String id) {
        int affectedRows = sysUserMapper.deleteById(id);
        if (affectedRows <= 0) {
            throw new NotFoundException("用户信息删除失败，可能记录不存在");
        }
        if (affectedRows > 1) {
            throw new IntegrityViolationException("用户信息删除异常，影响了多条记录");
        }
    }

    @Override
    public Page<SysUserPo> page(PageParam pageParam, SysUserPo sysUserPo) {
        return Optional.ofNullable(sysUserMapper.selectPage(pageParam, sysUserPo))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到用户信息"));
    }
}
