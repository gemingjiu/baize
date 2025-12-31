package com.gem.baize.system.user.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gem.baize.api.system.user.domain.dto.SysUserDto;
import com.gem.baize.common.core.exception.model.DataCreationException;
import com.gem.baize.common.core.exception.model.DuplicateException;
import com.gem.baize.common.core.exception.model.IntegrityViolationException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.system.user.entity.SysUser;
import com.gem.baize.system.user.mapper.SysUserMapper;
import com.gem.baize.system.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserConvert sysUserConvert;

    @Override
    @Cacheable(cacheNames = "sys_user",key ="#id")
    public SysUserDto getById(String id) {
        SysUser sysUser = Optional.ofNullable(sysUserMapper.selectById(id)).orElseThrow(() -> new NotFoundException("用户不存在"));
        return sysUserConvert.toDto(sysUser);
    }


    @Override
    @Transactional
    public Integer create(SysUserDto sysUserDto) {
        SysUser sysUser = sysUserConvert.toEntity(sysUserDto);

        boolean exists = sysUserMapper.exists(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUserName, sysUserDto.getUserName()));
        if (exists) {
            throw new DuplicateException("用户名称已存在，请更换后重试");
        }

        int result = sysUserMapper.insert(sysUser);
        if (result <= 0) {
            throw new DataCreationException("用户创建失败");
        }
        return result;
    }


    @Override
    @Transactional
    @CachePut(cacheNames = "sys_user", key = "#sysUserDto.id")
    public void updateById(SysUserDto sysUserDto) {
        SysUser sysUser = sysUserConvert.toEntity(sysUserDto);

        boolean exists = sysUserMapper.exists(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUserName, sysUserDto.getUserName()));
        if (!exists) {
            throw new DuplicateException("用户名称不存在，请更换后重试");
        }
        int affectedRows = sysUserMapper.updateById(sysUser);

        if (affectedRows <= 0) {
            throw new NotFoundException("用户信息更新失败，记录不存在");
        }

        if (affectedRows > 1) {
            throw new IntegrityViolationException("用户信息更新异常，影响了多条记录");
        }
    }

    @Override
    @CacheEvict(cacheNames = "sys_user",key ="#id")
    public void removeById(String id) {
        super.removeById(id);
    }


    @Override
    public Page<SysUserDto> page(Page<SysUser> page, SysUserDto sysUserDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 默认排序
        wrapper.orderByAsc(SysUser::getSort);

        // 动态条件查询
        if (StringUtils.isNotBlank(sysUserDto.getUserName())) {
            wrapper.like(SysUser::getUserName, sysUserDto.getUserName());
        }
        if (StringUtils.isNotBlank(sysUserDto.getEmail())) {
            wrapper.like(SysUser::getEmail, sysUserDto.getEmail());
        }
        if (StringUtils.isNotBlank(sysUserDto.getPhone())) {
            wrapper.eq(SysUser::getPhone, sysUserDto.getPhone());
        }

        Page<SysUser> sysUserPage = Optional.ofNullable(sysUserMapper.selectPage(page, wrapper))
                .filter(p -> !CollectionUtils.isEmpty(p.getRecords()))
                .orElseThrow(() -> new NotFoundException("未找到用户信息"));
        return sysUserConvert.toDtoPage(sysUserPage);
    }
}
