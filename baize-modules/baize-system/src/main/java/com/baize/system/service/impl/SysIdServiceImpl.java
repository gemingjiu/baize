package com.baize.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baize.common.core.utils.uuid.UUID;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.domain.SysId;
import com.baize.system.mapper.SysIdMapper;
import com.baize.system.service.ISysIdService;

/**
 * 自增ID生成Service业务层处理
 *
 * @author baize
 * @date 2024-05-13
 */
@Service
public class SysIdServiceImpl implements ISysIdService {
    @Autowired
    private SysIdMapper sysIdMapper;
    private void initSysId(SysId info) {
        info.setId(UUID.fastUUID().toString(true));
        info.setCreatedBy(SecurityUtils.getLoginUser().getUsername());
        info.setModifiedBy(SecurityUtils.getLoginUser().getUsername());
    }

    private void modifySysId(SysId info) {
        info.setModifiedBy(SecurityUtils.getLoginUser().getUsername());
    }
    /**
     * 查询自增ID生成
     *
     * @param id 自增ID生成主键
     * @return 自增ID生成
     */
    @Override
    public SysId selectSysIdById(String id) {
        return sysIdMapper.selectSysIdById(id);
    }

    /**
     * 查询自增ID生成列表
     *
     * @param sysId 自增ID生成
     * @return 自增ID生成
     */
    @Override
    public List<SysId> selectSysIdList(SysId sysId) {
        return sysIdMapper.selectSysIdList(sysId);
    }

    /**
     * 新增自增ID生成
     *
     * @param sysId 自增ID生成
     * @return 结果
     */
    @Override
    public int insertSysId(SysId sysId) {
        initSysId(sysId);
        return sysIdMapper.insertSysId(sysId);
    }

    /**
     * 修改自增ID生成
     *
     * @param sysId 自增ID生成
     * @return 结果
     */
    @Override
    public int updateSysId(SysId sysId) {
        return sysIdMapper.updateSysId(sysId);
    }

    /**
     * 批量删除自增ID生成
     *
     * @param ids 需要删除的自增ID生成主键
     * @return 结果
     */
    @Override
    public int deleteSysIdByIds(String[] ids) {
        return sysIdMapper.deleteSysIdByIds(ids);
    }

    /**
     * 删除自增ID生成信息
     *
     * @param id 自增ID生成主键
     * @return 结果
     */
    @Override
    public int deleteSysIdById(String id) {
        return sysIdMapper.deleteSysIdById(id);
    }

    @Override
    public int updateSysIdStatus(SysId sysId) {
        return sysIdMapper.updateSysIdStatus(sysId);
    }
}