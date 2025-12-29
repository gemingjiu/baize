package com.gem.baize.system.perm.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.perm.domain.dto.SysPermDto;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.perm.entity.SysPerm;
import com.gem.baize.system.perm.mapper.SysPermMapper;
import com.gem.baize.system.perm.service.SysPermService;
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

    @Autowired
    private SysPermConvert sysPermConvert;

    @Override
    public SysPermDto getById(String id) {
        SysPerm sysPerm = Optional.ofNullable(sysPermMapper.selectById(id)).orElseThrow(() -> new NotFoundException("权限不存在"));
        return sysPermConvert.toDto(sysPerm);

    }

    @Override
    public Integer create(SysPermDto sysPermDto) {
        SysPerm sysPerm = sysPermConvert.toEntity(sysPermDto);

        boolean exists = sysPermMapper.exists(Wrappers.<SysPerm>lambdaQuery()
                .eq(SysPerm::getPermCode, sysPermDto.getPermCode()));
        if (exists) {
            throw new DuplicateException("权限编码已存在，请更换后重试");
        }
        int result = sysPermMapper.insert(sysPerm);
        if (result <= 0) {
            throw new DataCreationException("权限创建失败");
        }
        return result;
    }

    @Override
    public void update(SysPermDto sysPermDto) {
        SysPerm sysPerm = sysPermConvert.toEntity(sysPermDto);

        boolean exists = sysPermMapper.exists(Wrappers.<SysPerm>lambdaQuery()
                .eq(SysPerm::getPermCode, sysPermDto.getPermCode()));
        if (!exists) {
            throw new DuplicateException("权限编码不存在，请更换后重试");
        }
        int affectedRows = sysPermMapper.updateById(sysPerm);

        if (affectedRows <= 0) {
            throw new NotFoundException("权限信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("权限信息更新异常，影响了多条记录");
        }
    }


    @Override
    public Page<SysPermDto> page(Page<SysPerm> page, SysPermDto sysPermDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysPerm> wrapper = new LambdaQueryWrapper<>();

        // 默认排序
        wrapper.orderByAsc(SysPerm::getSort);

        // 动态条件查询
        if (StringUtils.isNotBlank(sysPermDto.getPermCode())) {
            wrapper.like(SysPerm::getPermCode, sysPermDto.getPermCode());
        }
        if (StringUtils.isNotBlank(sysPermDto.getPermName())) {
            wrapper.like(SysPerm::getPermName, sysPermDto.getPermName());
        }
        if (StringUtils.isNotBlank(sysPermDto.getParentId())) {
            wrapper.eq(SysPerm::getParentId, sysPermDto.getParentId());
        }

        Page<SysPerm> sysPermPage = Optional.ofNullable(sysPermMapper.selectPage(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到授权信息"));
        return sysPermConvert.toDtoPage(sysPermPage);
    }
}
