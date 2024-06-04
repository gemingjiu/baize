package com.baize.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baize.system.domain.SysIdGenerator;
import com.baize.system.mapper.SysIdGeneratorMapper;
import com.baize.system.service.ISysIdGeneratorService;

/**
 * 自增ID生成Service业务层处理
 *
 * @author baize
 * @date 2024-05-13
 */
@Service
public class SysIdGeneratorServiceImpl implements ISysIdGeneratorService {
    @Autowired
    private SysIdGeneratorMapper sysIdGeneratorMapper;

    /**
     * 查询自增ID生成
     *
     * @param id 自增ID生成主键
     * @return 自增ID生成
     */
    @Override
    public SysIdGenerator selectSysIdGeneratorById(String id) {
        return sysIdGeneratorMapper.selectSysIdGeneratorById(id);
    }

    /**
     * 查询自增ID生成列表
     *
     * @param sysIdGenerator 自增ID生成
     * @return 自增ID生成
     */
    @Override
    public List<SysIdGenerator> selectSysIdGeneratorList(SysIdGenerator sysIdGenerator) {
        return sysIdGeneratorMapper.selectSysIdGeneratorList(sysIdGenerator);
    }

    /**
     * 新增自增ID生成
     *
     * @param sysIdGenerator 自增ID生成
     * @return 结果
     */
    @Override
    public int insertSysIdGenerator(SysIdGenerator sysIdGenerator) {
            return sysIdGeneratorMapper.insertSysIdGenerator(sysIdGenerator);
    }

    /**
     * 修改自增ID生成
     *
     * @param sysIdGenerator 自增ID生成
     * @return 结果
     */
    @Override
    public int updateSysIdGenerator(SysIdGenerator sysIdGenerator) {
        return sysIdGeneratorMapper.updateSysIdGenerator(sysIdGenerator);
    }

    /**
     * 批量删除自增ID生成
     *
     * @param ids 需要删除的自增ID生成主键
     * @return 结果
     */
    @Override
    public int deleteSysIdGeneratorByIds(String[] ids) {
        return sysIdGeneratorMapper.deleteSysIdGeneratorByIds(ids);
    }

    /**
     * 删除自增ID生成信息
     *
     * @param id 自增ID生成主键
     * @return 结果
     */
    @Override
    public int deleteSysIdGeneratorById(String id) {
        return sysIdGeneratorMapper.deleteSysIdGeneratorById(id);
    }
}