package com.baize.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baize.common.core.utils.uuid.UUID;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.domain.SysIdGen;
import com.baize.system.mapper.SysIdGenMapper;
import com.baize.system.service.ISysIdGenService;

/**
 * 自增ID生成Service业务层处理
 *
 * @author baize
 * @date 2024-05-13
 */
@Service
public class SysIdGenServiceImpl implements ISysIdGenService {
    @Autowired
    private SysIdGenMapper sysIdGenMapper;
    private void initSysIdGen(SysIdGen info) {
        info.setId(UUID.fastUUID().toString(true));
        info.setCreatedBy(SecurityUtils.getLoginUser().getUsername());
        info.setModifiedBy(SecurityUtils.getLoginUser().getUsername());
    }

    private void modifySysIdGen(SysIdGen info) {
        info.setModifiedBy(SecurityUtils.getLoginUser().getUsername());
    }
    /**
     * 查询自增ID生成
     *
     * @param id 自增ID生成主键
     * @return 自增ID生成
     */
    @Override
    public SysIdGen selectSysIdGenById(String id) {
        return sysIdGenMapper.selectSysIdGenById(id);
    }

    /**
     * 查询自增ID生成列表
     *
     * @param sysIdGen 自增ID生成
     * @return 自增ID生成
     */
    @Override
    public List<SysIdGen> selectSysIdGenList(SysIdGen sysIdGen) {
        return sysIdGenMapper.selectSysIdGenList(sysIdGen);
    }

    /**
     * 新增自增ID生成
     *
     * @param sysIdGen 自增ID生成
     * @return 结果
     */
    @Override
    public int insertSysIdGen(SysIdGen sysIdGen) {
        initSysIdGen(sysIdGen);
        return sysIdGenMapper.insertSysIdGen(sysIdGen);
    }

    /**
     * 修改自增ID生成
     *
     * @param sysIdGen 自增ID生成
     * @return 结果
     */
    @Override
    public int updateSysIdGen(SysIdGen sysIdGen) {
        return sysIdGenMapper.updateSysIdGen(sysIdGen);
    }

    /**
     * 批量删除自增ID生成
     *
     * @param ids 需要删除的自增ID生成主键
     * @return 结果
     */
    @Override
    public int deleteSysIdGenByIds(String[] ids) {
        return sysIdGenMapper.deleteSysIdGenByIds(ids);
    }

    /**
     * 删除自增ID生成信息
     *
     * @param id 自增ID生成主键
     * @return 结果
     */
    @Override
    public int deleteSysIdGenById(String id) {
        return sysIdGenMapper.deleteSysIdGenById(id);
    }

    @Override
    public int updateSysIdGenStatus(SysIdGen sysIdGen) {
        return sysIdGenMapper.updateSysIdGenStatus(sysIdGen);
    }
}