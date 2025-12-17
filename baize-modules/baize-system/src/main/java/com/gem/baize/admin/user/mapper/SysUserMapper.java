package com.gem.baize.admin.user.mapper;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.admin.user.po.SysUserPo;
import com.gem.baize.common.core.model.dto.PageParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserPo> {
    default Page<SysUserPo> selectPage(PageParam pageParam, SysUserPo sysUserPo) {
        // 1. 构建分页对象
        Page<SysUserPo> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SysUserPo> wrapper = new LambdaQueryWrapper<>();


        if (StringUtils.isNotBlank(sysUserPo.getStatus())) {
            wrapper.eq(SysUserPo::getStatus, sysUserPo.getStatus());
        }
        if (sysUserPo.getCreatedTime() != null) {
            wrapper.ge(SysUserPo::getCreatedTime, sysUserPo.getCreatedTime());
        }
        // 3. 执行分页查询
        return selectPage(page, wrapper);
    }
}
